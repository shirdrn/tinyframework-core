package org.shirdrn.tinyframework.commons.core.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.Configured;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.utils.ObjectFactory;

public class SinglePoolTinyExecutorManager extends TinyExecutorManager<SinglePoolReadable> implements SinglePoolReadable {

	private static final Log LOG = LogFactory.getLog(SinglePoolTinyExecutorManager.class);
	protected ExecutorService workerPool;
	protected RejectedExecutionHandler rejectedPolicy;
	protected ThreadFactory threadFactory;
	protected BlockingQueue<Runnable> workQueue;
	protected int corePoolSize;
	protected int maximumPoolSize;
	protected int keepAliveTime;
	protected int workQueueSize;
	protected int checkIdleWorkerInterval;
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		this.corePoolSize = readableContext.getInt("commons.core.executor.pool.corePoolSize", 1);
		this.maximumPoolSize = readableContext.getInt("commons.core.executor.pool.maximumPoolSize", 1);
		this.keepAliveTime = readableContext.getInt("commons.core.executor.pool.keepAliveTime", 1200);
		this.workQueueSize = readableContext.getInt("commons.core.executor.pool.workQueueSize", 1);
		this.checkIdleWorkerInterval = readableContext.getInt("commons.core.executor.pool.checkIdleWorkerInterval", 1);
		
		// configure rejected execution handler
		((Configured) rejectedPolicy).setReadableContext(readableContext);
		rejectedPolicy = new CallerScheduleIdleWorkerPolicy();
		
		// configure thread factory
		String threadFactoryClassName = readableContext.get(
				"commons.core.executor.thread.factory.class", 
				"org.shirdrn.tinyframework.commons.core.executor.DefaultThreadFactory");
		threadFactory = ObjectFactory.getInstance(threadFactoryClassName, ThreadFactory.class, classLoader);
		
		// configure work queue
		workQueue = new ArrayBlockingQueue<Runnable>(workQueueSize);
		
		LOG.info("Worker pool configuration;" + 
				"corePoolSize=" + this.corePoolSize + "," + "maximumPoolSize=" + this.maximumPoolSize + "," + 
				"keepAliveTime=" + this.keepAliveTime + "," + "workQueueSize=" + this.workQueueSize + "," + 
				"checkIdleWorkerInterval=" + this.checkIdleWorkerInterval
			);
	}

	@Override
	public void createExecutor() {
		// initialize thread pool
    	workerPool = new ThreadPoolExecutor(
    		corePoolSize,  maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, 
    		workQueue, threadFactory, rejectedPolicy);
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
	public SinglePoolReadable getReadableExecutorManager() {
		return this;
	}

}
