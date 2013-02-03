package org.shirdrn.tinyframework.core;

import org.shirdrn.tinyframework.core.constants.RunnerMode;
import org.shirdrn.tinyframework.core.executor.SinglePoolReadable;
import org.shirdrn.tinyframework.core.executor.TinyExecutorManager;

public interface JobRunner {

	/**
	 * Get mode the {@link JobRunner} will start to run.
	 * @return
	 */
	RunnerMode getRunnerMode();
	
	
	/**
     * Start a job holding by this {@link Job}, and a job is consist of multiple {@link TinyTask}s
     * which are organized and binded in a {@link TinyTaskGroupHolder} instance. 
     */
	void startRunner();
	
	/**
	 * Get the executor manager instance.
	 * @return
	 */
	TinyExecutorManager<SinglePoolReadable> getExecutorManager();
	
}
