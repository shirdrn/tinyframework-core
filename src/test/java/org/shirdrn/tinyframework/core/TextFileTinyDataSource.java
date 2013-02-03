package org.shirdrn.tinyframework.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TextFileTinyDataSource extends TinyDataSource<String> {

	private static final Log LOG = LogFactory.getLog(TextFileTinyDataSource.class);
	private BufferedReader reader;

	public TextFileTinyDataSource() {
		super();
	}

	@Override
	public void open() {
		String fileName = jobConf.getContext().get("tiny.core.job.file.name");
		LOG.info("File name: " + fileName);
		InputStream in = this.getClass().getResourceAsStream(fileName);
		if(fileName != null) {
			reader = new BufferedReader(new InputStreamReader(in));
			this.iterator = new TextFileTinyIterator(reader);
		}		
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
