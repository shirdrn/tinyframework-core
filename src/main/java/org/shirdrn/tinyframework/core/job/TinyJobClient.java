package org.shirdrn.tinyframework.core.job;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.core.conf.Context;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;

public abstract class TinyJobClient<R extends TinyJobRunner<TinyTask>> {

	private static final Logger LOG = Logger.getLogger(TinyJobClient.class);
	protected static final DecimalFormat FORMATTER = new DecimalFormat("00");
	protected final R tinyJobRunner;
	protected final JobConf jobConf;
	
	@SuppressWarnings("unchecked")
	public TinyJobClient() {
		super();
		// create global context instance
		jobConf = new JobConf();
		((Context) jobConf.getContext()).addResource("core-default.xml");
		((Context) jobConf.getContext()).addResource("core-this.xml");
		String jobRunnerClass = jobConf.getContext().get("core.job.runner.class", "org.shirdrn.tinyframework.core.job.files.FileLineTinyJobRunner");
		LOG.info("Load job runner class: " + jobRunnerClass);
		tinyJobRunner = (R) ObjectFactory.getInstance(
				jobRunnerClass, this.getClass().getClassLoader(), jobConf);
	}
	
	private void prepareForClient() {
		String classes = jobConf.getContext().get("core.job.task.classes", "");
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
		configure();
	}
	
	/**
	 * Configure a {@link JobClient} instance, and prepare to start to run.
	 */
	protected abstract void configure();

	public void start() {
		prepareForClient();
		tinyJobRunner.startRunner();
	}

	public JobConf getJobConf() {
		return jobConf;
	}
	
	@SuppressWarnings("rawtypes")
	public static TinyJobClient submitBy(Class<? extends TinyJobClient> taskClientClass) {
		TinyJobClient client = ObjectFactory.getInstance(taskClientClass);
		client.start();
		return client;
	}

}