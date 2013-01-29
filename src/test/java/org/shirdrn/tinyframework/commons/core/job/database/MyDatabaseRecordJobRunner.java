package org.shirdrn.tinyframework.commons.core.job.database;

import org.shirdrn.tinyframework.commons.core.conf.JobConf;

public class MyDatabaseRecordJobRunner extends DatabaseRecordJobRunner<MyPo, MyQueryService> {

	public MyDatabaseRecordJobRunner(JobConf jobConf) {
		super(jobConf);
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
