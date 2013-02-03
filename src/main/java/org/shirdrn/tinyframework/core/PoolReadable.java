package org.shirdrn.tinyframework.core;

public interface PoolReadable {

	int getCorePoolSize();

	int getMaximumPoolSize();

	int getKeepAliveTime();

	int getWorkQueueSize();

	int getCheckIdleWorkerInterval();
}
