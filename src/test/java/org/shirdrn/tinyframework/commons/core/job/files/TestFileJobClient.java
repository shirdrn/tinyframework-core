package org.shirdrn.tinyframework.commons.core.job.files;

import org.junit.Test;
import org.shirdrn.tinyframework.commons.core.conf.WriteableContext;
import org.shirdrn.tinyframework.commons.core.job.files.FileTinyJobClient;
import org.shirdrn.tinyframework.commons.core.job.files.FileLineTinyJobRunner;


public class TestFileJobClient {

	@Test
	public void start() {
		FileTinyJobClient jobClient = new FileTinyJobClient(FileLineTinyJobRunner.class);
		WriteableContext context = (WriteableContext) jobClient.getReadableContext();
		context.set("commons.core.task.classes", 
				"org.shirdrn.tinyframework.commons.core.job.files.PrintFileLineTask," +
				"org.shirdrn.tinyframework.core.job.files.AnotherPrintFileLineTask"
		);
		context.set("commons.core.files.waiting.dir", 
				"waiting");
		context.set("commons.core.files.suffix", ".java");
		jobClient.configure();
		jobClient.start();
	}
	
}

