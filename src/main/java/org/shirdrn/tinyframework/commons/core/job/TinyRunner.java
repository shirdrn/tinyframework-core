package org.shirdrn.tinyframework.commons.core.job;

import org.shirdrn.tinyframework.commons.core.conf.TaskConf;

/**
 * 
 * Used for managing a job, which contains a component:
 * <ul>
 * <li>A group of {@link TinyTask}s</li>
 * </ul>
 * 
 * @author Yanjun
 *
 * @param <T>
 * @param <TGH>
 */
public interface TinyRunner<T extends TinyTask> {

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
     * Start a job holding by this {@link TinyRunner}, and a job is consist of multiple {@link TinyTask}s
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
