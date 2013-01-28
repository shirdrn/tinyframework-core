package org.shirdrn.tinyframework.commons.core.job.files;

import org.shirdrn.tinyframework.commons.core.job.TinyJobClient;

public class FileTinyJobClient extends TinyJobClient<FileLineTinyJobRunner> {

	public FileTinyJobClient(Class<FileLineTinyJobRunner> jobRunnerClass) {
		super(jobRunnerClass);
	}

	public static void main(String[] args) {
		TinyJobClient<FileLineTinyJobRunner> jobClient = new FileTinyJobClient(FileLineTinyJobRunner.class);
		
		// configure tiny job client
		jobClient.configure();
		
		// start tiny job client
		jobClient.start();
	}

}
