package org.shirdrn.tinyframework.commons.core.executor;

import java.util.concurrent.ExecutorService;

import org.shirdrn.tinyframework.commons.core.conf.Configured;

public abstract class TinyExecutorManager<R> extends Configured {

	public abstract void createExecutor();
	
	public abstract ExecutorService getWorkerPool();
	
	public abstract R getReadableExecutorManager();

}
