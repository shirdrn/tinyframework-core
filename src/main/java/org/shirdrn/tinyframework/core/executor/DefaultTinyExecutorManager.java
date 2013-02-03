package org.shirdrn.tinyframework.core.executor;

import java.util.HashMap;
import java.util.Map;

import org.shirdrn.tinyframework.core.PoolReadable;
import org.shirdrn.tinyframework.core.Job;
import org.shirdrn.tinyframework.core.Task;


public class DefaultTinyExecutorManager implements TinyExecutorManager<PoolReadable> {

	private static final TinyExecutorManager<? extends PoolReadable> INSTANCE = 
			new DefaultTinyExecutorManager();
	private static final Map<Job<? extends Task>, TinyExecutor<? extends PoolReadable>> EXECUTORS = 
			new HashMap<Job<? extends Task>, TinyExecutor<? extends PoolReadable>>();

	private DefaultTinyExecutorManager() {
		super();
	}

	@Override
	public void registerExecutor(Job<? extends Task> tinyJob, 
			TinyExecutor<PoolReadable> tinyExecutor) {
		EXECUTORS.put(tinyJob, tinyExecutor);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TinyExecutor<PoolReadable> retrieve(Job<? extends Task> tinyJob) {
		return (TinyExecutor<PoolReadable>) EXECUTORS.get(tinyJob);
	}
	
	public static TinyExecutorManager<? extends PoolReadable> getInstance() {
		return INSTANCE;
	}

}
