package org.shirdrn.tinyframework.commons.core.job;

import java.util.List;

import org.shirdrn.tinyframework.commons.core.conf.TaskConf;
import org.shirdrn.tinyframework.commons.core.job.TinyJobRunner.RunningTask;
import org.shirdrn.tinyframework.commons.core.job.TinyTask.TaskStatus;

/**
 * A generic implementation of abstract class {@link TinyTaskGroupHolder}.
 * 
 * @author Yanjun
 */
public class GenericTinyTaskGroupHolder<T extends TinyTask> extends TinyTaskGroupHolder<T> {

	protected final TinyJobRunner<T> tinyJobRunner;
	protected final TaskConf taskConf;
	
	public GenericTinyTaskGroupHolder(TaskConf taskConf, 
			TinyJobRunner<T> tinyJobRunner) {
		super();
		this.taskConf = taskConf;
		this.tinyJobRunner = tinyJobRunner;
	}
	
	/**
	 * Obtain a idle task instance
	 * @param taskClass
	 * @param taskConf
	 * @return
	 */
	protected RunningTask<T> createTask(Class<T> taskClass, TaskConf taskConf) {
		RunningTask<T> runningTask = null;
		List<RunningTask<T>> instances = tinyJobRunner.getTaskInstances().get(taskClass);
		while(true) {
			synchronized(instances) {
				for(RunningTask<T> t : instances) {
					if(t.status==RunningTask.Status.IDLE) {
						runningTask = t;
						runningTask.task.setTaskConf(taskConf);
						runningTask.status = RunningTask.Status.RUNNING;
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
		List<RunningTask<T>> instances = tinyJobRunner.getTaskInstances().get(taskClass);
		synchronized(instances) {
			runningTask.status = RunningTask.Status.IDLE;
			runningTask.task.setTaskConf(null);
		}
	}

	@Override
	public void startHolder() throws Exception {
		for(Class<T> taskClass : tinyJobRunner.getTaskClasses().values()) {
			RunningTask<T> runningTask = null;
			try {
				runningTask = createTask(taskClass, taskConf);
				currentTask = runningTask.task;
				currentTask.runTask();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				addTask(currentTask);
				timeTaken += currentTask.getTimeTaken();
				if(currentTask.getTaskStatus()==TaskStatus.SUC) {
					tinyJobRunner.getCounter().incSuccessTinyTaskCount();
					status = HolderStatus.SUCCESS;
					reuseTask(taskClass, runningTask);
				} else {
					tinyJobRunner.getCounter().incFailureTinyTaskCount();
					status = HolderStatus.FAILURE;
					reuseTask(taskClass, runningTask);
				}
				
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
