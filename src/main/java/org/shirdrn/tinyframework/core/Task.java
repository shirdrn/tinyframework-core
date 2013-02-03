package org.shirdrn.tinyframework.core;

import org.shirdrn.tinyframework.core.TinyTask.TaskStatus;
import org.shirdrn.tinyframework.core.conf.TaskConf;

public abstract class Task {

	protected String taskId;
	protected String taskName;
	protected TaskConf taskConf;
	
	public abstract String getStartTime();
	
	public abstract String getTerminateTime();
	
	public abstract long getTimeTaken();
	
	public abstract TaskStatus getTaskStatus();
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
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
}
