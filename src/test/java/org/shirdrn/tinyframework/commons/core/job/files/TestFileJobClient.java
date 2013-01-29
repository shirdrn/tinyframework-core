package org.shirdrn.tinyframework.commons.core.job.files;

import org.junit.Test;
import org.shirdrn.tinyframework.commons.core.conf.WriteableContext;
import org.shirdrn.tinyframework.commons.core.job.files.FileTinyJobClient;
import org.shirdrn.tinyframework.commons.core.job.files.FileLineTinyJobRunner;


public class TestFileJobClient {

	@Test
	public void start() {
		FileTinyJobClient jobClient = new FileTinyJobClient(FileLineTinyJobRunner.class);
		WriteableContext context = (WriteableContext) jobClient.getJobConf().getContext();
		context.set("commons.core.task.classes", 
				"org.shirdrn.tinyframework.commons.core.job.files.PrintFileLineTask," +
				"org.shirdrn.tinyframework.commons.core.job.files.AnotherPrintFileLineTask"
		);
		context.set("commons.core.files.waiting.dir", 
				"/home/shirdrn/programs/eclipse-jee-juno/workspace/tinyframework-core/src/test/resources/org/shirdrn/tinyframework/commons/core/job/files");
		context.set("commons.core.files.suffix", ".txt");
		jobClient.configure();
		jobClient.start();
	}
	
}

