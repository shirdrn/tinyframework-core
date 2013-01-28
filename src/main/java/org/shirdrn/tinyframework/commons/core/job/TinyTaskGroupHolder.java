package org.shirdrn.tinyframework.commons.core.job;

import java.util.LinkedList;

/**
 * A task group holder holds the objects and logics of multiple {@link T extends TinyTask},
 * meanwhile it can hold the task running status, as well as statistics information.
 * 
 * @author Yanjun
 */
public abstract class TinyTaskGroupHolder<T extends TinyTask> {
	
	/** Task group name, a unique identifier */
	protected String name;
	protected T currentTask;
	protected HolderStatus status;
	protected int timeTaken;
	protected LinkedList<T> tasks = new LinkedList<T>();
	
	public TinyTaskGroupHolder() {
		super();
	}
	
	public TinyTaskGroupHolder(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Start a group of {@link T extends TinyTask} instances to run.
	 * @throws Exception
	 */
	public abstract void startHolder() throws Exception;
	
	protected void addTask(T task) {
		tasks.add(task);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf
			.append("TaskGroupHolder[" + name + "];")
			.append("timeTaken=" + timeTaken + ",")
			.append("status=" + status + ",")
			.append("tasks=[");
		if(!tasks.isEmpty()) {
			for(int i=0; i<tasks.size()-1; i++) {
				buf.append(tasks.get(i) + "-");
			}
			buf.append(tasks.get(tasks.size()-1));
		}
		buf.append("]");
		return buf.toString();
	}
	
	public String toString(long id) {
		StringBuffer buf = new StringBuffer();
		buf
			.append("TaskGroupHolder[" + id + "][" + name + "];")
			.append("timeTaken=" + timeTaken + ",")
			.append("status=" + status + ",")
			.append("tasks=[");
		if(!tasks.isEmpty()) {
			for(int i=0; i<tasks.size()-1; i++) {
				buf.append(tasks.get(i) + "-");
			}
			buf.append(tasks.get(tasks.size()-1));
		}
		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * The status of {@link TinyTaskGroupHolder<T extends TinyTask>} class.
	 * 
	 * @author Yanjun
	 */
	public static enum HolderStatus {
		SUCCESS,
		FAILURE
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
