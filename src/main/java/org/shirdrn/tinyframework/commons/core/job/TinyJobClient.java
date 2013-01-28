package org.shirdrn.tinyframework.commons.core.job;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.commons.core.conf.Configurable;
import org.shirdrn.tinyframework.commons.core.conf.Context;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.conf.TaskConf;
import org.shirdrn.tinyframework.commons.core.utils.ObjectFactory;

public abstract class TinyJobClient<R extends TinyJobRunner<TinyTask>> implements Configurable {

	private static final Logger LOG = Logger.getLogger(TinyJobClient.class);
	protected static final DecimalFormat FORMATTER = new DecimalFormat("00");
	protected final R tinyJobRunner;
	protected final ReadableContext readableContext;
	
	public TinyJobClient(Class<R> tinyJobRunnerClass) {
		super();
		// create global context instance
		readableContext = TaskConf.getReadOnlyContext();
		((Context) readableContext).addResource("core-default.xml");
		((Context) readableContext).addResource("core-commons.xml");
		
		tinyJobRunner = ObjectFactory.getInstance(tinyJobRunnerClass, readableContext);
		LOG.info("Load job runner class: " + tinyJobRunnerClass.getName());
	}
	
	@Override
	public void configure() {
		// configure tiny job runner
		tinyJobRunner.setReadableContext(readableContext);
		
		String classes = readableContext.get("commons.core.task.classes", "");
		LOG.info("Read configured task classes;classes=" + classes.trim());
		if(classes!=null) {
			int id = 0;
			for(String clazz : classes.split("[\\s,;]+")) {
				clazz = clazz.trim();
				if(!clazz.isEmpty()) {
					Class<TinyTask> tinyTaskClass = ObjectFactory.getClass(clazz, TinyTask.class);
					String taskId = FORMATTER.format(++id);
					tinyJobRunner.registerTask(taskId, tinyTaskClass);
					LOG.info("Load and register task;id=" + taskId + ",class=" + clazz);
				}
			}
		}
	}
	
	public void start() {
		tinyJobRunner.startRunner();
	}

	public ReadableContext getReadableContext() {
		return readableContext;
	}

}