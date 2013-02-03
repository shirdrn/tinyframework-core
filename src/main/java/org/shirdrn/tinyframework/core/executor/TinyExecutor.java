package org.shirdrn.tinyframework.core.executor;

import java.util.concurrent.ExecutorService;

public interface TinyExecutor<P> {

	ExecutorService getWorkerPool();
	
	P getPoolReadable();
}
