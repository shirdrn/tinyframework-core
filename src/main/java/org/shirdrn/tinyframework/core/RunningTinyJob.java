package org.shirdrn.tinyframework.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.TinyTaskGroupHolder.HolderStatus;
import org.shirdrn.tinyframework.core.TinyTaskGroupHolder.RunningTask;
import org.shirdrn.tinyframework.core.conf.Configured;
import org.shirdrn.tinyframework.core.conf.TaskConf;
import org.shirdrn.tinyframework.core.constants.KeyName;
import org.shirdrn.tinyframework.core.constants.RunnerMode;
import org.shirdrn.tinyframework.core.counter.DefaultTinyCounter;
import org.shirdrn.tinyframework.core.counter.TinyCounter;
import org.shirdrn.tinyframework.core.executor.TinyExecutor;
import org.shirdrn.tinyframework.core.sequence.SequenceGenerator;
import org.shirdrn.tinyframework.core.sequence.SequenceGeneratorFactory;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;

public class RunningTinyJob<T extends TinyTask, J extends TinyJob<T>> implements Runnable {
	
	private static final Log LOG = LogFactory.getLog(RunningTinyJob.class);
	private final J tinyJob;
	private SequenceGenerator sequenceGenerator;
	private final TinyCounter counter = new DefaultTinyCounter();
	private final TinyJobRunner<T, J> jobRunner;
	private JobStatus jobStatus = JobStatus.INITIALIZED;
	private TinyExecutor<? extends PoolReadable> tinyExecutor;
	private final Map<Class<T>, List<RunningTask<T>>> reusableTaskInstances = 
			new HashMap<Class<T>, List<RunningTask<T>>>();
	private TinyJobClient jobClient;
	private Thread jobThread;
	private volatile boolean doneIterating = false;
	
	@SuppressWarnings("unchecked")
	public RunningTinyJob(final TinyJobRunner<T, J> tinyJobRunner, final J tinyJob) {
		super();
		this.jobRunner = tinyJobRunner;
		this.tinyJob = tinyJob;
		SequenceGeneratorFactory.createFor(tinyJob);
		this.sequenceGenerator = SequenceGeneratorFactory.getSequenceGenerator(tinyJob);
		String tinyExecutorClass = tinyJob.getJobConf().getContext().get(
				"tiny.core.job.executor.class", 
				"org.shirdrn.tinyframework.core.executor.SinglePoolTinyExecutor");
		tinyExecutor = ObjectFactory.getInstance(tinyExecutorClass, TinyExecutor.class, 
				tinyJobRunner.getClass().getClassLoader());
		((Configured) tinyExecutor).setJobConf(tinyJob.getJobConf());
//		tinyExecutor = DefaultTinyExecutorManager.getInstance().retrieve((TinyJob<? extends TinyTask>) tinyJob);
	}
	
	public J getTinyJob() {
		return tinyJob;
	}
	
	public void createReusableTaskInstances() {
		for (int i = 0; i < tinyExecutor.getPoolReadable().getMaximumPoolSize(); i++) {
			for (int j = 0; j < tinyJob.getTaskClassesAsList().size(); j++) {
				Class<T> taskClass = tinyJob.getTaskClassesAsList().get(j);
    			T task = ObjectFactory.getInstance(taskClass);
    			task.setTaskId(tinyJob.getTaskId(taskClass));
    			TaskConf taskConf = new TaskConf();
    			task.setTaskConf(taskConf);
    			if(reusableTaskInstances.get(taskClass)==null) {
    				reusableTaskInstances.put(taskClass, Collections.synchronizedList(new ArrayList<RunningTask<T>>()));
    			}
    			reusableTaskInstances.get(taskClass).add(new RunningTask<T>(task, RunningTask.RunningStatus.IDLE));
			}
		}
	}
	
