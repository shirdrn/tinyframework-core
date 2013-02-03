package org.shirdrn.tinyframework.core;

import java.io.IOException;

import org.junit.Test;


public class TestTextFileTinyJob {

	@Test
	public void print() {
		TinyJob<TinyTask> job = new TinyJob<TinyTask>();
		job.setName("PrintTextLineJob");
		job.getJobConf().addJobResource("test-file-job.xml");
		TinyJobClient jobClient = TinyJobClient.startup();
		jobClient.submitJob(job);
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
