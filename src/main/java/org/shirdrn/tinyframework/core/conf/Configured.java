package org.shirdrn.tinyframework.core.conf;

/**
 * It's a {@link Context} instance holder. A {@link Context} instance
 * contains 2 type of sub-configuration objects:
 * 
 * <ol>
 * <li>{@link ReadableContext}: a global configuration object, which can not
 * modify its content in current application scope.</li>
 * <li>{@link WriteableContext}: a writeable configuration object, whose content
 * can be updated if necessary. </li>
 * </ol>
 * 
 * @author Yanjun
 */
public abstract class Configured {
	
	protected JobConf jobConf;
	
	public JobConf getJobConf() {
		return jobConf;
	}
	public void setJobConf(final JobConf jobConf) {
		this.jobConf = jobConf;
	}
	
}
