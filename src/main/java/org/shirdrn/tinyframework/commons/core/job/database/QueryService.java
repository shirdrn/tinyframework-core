package org.shirdrn.tinyframework.commons.core.job.database;

import java.util.List;
import java.util.Map;

public interface QueryService<P> {

	List<P> query(Map<String, ?> conditions);
	
}
