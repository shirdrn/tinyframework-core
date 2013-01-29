package org.shirdrn.tinyframework.commons.core.job;

import java.util.LinkedHashMap;

import org.shirdrn.tinyframework.commons.core.conf.JobConf;

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
public interface TinyJob<T extends TinyTask> {

	/**
	 * Register {@link T extends TinyTask} classes by repeatedly invoking this method.
	 * @param taskId
	 * @param taskClass
	 */
	void registerTask(String taskId, Class<T> taskClass);
	
	/**
	 * Return {@link TinyTask} class collection registered.
	 * @return
	 */
	LinkedHashMap<String, Class<T>> getTaskClasses();
	
	/**
	 * Return the <code>taskId</code> registerd related to the given {@link TinyTask} class implementation.
	 * @return
	 */
	String getTaskId(Class<T> taskClass);
	
	/**
	 * Gets a {@link JobConf} instance.
	 */
	JobConf getJobConf();
	
}
