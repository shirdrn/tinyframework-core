package org.shirdrn.tinyframework.core.box;

import org.shirdrn.tinyframework.core.conf.JobConf;

/**
 * A box object which implements the interface can register to 
 * {@link TinyBoxFactory} by invoking {@link TinyBoxFactory#register(TinyBox, Class)}.
 * The box object registered is only one replica in the period when managed by
 * {@link TinyBoxFactory}. And, after we can get the registered box object by calling
 * {@link TinyBoxFactory#getFacadeObject(Class)}.</br></br>
 * 
 * Usually the purpose for box object implementation is to complete a procedure independently, with or without
 * output(return value). 
 * 
 * @author Yanjun
 */
public abstract class TinyBox implements Box {
	
	protected JobConf jobConf;
	
	/**
	 * Initialize all resources to be used, such as loading data,
	 * opening database connections, etc.
	 */
	protected abstract void create();
	
	/**
	 * Release all resources used in the scope of the application, such as
	 * closing database connections, saving important data produced by application,
	 * clearing temporary files, and so on.</br></br>
	 * 
	 * In internal, this method of a {@link TinyBox} instance can be invoked by
	 * {@link TinyBoxFactory} in a shutdown hook thread to assure all resources are
	 * released and store probably useful data.
	 */
	protected abstract void destroy();
}
