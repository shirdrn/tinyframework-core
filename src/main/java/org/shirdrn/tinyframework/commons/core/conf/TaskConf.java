package org.shirdrn.tinyframework.commons.core.conf;

import org.shirdrn.tinyframework.commons.core.job.TinyTask;

/**
 * Application configuration object, which contains 2 kinds:
 * <ol>
 * <li>{@link ReadableContext}:  global configuration, read only.</li>
 * <li>{@link WriteableContext}: local or temporary configuration.</li>
 * </ol>
 * 
 * @author Yanjun
 */
public class TaskConf extends Configured implements Cloneable {

	private static final ReadableContext readOnlyContext;
	static {
		readOnlyContext = new Context(false);
	}
	
	public TaskConf() {
		this(new Context(false));
	}
	
	protected TaskConf(WriteableContext writeableContext) {
		super();
		this.readableContext = readOnlyContext;
		this.writeableContext = writeableContext;
	}
	
	/**
	 * Get read only context instance. Actually return the
	 * Unique {@link ReadableContext} instance shared by each
	 * {@link TinyTask} instance.
	 * @return
	 */
	public static final ReadableContext getReadOnlyContext() {
		return readOnlyContext;
	}
	
	/**
	 * Add a xml resource to a <code>readOnlyContext</code> context objecg.
	 * @param name
	 */
	public static final void addResource(String name) {
		((Context)readOnlyContext).addResource(name);
	}
	
}
