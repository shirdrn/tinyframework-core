package org.shirdrn.tinyframework.core;

import java.util.Map;

import org.shirdrn.tinyframework.core.conf.JobConf;

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
 */
public interface Job<T extends Task> {

	/**
	 * Gets job name.
	 */
	String getName();
	
	/**
	 * Gets a {@link JobConf} instance.
	 */
	JobConf getJobConf();
	
	/**
	 * Register {@link T extends TinyTask} classes by repeatedly invoking this method.
	 * @param taskId
	 * @param taskClass
	 */
	void registerTask(String taskId, Class<? extends TinyTask> taskClass);
	
	/**
	 * Return {@link TinyTask} class collection registered.
	 * @return
	 */
	Map<String, Class<T>> getTaskClasses();
	
	/**
	 * Return the <code>taskId</code> registerd related to the given {@link TinyTask} class implementation.
	 * @return
	 */
	String getTaskId(final Class<T> taskClass);
	
}
