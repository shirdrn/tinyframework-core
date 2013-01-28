package org.shirdrn.tinyframework.commons.core.job.database;

import java.util.List;
import java.util.Map;

public interface BatchQueryService<P> {

	List<P> query(int fromId, int toId, Map<String, ?> conditions);
	
}
