package org.shirdrn.tinyframework.commons.core.job.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.job.TinyJobRunner;
import org.shirdrn.tinyframework.commons.core.job.TinyTask;

public abstract class DatabaseRecordJobRunner<P, S extends QueryService<P>> 
		extends TinyJobRunner<TinyTask> implements RecordService<P> {

	public DatabaseRecordJobRunner(ReadableContext readableContext) {
		super(readableContext);
	}

	private static final Log LOG = LogFactory.getLog(DatabaseRecordJobRunner.class);
	protected S queryService;
	protected Map<String, ?> conditions;
	
	
	@Override
	protected void iterate() {
		List<P> records = queryService.query(conditions);	
		for (P persistObject : records) {
			try {
				doRecord(persistObject);
			} catch (Exception e) {
				LOG.error("Error to read record;record=" + persistObject, e);
			}
		}
	}

	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		this.conditions = new HashMap<String, Object>();
	}

}
