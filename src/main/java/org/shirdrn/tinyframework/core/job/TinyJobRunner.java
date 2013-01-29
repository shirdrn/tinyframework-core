package org.shirdrn.tinyframework.core.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.HookService;
import org.shirdrn.tinyframework.core.SequenceGenerator;
import org.shirdrn.tinyframework.core.SequenceGeneratorFactory;
import org.shirdrn.tinyframework.core.conf.Configurable;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.conf.TaskConf;
import org.shirdrn.tinyframework.core.constants.RunningMode;
import org.shirdrn.tinyframework.core.counter.DefaultTinyCounter;
import org.shirdrn.tinyframework.core.counter.TinyCounter;
import org.shirdrn.tinyframework.core.executor.SinglePoolReadable;
import org.shirdrn.tinyframework.core.executor.TinyExecutorManager;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;


/**
 * Abstract tiny job runner who is responsible for supporting the basic framework process.
 * The main driver class {@link TinyJobClient} can invoke a {@link TinyJobRunner} instance
 * controlled by end user(for example, a developer). Usually it contains basic logics
 * of one ore more {@link T extends TinyTask} instances running.
 * 
 * @author Yanjun
 */
public abstract class TinyJobRunner<T extends TinyTask> implements Configurable, HookService, TinyJob<T>, TinyRunner<T> {

	private static final Log LOG = LogFactory.getLog(TinyJobRunner.class);
	private volatile boolean doneIterate = false;
	private final Object releaseLock = new Object();
	
	protected final RunningMode runningMode;
	protected final JobConf jobConf;
	protected SequenceGenerator sequenceGenerator;
	protected final TinyCounter counter = new DefaultTinyCounter();
	protected TinyExecutorManager<SinglePoolReadable> executorManager;
	
	protected final LinkedHashMap<String, Class<T>> taskClasses = new LinkedHashMap<String, Class<T>>();
	protected final LinkedHashMap<Class<T>, String> taskMappings = new LinkedHashMap<Class<T>, String>();
	protected final Map<Class<T>, List<RunningTask<T>>> taskInstances = new HashMap<Class<T>, List<RunningTask<T>>>();
	
	
	public TinyJobRunner(JobConf jobConf) {
		super();
		this.jobConf = jobConf;
		runningMode = RunningMode.valueOf(
				jobConf.getContext().getInt("core.job.running.mode", RunningMode.PROD.getCode()));
		LOG.info("Job running mode;mode=" + runningMode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure() {
		// configure tiny executor manager
		String tinyExecutorManagerClassName = jobConf.getContext().get(
				"core.job.executor.manager.class", 
				"org.shirdrn.tinyframework.core.executor.SinglePoolTinyExecutorManager");
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
	
	@Override
	public synchronized void registerTask(String taskId, Class<T> taskClass) {
		if(!taskClasses.containsValue(taskClass)) {
			LOG.debug("Register tiny task;taskClass=" + taskClass.getName());
			taskClasses.put(taskId, taskClass);
			taskMappings.put(taskClass, taskId);
		} else {
			LOG.warn("Ignore existed task;taskClass=" + taskClass.getName());
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

	@Override
	public RunningMode getRunningMode() {
		return this.runningMode;
	}
	
	@Override
	public JobConf getJobConf() {
		return jobConf;
	}
	
	@Override
	public LinkedHashMap<String, Class<T>> getTaskClasses() {
		return taskClasses;
	}
	
	@Override
	public String getTaskId(Class<T> taskClass) {
		return taskMappings.get(taskClass);
	}

	public Map<Class<T>, List<RunningTask<T>>> getTaskInstances() {
		return taskInstances;
	}

	public TinyCounter getCounter() {
		return counter;
	}

}
