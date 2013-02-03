package org.shirdrn.tinyframework.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.RunningTinyJob.JobStatus;
import org.shirdrn.tinyframework.core.conf.Context;
import org.shirdrn.tinyframework.core.conf.ReadableContext;
import org.shirdrn.tinyframework.core.conf.TaskConf;
import org.shirdrn.tinyframework.core.constants.RunnerMode;
import org.shirdrn.tinyframework.core.executor.DefaultTinyExecutorManager;
import org.shirdrn.tinyframework.core.executor.SinglePoolReadable;
import org.shirdrn.tinyframework.core.executor.TinyExecutorManager;

public abstract class TinyJobRunner<T extends TinyTask, J extends TinyJob<T>> extends Thread 
		implements JobRunner, TinyTaskProtocol<T, J>, TinyJobProtocol<T, J> {

	private static final Log LOG = LogFactory.getLog(TinyJobRunner.class);
	private volatile boolean running = false;
	protected RunnerMode runnerMode = RunnerMode.PRODUCTION;
	protected final TinyExecutorManager<SinglePoolReadable> executorManager;
	protected final JobQueue jobQueue = new JobQueue();
	private static final ReadableContext coreContext;
	
	static {
		coreContext = new Context(false);
	}
	
	@SuppressWarnings("unchecked")
	protected TinyJobRunner() {
		super();
		setName("RUNNER");
		executorManager = (TinyExecutorManager<SinglePoolReadable>) DefaultTinyExecutorManager.getInstance();
		final Context context = (Context) coreContext;

		// configure a job runner
		configure(context);
	}
	
	class JobQueue {
		private final Map<Job<? extends Task>, RunningTinyJob<T, J>> runningJobs = 
				new HashMap<Job<? extends Task>, RunningTinyJob<T, J>>();
		private final Map<Job<? extends Task>, RunningTinyJob<T, J>> waitingJobs = 
				new HashMap<Job<? extends Task>, RunningTinyJob<T, J>>();
		private final Map<Job<? extends Task>, RunningTinyJob<T, J>> completedJobs = 
				new HashMap<Job<? extends Task>, RunningTinyJob<T, J>>();
		private SignalLock lock = new SignalLock();
	}
	
	protected abstract void configure(final Context context);

	static enum Signal {
		NO_ACTION,
		CHECK_WAITING_JOBS_QUEUE,
		CHECK_RUNNING_JOBS_QUEUE,
	}
	
	class SignalLock {
		private Signal checkingWaitingJobsQueue;
		private Signal checkingRunningJobsQueue;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				synchronized(jobQueue.lock) {
					jobQueue.lock.wait();
					checkSignal();
				}
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkSignal() {
		if(jobQueue.lock.checkingWaitingJobsQueue == Signal.CHECK_WAITING_JOBS_QUEUE) {
			checkWaitingJobQueue();
		} else if(jobQueue.lock.checkingRunningJobsQueue == Signal.CHECK_RUNNING_JOBS_QUEUE) {
			checkRunningJobsQueue();
		}
	}

	private void checkRunningJobsQueue() {
		LOG.info("Receive a notification: " + jobQueue.lock.checkingRunningJobsQueue);
		LOG.info("Check running jobs queue...");
		synchronized(jobQueue) {
			if(!jobQueue.runningJobs.isEmpty()) {
				Iterator<Entry<Job<? extends Task>, RunningTinyJob<T, J>>> iter = 
						jobQueue.runningJobs.entrySet().iterator();
				while(iter.hasNext()) {
					Entry<Job<? extends Task>, RunningTinyJob<T, J>> entry = iter.next();
					RunningTinyJob<T, J> job = entry.getValue();
					if(job.getJobStatus() == JobStatus.SUCCESS 
							|| job.getJobStatus() == JobStatus.FAILURE) {
						iter.remove();
						jobQueue.completedJobs.put(job.getTinyJob(), job);
						LOG.info("Move to completed jobs queue: JOB[" + job.getTinyJob().getName() + "]");
					} else {
						LOG.warn("Invalid job status: JOB[" + job.getTinyJob().getName() + "," + job.getJobStatus() + "]");
						// need to be executed again?
					}
				}
			}
		}
		// clear signal
		jobQueue.lock.checkingRunningJobsQueue = Signal.NO_ACTION;
	}

	private void checkWaitingJobQueue() {
		LOG.info("Receive a notification: " + jobQueue.lock.checkingWaitingJobsQueue);
		LOG.info("Check waiting jobs queue...");
		synchronized(jobQueue) {
			if(!jobQueue.waitingJobs.isEmpty()) {
				Iterator<Entry<Job<? extends Task>, RunningTinyJob<T, J>>> iter = 
						jobQueue.waitingJobs.entrySet().iterator();
				while(iter.hasNext()) {
					Entry<Job<? extends Task>, RunningTinyJob<T, J>> entry = iter.next();
					RunningTinyJob<T, J> job = entry.getValue();
					job.startThread();
					jobQueue.runningJobs.put(job.getTinyJob(), job);
					iter.remove();
					LOG.info("Move to running jobs queue:JOB[" + job.getTinyJob().getName() + "]");
				}
				// clear signal
				jobQueue.lock.checkingWaitingJobsQueue = Signal.NO_ACTION;
			}
			
		}
	}
	
	@Override
	public RunningTinyJob<T, J> submitJob(final TinyJobClient jobClient, J tinyJob) throws TinyJobException {
		RunningTinyJob<T, J> runningTinyJob = null;
		synchronized(jobQueue.lock) {
			jobQueue.lock.checkingWaitingJobsQueue = Signal.CHECK_WAITING_JOBS_QUEUE;
			// notify this runner to check the submitted tiny job to run
			synchronized(jobQueue) {
				runningTinyJob = checkJob(jobClient, tinyJob);
			}
			jobQueue.lock.notify();
			LOG.info("Notify this job runner to check waiting jobs queue.");
		}
		return runningTinyJob;
	}
	
	private RunningTinyJob<T, J> checkJob(final TinyJobClient jobClient, final J tinyJob) {
		if(!jobQueue.waitingJobs.containsKey(tinyJob) 
				&& !jobQueue.runningJobs.containsKey(tinyJob)) {
			RunningTinyJob<T, J> runningJob = new RunningTinyJob<T, J>(this, tinyJob);
			runningJob.createReusableTaskInstances();
			runningJob.setJobClient(jobClient);
			jobQueue.waitingJobs.put(tinyJob, runningJob);
			runningJob.changeToSubmittedJobStatus();
			LOG.info("Add to waiting jobs queue:JOB[" + tinyJob.getName() + "]");
			return runningJob;
		} else {
			throw new RuntimeException("Job has already been submitted before!");
		}
	}
	
	public void notifyJobCompletion(final J tinyJob) {
		synchronized(jobQueue.lock) {
			// check running jobs queue
			synchronized(jobQueue) {
				if(jobQueue.runningJobs.containsKey(tinyJob)) {
					RunningTinyJob<T, J> runningJob = jobQueue.runningJobs.remove(tinyJob); 
					jobQueue.completedJobs.put(tinyJob, runningJob);
				}
			}
		}
	}
	
	@Override
	public void submitTask(final J tinyJob, String taskName, TaskConf taskConf) throws TinyTaskException {
		RunningTinyJob<T, J> runningJob = getJob(tinyJob);
		if(runningJob != null) {
			runningJob.fireTinyTaskHolder(taskName, taskConf);
		} else {
			throw new RuntimeException("Job doesn't exist:" + tinyJob);
		}
	}

	private RunningTinyJob<T, J> getJob(final J tinyJob) {
		RunningTinyJob<T, J> runningJob = null;
		if(jobQueue.waitingJobs.containsKey(tinyJob)) {
			runningJob = jobQueue.waitingJobs.get(tinyJob);
		} else {
			runningJob = jobQueue.runningJobs.get(tinyJob);
		}
		return runningJob;
	}

	@Override
	public void startRunner() {
		synchronized(this) {
			if(!running) {
				running = true;
				this.setDaemon(true);
				this.start();
				LOG.info("Tiny job runner is started!");
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public RunnerMode getRunnerMode() {
		return runnerMode;
	}
	
	public static ReadableContext getCoreContext() {
		return coreContext;
	}

	@Override
	public TinyExecutorManager<SinglePoolReadable> getExecutorManager() {
		return executorManager;
	}

	public boolean isRunning() {
		return running;
	}

}
