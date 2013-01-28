package org.shirdrn.tinyframework.commons.core.facade;

/**
 * A facade object which implements the interface can register to 
 * {@link TinyFacadeFactory} by invoking {@link TinyFacadeFactory#register(TinyFacade, Class)}.
 * The facade object registered is only one replica in the period when managed by
 * {@link TinyFacadeFactory}. And, after we can get the registered facade object by calling
 * {@link TinyFacadeFactory#getFacadeObject(Class)}.</br></br>
 * 
 * Usually the purpose for facade object implementation is to complete a procedure independently, with or without
 * output(return value). 
 * 
 * @author Yanjun
 */
public abstract class TinyFacade {
	
	/**
	 * Initialize all resources to be used, such as loading data,
	 * opening database connections, etc.
	 */
	protected abstract void initialize();
	
	/**
	 * release all resources used in the scope of the application, such as
	 * closing database connections, saving important data produced by application,
	 * clearing temporary files, and so on.</br></br>
	 * 
	 * In internal, this method of a {@link TinyFacade} instance can be invoked by
	 * {@link TinyFacadeFactory} in a shutdown hook thread to assure all resources are
	 * released and store probably useful data.
	 */
	protected abstract void release();
}
