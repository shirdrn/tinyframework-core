package org.shirdrn.tinyframework.commons.core.job.database;

public interface RecordService<P> {

	void doRecord(P persistObject);
	
}
