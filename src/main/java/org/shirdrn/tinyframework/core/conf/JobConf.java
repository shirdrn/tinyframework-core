package org.shirdrn.tinyframework.core.conf;

import org.shirdrn.tinyframework.core.TinyJob;
import org.shirdrn.tinyframework.core.TinyTask;

public class JobConf {

	private TinyJob<? extends TinyTask> tinyJob;
	private final ReadableContext context;
	private static final ReadableContext readOnlyContext;
	
	static {
		readOnlyContext = new Context(false);
	}
	
	public JobConf() {
		this(null);
	}

	public JobConf(TinyJob<? extends TinyTask> tinyJob) {
		super();
		this.tinyJob = tinyJob;
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

	public TinyJob<? extends TinyTask> getTinyJob() {
		return tinyJob;
	}

	public void setTinyJob(TinyJob<? extends TinyTask> tinyJob) {
		this.tinyJob = tinyJob;
	}

}
