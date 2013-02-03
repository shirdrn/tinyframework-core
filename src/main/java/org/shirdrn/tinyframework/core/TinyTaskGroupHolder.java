package org.shirdrn.tinyframework.core;

import java.util.LinkedList;


/**
 * A task group holder holds the objects and logics of multiple {@link T extends TinyTask},
 * meanwhile it can hold the task running status, as well as statistics information.
 * 
 * @author Yanjun
 */
public abstract class TinyTaskGroupHolder<T extends Task> {
	
	/** Task group name, a unique identifier */
	protected String name;
	protected T currentTask;
	protected HolderStatus holderStatus;
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
			.append("status=" + holderStatus + ",")
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
	
	public String toString(long seqNo) {
		StringBuffer buf = new StringBuffer();
		buf
			.append("TaskGroupHolder[" + seqNo + "][" + name + "];")
			.append("timeTaken=" + timeTaken + ",")
			.append("status=" + holderStatus + ",")
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
	 * A running tiny task held by a {@link TinyTaskGroupHolder} instance,
	 * and will be started to process.
	 * 
	 * @author Yanjun
	 *
	 * @param <T>
	 */
	public static class RunningTask<T> {
		
		protected final T tinyTask;
		protected RunningStatus runningStatus;
		
		public RunningTask(T tinyTask, RunningStatus runningStatus) {
			super();
			this.tinyTask = tinyTask;
			this.runningStatus = runningStatus;
		}
		
		public static enum RunningStatus {
			RUNNING,
			IDLE
		}

		@Override
		public String toString() {
			return "[" + tinyTask + ", " + runningStatus + "]";
		}

		public RunningStatus getRunningStatus() {
			return runningStatus;
		}

		public T getTinyTask() {
			return tinyTask;
		}

		public void setRunningStatus(RunningStatus runningStatus) {
			this.runningStatus = runningStatus;
		}
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

	public HolderStatus getHolderStatus() {
		return holderStatus;
	}

	public void setHolderStatus(HolderStatus holderStatus) {
		this.holderStatus = holderStatus;
	}
}
