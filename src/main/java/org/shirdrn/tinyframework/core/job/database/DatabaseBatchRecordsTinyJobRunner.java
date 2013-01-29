package org.shirdrn.tinyframework.core.job.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.job.TinyJobRunner;
import org.shirdrn.tinyframework.core.job.TinyTask;

public abstract class DatabaseBatchRecordsTinyJobRunner<P, S extends BatchQueryService<P>> 
		extends TinyJobRunner<TinyTask> implements RecordService<P> {

	public DatabaseBatchRecordsTinyJobRunner(JobConf jobConf) {
		super(jobConf);
	}

	private static final Log LOG = LogFactory.getLog(DatabaseBatchRecordsTinyJobRunner.class);
	protected int fromId = 1;
	protected int batchCount = 1000;
	protected S queryService;
	protected Map<String, ?> conditions;
	
	@Override
	public void configure() {
		super.configure();
		this.conditions = new HashMap<String, Object>();
	}
	
	@Override
	protected void iterate() {
		final int maxId = getMaxRecordId();
		if(maxId <= 0) {
			throw new RuntimeException("Missing to set maxId!!!");
		}
		int toId = Math.min(fromId + batchCount, maxId);
		while(fromId <= maxId) {
			List<P> records = queryService.query(fromId, toId, conditions);	
			for (P persistObject : records) {
				try {
					doRecord(persistObject);
				} catch (Exception e) {
					LOG.error("Error to read record;record=" + persistObject, e);
				}
			}
			
			fromId = toId + 1;
			toId = fromId + batchCount;
			
		}
	}
	
	protected abstract int getMaxRecordId();

}
