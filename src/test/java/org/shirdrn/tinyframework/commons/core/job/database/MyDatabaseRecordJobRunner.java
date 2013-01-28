package org.shirdrn.tinyframework.commons.core.job.database;

import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.job.database.DatabaseRecordJobRunner;

public class MyDatabaseRecordJobRunner extends DatabaseRecordJobRunner<MyPo, MyQueryService> {

	public MyDatabaseRecordJobRunner(ReadableContext readableContext) {
		super(readableContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doRecord(MyPo persistObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void releaseAll() {
		// TODO Auto-generated method stub
		
	}


}