	public void fireTinyTaskHolder(final String taskName, final TaskConf taskConf) {
		// increate total task count
		counter.incTotalCount();
		tinyExecutor.getWorkerPool().execute(new Runnable() {

			@Override
			public void run() {
				TinyTaskGroupHolder<T> groupHolder = new GenericTinyTaskGroupHolder<T, J>(RunningTinyJob.this, taskConf);
				try {
					groupHolder.setName(taskName);
					groupHolder.startHolder();
					groupHolder.setHolderStatus(HolderStatus.SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					groupHolder.setHolderStatus(HolderStatus.FAILURE);
				} finally {
					// increase current completed task count
					counter.incCurrentCount();
					if(jobRunner.getRunnerMode() == RunnerMode.DEVELOPMENT) {
						long seqNo = sequenceGenerator.next();
						LOG.info(groupHolder.toString(seqNo));
					}
					if(isDoneIterating() 
							&& getCounter().getTotalCount() > 0
							&& getCounter().getCurrentCount() == getCounter().getTotalCount()) {
						// notify running job thread
						synchronized(jobThread) {
							LOG.info("Notify this job thread: RunningJob[" + getTinyJob().getName() + "]");
							jobThread.notify();
						}
					}
				}				
			}
			
		});
	}
	
	public static enum JobStatus {
		INITIALIZED,
		SUBMITTED,
		SUCCESS,
		FAILURE
	}
	
	public void startThread() {
		LOG.info("Start job;name=" + tinyJob.getName());
		jobThread = new Thread(this);
		jobThread.setName("JT" + "[" + tinyJob.getName() + "]");
		jobThread.start();
	}
	
	@Override
	public void run() {
		String jobDatasourceClass = tinyJob.getJobConf().getContext().get("tiny.core.job.datasource.class");
		LOG.info("Data source class: " + jobDatasourceClass);
		if(jobDatasourceClass!=null) {
			@SuppressWarnings("rawtypes")
			TinyDataSource ds = null;
			try {
				ds = ObjectFactory.getInstance(jobDatasourceClass, 
						TinyDataSource.class, tinyJob.getClass().getClassLoader());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ds.setJobConf(tinyJob.getJobConf());
			ds.open();
			// iterate
			LOG.info("Iterate data source...");
			while(ds.getIterator().hasNext()) {
				try {
					Object obj = ds.getIterator().next();
					TaskConf taskConf = new TaskConf();
					taskConf.getContext().setObject(KeyName.OBJECT, obj);
					jobRunner.submitTask(tinyJob, "", taskConf);
				} catch (TinyTaskException e) {
					e.printStackTrace();
				}
			}
			
			// wait tasks finished
			setDoneIterating(true);
			synchronized(jobThread) {
				try {
					jobThread.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ds.close();
			LOG.info("Done to iterate data source.");
			
			// notify job client submitting this tiny job
			LOG.info("Notify job client: " + jobClient + "[" + tinyJob.getName() + "]");
			synchronized(jobClient) {
				jobClient.notify();
				// release all resources, such as stopping thread pool, etc
				releaseAll();
			}
			
			// update job status
			setJobStatus(JobStatus.SUCCESS);
		}
		
	}
	
	public void releaseAll() {
		tinyExecutor.getWorkerPool().shutdown();		
	}
	
	public Map<Class<T>, List<RunningTask<T>>> getReusableTaskInstances() {
		return reusableTaskInstances;
	}

	public TinyCounter getCounter() {
		return counter;
	}

	public void setJobClient(TinyJobClient jobClient) {
		this.jobClient = jobClient;		
	}

	public boolean isDoneIterating() {
		return doneIterating;
	}

	public void setDoneIterating(boolean doneIterating) {
		this.doneIterating = doneIterating;
	}

	private synchronized void setJobStatus(JobStatus jobStatus) {
		if(this.jobStatus != jobStatus) {
			this.jobStatus = jobStatus;
			LOG.info("Job status: " + jobStatus);
			if(jobStatus == JobStatus.SUCCESS 
					|| jobStatus == JobStatus.FAILURE) {
				LOG.info("Job completed:JOB[" + tinyJob.getName() + "]");
			}
		}
	}
	
	public void changeToSubmittedJobStatus() {
		if(this.jobStatus == JobStatus.INITIALIZED) {
			setJobStatus(JobStatus.SUBMITTED);
		}
	}
	
	public JobStatus getJobStatus() {
		return jobStatus;
	}

}
