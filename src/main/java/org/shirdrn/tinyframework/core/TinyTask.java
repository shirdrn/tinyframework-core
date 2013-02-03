package org.shirdrn.tinyframework.core;

import java.util.Date;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.core.utils.DatetimeUtils;


/**
 * Abstract tiny task for process specified logics.
 * 
 * @author Yanjun
 */
public abstract class TinyTask extends Task {

	public TinyTask() {
		super();
	}
	
	private static final Logger LOG = Logger.getLogger(TinyTask.class);
	protected TaskStatus taskStatus = TaskStatus.INI;
	protected Date startTime;
	protected Date terminateTime;
	protected long timeTaken;
	protected String dateFormat = "yyyy-MM-dd HH:mm:ss";
	protected Throwable cause;
	
	/**
	 * The logics of a task executing.
	 * @throws TinyTaskException
	 */
	protected abstract void execute() throws TinyTaskException;
	
	/**
	 * This method is used for {@link TinyJobRunnerBAK} instance invoking
	 * to process each {@link TinyTask} instance.
	 */
	public void runTask() {
		startTime = new Date();
		try {
			taskStatus = TaskStatus.PRO;
			execute();
			taskStatus = TaskStatus.SUC;
		} catch (Exception e) {
			cause = e;
			if(LOG.isDebugEnabled()) {
				e.printStackTrace();
			}
			taskStatus = TaskStatus.FAI;
		} finally {
			terminateTime = new Date();
			timeTaken = (terminateTime.getTime()-startTime.getTime());
		}
	}
	
	@Override
	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	@Override
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	@Override
	public String getStartTime() {
		return DatetimeUtils.formatDateTime(startTime, dateFormat);
	}

	@Override
	public String getTerminateTime() {
		return DatetimeUtils.formatDateTime(terminateTime, dateFormat);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("" + taskId + ":" + taskStatus + ":" + timeTaken);
		sb.append("");
		return sb.toString();
	}
	
	/**
	 * Status of {@link TinyTask} executing, which are used by
	 * framework internally, not exported to developer.
	 * 
	 * @author Yanjun
	 */
	public static enum TaskStatus {

		INI, // INITIAL
		PRO, // PROCESSING
		SUC, // SUCCESS
		FAI  // FAILURE
		
	}

}
