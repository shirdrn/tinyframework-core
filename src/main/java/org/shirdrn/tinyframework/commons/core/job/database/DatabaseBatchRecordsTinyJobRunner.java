package org.shirdrn.tinyframework.commons.core.job.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.job.TinyJobRunner;
import org.shirdrn.tinyframework.commons.core.job.TinyTask;

public abstract class DatabaseBatchRecordsTinyJobRunner<P, S extends BatchQueryService<P>> 
		extends TinyJobRunner<TinyTask> implements RecordService<P> {

	public DatabaseBatchRecordsTinyJobRunner(ReadableContext readableContext) {
		super(readableContext);
	}

	private static final Log LOG = LogFactory.getLog(DatabaseBatchRecordsTinyJobRunner.class);
	protected int fromId = 1;
	protected int batchCount = 1000;
	protected S queryService;
	protected Map<String, ?> conditions;
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
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
