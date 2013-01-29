package org.shirdrn.tinyframework.core.executor;

public interface SinglePoolReadable {

	int getCorePoolSize();

	int getMaximumPoolSize();

	int getKeepAliveTime();

	int getWorkQueueSize();

	int getCheckIdleWorkerInterval();
	
}
