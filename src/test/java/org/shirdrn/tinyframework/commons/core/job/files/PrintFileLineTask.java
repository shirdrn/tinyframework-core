package org.shirdrn.tinyframework.commons.core.job.files;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.commons.core.constants.KeyName;
import org.shirdrn.tinyframework.commons.core.job.TinyTask;


public class PrintFileLineTask extends TinyTask {
	
	private static final Logger LOG = Logger.getLogger(PrintFileLineTask.class);
	
	@Override
	protected void execute() throws Exception {
		String domain = taskConf.getWriteableContext().get(KeyName.LINE);
		Thread.sleep(1000);
		LOG.info("Line=" + domain);
	}
	
}