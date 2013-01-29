package org.shirdrn.tinyframework.core.executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.conf.Configured;
import org.shirdrn.tinyframework.core.conf.ReadableContext;


/**
 * A handler for rejected tasks that waits the worker to be idle and assigns to who will run 
 * the rejected task by invoking the <tt>execute</tt> method, unless the executor has been 
 * shut down, in which case the task is discarded.
 * 
 * @author Yanjun
 */
public class CallerScheduleIdleWorkerPolicy extends Configured implements RejectedExecutionHandler {

	private static final Log LOG = LogFactory.getLog(CallerScheduleIdleWorkerPolicy.class);
	private int checkIdleWorkerInterval = 1000;
	private int workQueueSize = 1;
	
	/** Creates a <tt>CallerWaitIdleWorkerPolicy</tt> instance. */
    public CallerScheduleIdleWorkerPolicy() {
    	super();
    }
    
    @Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		this.workQueueSize = readableContext.getInt("core.job.executor.pool.workQueueSize", 1);
		this.checkIdleWorkerInterval = readableContext.getInt("core.job.executor.pool.checkIdleWorkerInterval", 1000);
	}

	/**
     * If no worker thread is idle, caller is entering loop until a worker thread is released.
     * The idle worker thread executes task r out of the caller's thread, unless the executor
     * has been shut down, in which case the task is discarded.
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		while(!executor.isShutdown() && executor.getQueue().size()>=workQueueSize) {
    		try {
				Thread.sleep(checkIdleWorkerInterval);
				LOG.debug("Loop statistics;" + "sleepInterval=" + checkIdleWorkerInterval + "," +
						"poolSize=" + executor.getPoolSize() + "," + "activeCount=" + executor.getActiveCount() + "," +
						"queueSize=" + executor.getQueue().size() + "," + "completedTaskCount=" + executor.getCompletedTaskCount() + "," +
						"taskCount=" + executor.getTaskCount() + "," + "largestPoolSize=" + executor.getLargestPoolSize()
						);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
		// Push back to thread pool, 
		// and re-schedule worker thread to process task r.
		executor.execute(r);			
	}
}