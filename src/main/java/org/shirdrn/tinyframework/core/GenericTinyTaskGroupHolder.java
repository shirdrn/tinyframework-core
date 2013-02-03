package org.shirdrn.tinyframework.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.TinyTask.TaskStatus;
import org.shirdrn.tinyframework.core.conf.TaskConf;

/**
 * A generic implementation of abstract class {@link TinyTaskGroupHolder}.
 * 
 * @author Yanjun
 */
public class GenericTinyTaskGroupHolder<T extends TinyTask, J extends TinyJob<T>> extends TinyTaskGroupHolder<T> {

	private static final Log LOG = LogFactory.getLog(GenericTinyTaskGroupHolder.class);
	private final RunningTinyJob<T, J> runningJob;
	private final TaskConf taskConf;
	
	public GenericTinyTaskGroupHolder(final RunningTinyJob<T, J> runningJob, final TaskConf taskConf) {
		super();
		this.runningJob = runningJob;
		this.taskConf = taskConf;
	}
	
	/**
	 * Obtain a idle task instance
	 * @param taskClass
	 * @param taskConf
	 * @return
	 */
	protected RunningTask<T> createTask(Class<T> taskClass) {
		RunningTask<T> runningTask = null;
		List<RunningTask<T>> instances = runningJob.getReusableTaskInstances().get(taskClass);
		while(true) {
			synchronized(instances) {
				for(RunningTask<T> t : instances) {
					if(t.getRunningStatus()==RunningTask.RunningStatus.IDLE) {
						runningTask = t;
						runningTask.getTinyTask().setTaskConf(taskConf);
						runningTask.setRunningStatus(RunningTask.RunningStatus.RUNNING);
						break;
					}
				}
			}
			if(runningTask!=null) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		return runningTask;
	}
	
	/**
	 * Reuse task instance: clear task state data
	 * @param taskClass
	 * @param runningTask
	 */
	protected void reuseTask(Class<T> taskClass, RunningTask<T> runningTask) {
		List<RunningTask<T>> instances = runningJob.getReusableTaskInstances().get(taskClass);
		synchronized(instances) {
			runningTask.setRunningStatus(RunningTask.RunningStatus.IDLE);
			runningTask.getTinyTask().setTaskConf(null);
		}
	}

	@Override
	public void startHolder() throws Exception {
		for(Class<T> taskClass : runningJob.getTinyJob().getTaskClasses().values()) {
			RunningTask<T> runningTask = null;
			try {
				runningTask = createTask(taskClass);
				currentTask = runningTask.getTinyTask();
				TinyTask taskRunner = (TinyTask) currentTask;
				taskRunner.runTask();
			} catch(Exception e) {
				throw e;
			} finally {
				addTask(currentTask);
				timeTaken += currentTask.getTimeTaken();
				if(currentTask.getTaskStatus()==TaskStatus.SUC) {
					runningJob.getCounter().incSuccessTinyTaskCount();
				} else {
					runningJob.getCounter().incFailureTinyTaskCount();
				}
				reuseTask(taskClass, runningTask);
				
				// if break flag returned is true gained from the callback interface 
				if(currentTask instanceof BreakCallback) {
					BreakCallback callback = (BreakCallback) currentTask;
					if(callback.isBreak()) {
						break;
					}
				} else if(currentTask.getTaskStatus()!=TaskStatus.SUC) {
					break;
				}
			}
		}
	}

	public TaskConf getTaskConf() {
		return taskConf;
	}
	
	
}
