package org.shirdrn.tinyframework.commons.core.executor;

public interface SinglePoolReadable {

	int getCorePoolSize();

	int getMaximumPoolSize();

	int getKeepAliveTime();

	int getWorkQueueSize();

	int getCheckIdleWorkerInterval();
	
}
