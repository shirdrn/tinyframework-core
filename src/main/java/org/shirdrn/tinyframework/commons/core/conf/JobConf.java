package org.shirdrn.tinyframework.commons.core.conf;

import org.shirdrn.tinyframework.commons.core.job.TinyTask;

public class JobConf {

	private final ReadableContext context;
	private static final ReadableContext readOnlyContext;
	
	static {
		readOnlyContext = new Context(false);
	}
	
	public JobConf() {
		super();
		this.context = readOnlyContext;
	}

	public ReadableContext getContext() {
		return context;
	}
	
	/**
	 * Add a xml resource to a <code>readOnlyContext</code> context object.
	 * @param name
	 */
	public final void addJobResource(String name) {
		addResource(name);
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
	 * Add a xml resource to a <code>readOnlyContext</code> context object.
	 * @param name
	 */
	public static final void addResource(String name) {
		((Context)readOnlyContext).addResource(name);
	}

}
