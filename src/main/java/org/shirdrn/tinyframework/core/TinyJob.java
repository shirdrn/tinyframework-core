package org.shirdrn.tinyframework.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.constants.RunnerMode;

public class TinyJob<T extends TinyTask> implements Job<T> {

	private static final Log LOG = LogFactory.getLog(TinyJob.class);
	protected String name;
	protected JobConf jobConf;
	protected RunnerMode runningMode;
	protected final Map<String, Class<T>> taskClasses = new LinkedHashMap<String, Class<T>>();
	protected final LinkedList<Class<T>> taskClassesAsList = new LinkedList<Class<T>>();
	protected final Map<Class<T>, String> taskMappings = new LinkedHashMap<Class<T>, String>();
	
	public TinyJob() {
		super();
		this.jobConf = new JobConf(this);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public JobConf getJobConf() {
		return jobConf;
	}

	@Override
	public Map<String, Class<T>> getTaskClasses() {
		return Collections.unmodifiableMap(taskClasses);
	}

	@Override
	public String getTaskId(final Class<T> taskClass) {
		return taskMappings.get(taskClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerTask(String taskId, Class<? extends TinyTask> taskClass) {
		if(!taskClasses.containsValue(taskClass)) {
			LOG.debug("Register tiny task;taskClass=" + taskClass.getName());
			taskClasses.put(taskId, (Class<T>) taskClass);
			taskClassesAsList.addLast((Class<T>) taskClass);
			taskMappings.put((Class<T>) taskClass, taskId);
		} else {
			LOG.warn("Ignore existed task;taskClass=" + taskClass.getName());
		}		
	}

	public LinkedList<Class<T>> getTaskClassesAsList() {
		return taskClassesAsList;
	}

	
}
