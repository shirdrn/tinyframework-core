package org.shirdrn.tinyframework.core;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TextFileTinyIterator extends TinyIterator<String> {

	private static final Log LOG = LogFactory.getLog(TextFileTinyIterator.class);
	private final BufferedReader reader;
	private String line = null;
	
	public TextFileTinyIterator(BufferedReader reader) {
		super();
		this.reader = reader;
	}

	@Override
	public boolean hasNext() {
		boolean hasNextLine = false;
		try {
			line = reader.readLine();
			hasNextLine = (line != null);
			LOG.debug("hasNextLine=" + hasNextLine + ",line=" + line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hasNextLine;
	}

	@Override
	public String next() {
		// TODO Auto-generated method stub
		return line;
	}

}
