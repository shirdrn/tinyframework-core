package org.shirdrn.tinyframework.commons.core.job;

import java.util.Date;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.commons.core.conf.TaskConf;
import org.shirdrn.tinyframework.commons.core.utils.DatetimeUtils;


/**
 * Abstract tiny task for process specified logics.
 * 
 * @author Yanjun
 */
public abstract class TinyTask {

	private static final Logger LOG = Logger.getLogger(TinyTask.class);
	private String taskId;
	private TaskStatus taskStatus = TaskStatus.INI;
	private Date startTime;
	private Date endTime;
	private long timeTaken;
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private Throwable cause;
	
	protected TaskConf taskConf;
	
	public TinyTask() {
		super();
	}
	
	/**
	 * This method is used for {@link TinyJobRunner} instance invoking
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
			endTime = new Date();
			timeTaken = (endTime.getTime()-startTime.getTime());
		}
	}
	
	/**
	 * The logics of a task executing.
	 * @throws Exception
	 */
	protected abstract void execute() throws Exception;

	/**
	 * Access the configuration of a {@link TinyTask} object. Here
	 * {@link TinyTask#taskConf} maybe hold useful data for a {@link TinyTask}
	 * instance running, and this configuration object is of final feature.   
	 * @return
	 */
	public TaskConf getTaskConf() {
		return taskConf;
	}

	public void setTaskConf(TaskConf taskConf) {
		this.taskConf = taskConf;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public String getStartTime() {
		return DatetimeUtils.formatDateTime(startTime, dateFormat);
	}

	public String getEndTime() {
		return DatetimeUtils.formatDateTime(endTime, dateFormat);
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("" + taskId + ":" + taskStatus + ":" + timeTaken);
		if(LOG.isDebugEnabled()) {
			if(taskStatus!=TaskStatus.SUC && cause!=null) {
				sb.append(":" + cause.getClass().getSimpleName());
			}
		}
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
