package org.shirdrn.tinyframework.core.job.files;

import org.junit.Test;
import org.shirdrn.tinyframework.core.conf.WriteableContext;
import org.shirdrn.tinyframework.core.job.TinyJobClient;


public class TestFileLineJobClient {

	@Test
	public void start() {
		FileTinyJobClient jobClient = new FileTinyJobClient();
		WriteableContext context = (WriteableContext) jobClient.getJobConf().getContext();
		context.set("core.job.task.classes", 
				"org.shirdrn.tinyframework.core.job.files.PrintFileLineTask," +
				"org.shirdrn.tinyframework.core.job.files.AnotherPrintFileLineTask"
		);
		context.set("commons.core.files.waiting.dir", 
				"/home/shirdrn/programs/eclipse-jee-juno/workspace/tinyframework-core/src/test/resources/org/shirdrn/tinyframework/core/job/files");
		context.set("core.job.files.suffix", ".txt");
		TinyJobClient.submitBy(FileTinyJobClient.class);
	}
	
}

