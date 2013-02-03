package org.shirdrn.tinyframework.core.conf;


/**
 * Application configuration object, which contains 2 kinds:
 * <ol>
 * <li>{@link ReadableContext}:  global configuration, held by {@link JobConf} instance.</li>
 * <li>{@link WriteableContext}: local or temporary configuration.</li>
 * </ol>
 * 
 * @author Yanjun
 */
public class TaskConf extends Configured implements Cloneable {

	private final JobConf jobConf;
	private final WriteableContext context;
	
	public TaskConf() {
		this(new Context(false));
	}
	
	protected TaskConf(WriteableContext context) {
		super();
		this.jobConf = new JobConf();
		this.context = context;
	}
	
	public WriteableContext getContext() {
		return context;
	}
	
	public JobConf getJobConf() {
		return jobConf;
	}
	
	/**
	 * Add a xml resource to a <code>readOnlyContext</code> context objecg.
	 * @param name
	 */
	public final void addResource(String name) {
		((Context)context).addResource(name);
	}

}
