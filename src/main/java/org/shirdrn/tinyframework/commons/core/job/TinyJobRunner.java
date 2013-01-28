package org.shirdrn.tinyframework.commons.core.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.HookService;
import org.shirdrn.tinyframework.commons.core.SequenceGenerator;
import org.shirdrn.tinyframework.commons.core.SequenceGeneratorFactory;
import org.shirdrn.tinyframework.commons.core.conf.Configured;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.conf.TaskConf;
import org.shirdrn.tinyframework.commons.core.counter.DefaultTinyCounter;
import org.shirdrn.tinyframework.commons.core.counter.TinyCounter;
import org.shirdrn.tinyframework.commons.core.executor.SinglePoolReadable;
import org.shirdrn.tinyframework.commons.core.executor.TinyExecutorManager;
import org.shirdrn.tinyframework.commons.core.utils.ObjectFactory;


/**
 * Abstract tiny job runner who is responsible for supporting the basic framework process.
 * The main driver class {@link TinyJobClient} can invoke a {@link TinyJobRunner} instance
 * controlled by end user(for example, a developer). Usually it contains basic logics
 * of one ore more {@link T extends TinyTask} instances running.
 * 
 * @author Yanjun
 */
public abstract class TinyJobRunner<T extends TinyTask> extends Configured
		implements HookService, TinyRunner<T> {

	private static final Log LOG = LogFactory.getLog(TinyJobRunner.class);
	private volatile boolean doneIterate = false;
	protected final ReadableContext readableContext;
	
	protected SequenceGenerator sequenceGenerator;
	protected final TinyCounter counter = new DefaultTinyCounter();
	protected TinyExecutorManager<SinglePoolReadable> executorManager;
	
	protected final LinkedHashMap<String, Class<T>> taskClasses = new LinkedHashMap<String, Class<T>>();
	protected final LinkedHashMap<Class<T>, String> taskMappings = new LinkedHashMap<Class<T>, String>();
	protected final Map<Class<T>, List<RunningTask<T>>> taskInstances = new HashMap<Class<T>, List<RunningTask<T>>>();
	
	private final Object releaseLock = new Object();
	
	public TinyJobRunner(ReadableContext readableContext) {
		super();
		this.readableContext = readableContext;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		// configure tiny executor manager
		String tinyExecutorManagerClassName = readableContext.get(
				"commons.core.executor.manager.class", 
				"org.shirdrn.tinyframework.commons.core.executor.SinglePoolTinyExecutorManager");
		executorManager = (TinyExecutorManager<SinglePoolReadable>) 
				ObjectFactory.getInstance(tinyExecutorManagerClassName, TinyExecutorManager.class, this.getClass().getClassLoader());
		
		// create a sequence generator
		SequenceGeneratorFactory.createFor(this);
		sequenceGenerator = SequenceGeneratorFactory.getSequenceGenerator(this);
				
		// Add shutdown hook to output statistics information, whatever
		// this program exits JVM by any means
		Thread hook = new HookThread();
		hook.setName("SHUTDOWN-HOOK");
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	@Override
	public void hook() {
		// Output counter and statistics information before JVM exits.
		LOG.info("Final TinyTask counter;" +
				"successTinyTaskCount=" + counter.getSuccessTinyTaskCount() + "," +
				"failureTinyTaskCount=" + counter.getFailureTinyTaskCount() + "," +
				"totalTinyTaskCount=" + (counter.getSuccessTinyTaskCount() + counter.getFailureTinyTaskCount()));
		LOG.info("Final counter;" +
				"successCount=" + counter.getSuccessCount() + "," +
				"failureCount=" + counter.getFailuerCount() + "," +
				"totalCount=" + counter.getTotalCount());
	}
	
	/**
	 * Shutdown hook thread.
	 * 
	 * @author Yanjun
	 */
	class HookThread extends Thread {
		@Override
		public void run() {
			hook();
		}
	}
	
	private boolean isDoneIterate() {
		return doneIterate;
	}
	
	/**
	 * Register {@link T extends TinyTask} classes by repeatedly invoking this method.
	 * @param taskId
	 * @param taskClass
	 */
	public synchronized void registerTask(String taskId, Class<T> taskClass) {
		if(!taskClasses.containsValue(taskClass)) {
			LOG.debug("Register tiny task;taskClass=" + taskClass.getName());
			taskClasses.put(taskId, taskClass);
			taskMappings.put(taskClass, taskId);
		} else {
			LOG.warn("Ignore existed TinyTask;taskClass=" + taskClass.getName());
		}
	}
	
	@Override
	public void submitTask(final String taskName, final TaskConf taskConf) throws TinyTaskException {
		// accumulate the count for each iterated item submitted
		counter.incTotalCount();
		
		// Internal basic worker definition, defining basic controlling and 
		// processing logics, meanwhile including log basic running information.
		executorManager.getWorkerPool().execute(new Runnable() {
			@Override
			public void run() {
				String name = taskName;
				if(name == null) {
					name = "";
				}
				fireTaskGroupHolder(name, taskConf);				
			}
			
		});
	}
	
	@Override
	public void fireTaskGroupHolder(final String taskName, final TaskConf taskConf) {
		// create a TinyTaskGroupHolder instance
		TinyTaskGroupHolder<T> taskGroupHolder = new GenericTinyTaskGroupHolder<T>(taskConf,this);
		taskGroupHolder.setName(taskName);
		// execute task group runner instance
		try {
			taskGroupHolder.startHolder();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			// print TinyTaskGroupRunner's log information
			long seqNo = sequenceGenerator.next();
			LOG.info(taskGroupHolder.toString(seqNo));
			if(taskGroupHolder.status==TinyTaskGroupHolder.HolderStatus.SUCCESS) {
				counter.incSuccessCount();
			} else {
				counter.incFailureCount();
			}
			// increment counter
			counter.incCurrentCount();
			if(isDoneIterate() && counter.getTotalCount() !=0 
					&& counter.getCurrentCount() == counter.getTotalCount()) {
				synchronized(releaseLock) {
					releaseLock.notify();
				}
			}
		}		
	}
	
	@Override
    public void startRunner() {
    	// initialize reusable task instances
    	// and avoid bad performance when reflecting 
    	SinglePoolReadable readable = executorManager.getReadableExecutorManager();
    	for(int i=0; i<readable.getMaximumPoolSize(); i++) {
    		for(int j=0; j<taskClasses.size(); j++) {
    			Class<T> taskClass = taskClasses.get(j);
    			T task = ObjectFactory.getInstance(taskClasses.get(j));
    			task.setTaskId(taskMappings.get(taskClass));
    			TaskConf taskConf = new TaskConf();
    			task.setTaskConf(taskConf);
    			if(taskInstances.get(taskClass)==null) {
    				taskInstances.put(taskClass, Collections.synchronizedList(new ArrayList<RunningTask<T>>()));
    			}
    			taskInstances.get(taskClass).add(new RunningTask<T>(task, RunningTask.Status.IDLE));
    		}
    	}
    	
    	// iterate the given data source and submit task holding a data unit
    	iterate();
    	LOG.info("Total task count;totalCount=" + counter.getTotalCount());
    	
    	// shutdown thread pool
    	if(counter.getTotalCount() == 0) {
    		LOG.warn("No data to process;totalCount=" + counter.getTotalCount());
		} else {
			// Wait for calling method iterate() completed, and
			// then the totalCount has been computed. It's time to shutdown 
			// thread pool thread depending on totalCount and doneIterate,
			// finally program exits normally.
			doneIterate = true;
			synchronized(releaseLock) {
				try {
					releaseLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
    	executorManager.getWorkerPool().shutdownNow();
    	
    	// release all resources, if necessary
    	releaseAll();
    	LOG.info("Tiny job runner exits normally.");
    }
    
	/**
	 * Abstract method for reading data source and iterate each element.  
	 * It depends on your type of data source, such as static text file, dynamic
	 * fetching pages from remote web host, etc.
	 */
	protected abstract void iterate();
	
	/**
	 * Release all resources we have been using as necessary.
	 */
	protected abstract void releaseAll();
	
	/**
	 * A running tiny task held by a {@link TinyTaskGroupHolder} instance,
	 * and will be started to process.
	 * 
	 * @author Yanjun
	 *
	 * @param <T>
	 */
	public static class RunningTask<T> {
		
		protected T task;
		protected Status status;
		
		public RunningTask(T task, Status status) {
			super();
			this.task = task;
			this.status = status;
		}
		
		protected static enum Status {
			RUNNING,
			IDLE
		}

		@Override
		public String toString() {
			return "[" + task + ", " + status + "]";
		}
	}

	public LinkedHashMap<String, Class<T>> getTaskClasses() {
		return taskClasses;
	}

	public LinkedHashMap<Class<T>, String> getTaskMappings() {
		return taskMappings;
	}

	public Map<Class<T>, List<RunningTask<T>>> getTaskInstances() {
		return taskInstances;
	}

	public TinyCounter getCounter() {
		return counter;
	}

}
