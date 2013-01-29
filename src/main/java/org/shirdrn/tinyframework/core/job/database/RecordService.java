package org.shirdrn.tinyframework.core.job.database;

public interface RecordService<P> {

	void doRecord(P persistObject);
	
}
