package org.shirdrn.tinyframework.core.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.PoolReadable;
import org.shirdrn.tinyframework.core.Job;
import org.shirdrn.tinyframework.core.Task;
import org.shirdrn.tinyframework.core.conf.Configured;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;

public class SinglePoolTinyExecutor extends Configured implements TinyExecutor<PoolReadable>, PoolReadable {

	private static final Log LOG = LogFactory.getLog(SinglePoolTinyExecutor.class);
	protected ExecutorService workerPool;
	protected RejectedExecutionHandler rejectedPolicy;
	protected ThreadFactory threadFactory;
	protected BlockingQueue<Runnable> workQueue;
	protected int corePoolSize;
	protected int maximumPoolSize;
	protected int keepAliveTime;
	protected int workQueueSize;
	protected int checkIdleWorkerInterval;
	private Job<? extends Task> tinyJob;
	private TinyExecutorManager<PoolReadable> tinyExecutorManager;
	
	public SinglePoolTinyExecutor() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setJobConf(JobConf jobConf) {
		super.setJobConf(jobConf);
		this.tinyJob = jobConf.getTinyJob();
		
		this.tinyExecutorManager = (TinyExecutorManager<PoolReadable>) DefaultTinyExecutorManager.getInstance();
		this.tinyExecutorManager.registerExecutor(tinyJob, this);
		
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		this.corePoolSize = jobConf.getContext().getInt("tiny.core.job.executor.pool.corePoolSize", 1);
		this.maximumPoolSize = jobConf.getContext().getInt("tiny.core.job.executor.pool.maximumPoolSize", 1);
		this.keepAliveTime = jobConf.getContext().getInt("tiny.core.job.executor.pool.keepAliveTime", 1200);
		this.workQueueSize = jobConf.getContext().getInt("tiny.core.job.executor.pool.workQueueSize", 1);
		this.checkIdleWorkerInterval = jobConf.getContext().getInt("tiny.core.job.executor.pool.checkIdleWorkerInterval", 1);
		
		// configure rejected execution handler
		rejectedPolicy = new CallerScheduleIdleWorkerPolicy();
		((Configured) rejectedPolicy).setJobConf(jobConf);
		
		// configure thread factory
		String threadFactoryClassName = jobConf.getContext().get(
				"tiny.core.job.executor.thread.factory.class", 
				"org.shirdrn.tinyframework.core.executor.DefaultThreadFactory");
		threadFactory = ObjectFactory.getInstance(threadFactoryClassName, ThreadFactory.class, classLoader);
		
		// configure work queue
		workQueue = new ArrayBlockingQueue<Runnable>(workQueueSize);
		
		// create thread pool
		workerPool = new ThreadPoolExecutor(
    		corePoolSize,  maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, 
    		workQueue, threadFactory, rejectedPolicy);
				
		LOG.info("Worker pool configuration;" + 
				"corePoolSize=" + this.corePoolSize + "," + "maximumPoolSize=" + this.maximumPoolSize + "," + 
				"keepAliveTime=" + this.keepAliveTime + "," + "workQueueSize=" + this.workQueueSize + "," + 
				"checkIdleWorkerInterval=" + this.checkIdleWorkerInterval
			);		
	}

	@Override
	public ExecutorService getWorkerPool() {
		return workerPool;
	}

	@Override
	public int getCorePoolSize() {
		return corePoolSize;
	}

	@Override
	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	@Override
	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	@Override
	public int getWorkQueueSize() {
		return workQueueSize;
	}

	@Override
	public int getCheckIdleWorkerInterval() {
		return checkIdleWorkerInterval;
	}

	@Override
	public PoolReadable getPoolReadable() {
		return this;
	}

	public Job<? extends Task> getTinyJob() {
		return tinyJob;
	}

	public void setTinyJob(Job<? extends Task> tinyJob) {
		this.tinyJob = tinyJob;
	}

}
