package org.shirdrn.tinyframework.core.executor;

import org.shirdrn.tinyframework.core.Job;
import org.shirdrn.tinyframework.core.Task;


public interface TinyExecutorManager<P> {

	void registerExecutor(final Job<? extends Task> tinyJob, TinyExecutor<P> tinyExecutor);
	
	TinyExecutor<P> retrieve(final Job<? extends Task> tinyJob);

}
