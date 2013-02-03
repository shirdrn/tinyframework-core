package org.shirdrn.tinyframework.core;

import org.shirdrn.tinyframework.core.conf.Configured;
import org.shirdrn.tinyframework.core.conf.JobConf;

public abstract class TinyDataSource<O> extends Configured {

	protected TinyIterator<O> iterator;

	@Override
	public void setJobConf(JobConf jobConf) {
		super.setJobConf(jobConf);
	}
	
	public TinyIterator<O> getIterator() {
		return iterator;
	}

	public abstract void open();
	
	public abstract void close();

}
