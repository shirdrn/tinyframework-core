package org.shirdrn.tinyframework.core.job.files;

import org.shirdrn.tinyframework.core.job.TinyJobClient;

public class FileTinyJobClient extends TinyJobClient<FileLineTinyJobRunner> {

	public FileTinyJobClient() {
		super();
	}
	
	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		TinyJobClient<FileLineTinyJobRunner> jobClient = new FileTinyJobClient();
		// start tiny job client
		jobClient.start();
	}

}
