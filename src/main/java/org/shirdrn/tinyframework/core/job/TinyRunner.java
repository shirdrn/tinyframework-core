package org.shirdrn.tinyframework.core.job;

import org.shirdrn.tinyframework.core.conf.TaskConf;
import org.shirdrn.tinyframework.core.constants.RunningMode;

public interface TinyRunner<T extends TinyTask> {

	/**
	 * Get mode the {@link TinyRunner} will start to run.
	 * @return
	 */
	RunningMode getRunningMode();
	
	/**
	 * Submit a {@link Runnable} task encapsulated in <code>appConf</code>, invoking 
	 * by implementation class of base class {@link TinyJobRunner}.</br>
	 * Each {@link TinyTask} instance must own a name called <code>taskName</code>.
	 * @param taskName
	 * @param taskConf
	 * @throws TinyTaskException
	 */
	void submitTask(final String taskName, final TaskConf taskConf) throws TinyTaskException;
	
	/**
     * Start a job holding by this {@link TinyJob}, and a job is consist of multiple {@link TinyTask}s
     * which are organized and binded in a {@link TinyTaskGroupHolder} instance. 
     */
	void startRunner();
	
	/**
	 * Fire a {@link TinyTaskGroupHolder} instance to be started.
	 * @param taskName
	 * @param taskConf
	 */
	void fireTaskGroupHolder(final String taskName, final TaskConf taskConf);
}
