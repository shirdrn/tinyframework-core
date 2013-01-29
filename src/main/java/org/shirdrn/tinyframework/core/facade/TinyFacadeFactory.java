package org.shirdrn.tinyframework.core.facade;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Tiny facade factory who is responsible for managing {@link TinyFacade}
 * instances. It provides the following services:
 * <ol>
 * <li>Register a {@link TinyFacade} object by invoking {@link #register(TinyFacade, Class)}</li>
 * <li>Retrieve a {@link TinyFacade} object registered by invoking {@link #getFacade(Class)}</li>
 * </ol>
 * Finally, the {@link TinyFacadeFactory} can release all resources related to each {@link TinyFacade}
 * instance by executing a shutdown hook thread when JVM exits.
 * 
 * @author Yanjun
 */
public class TinyFacadeFactory {

	private static final Logger LOG = Logger.getLogger(TinyFacadeFactory.class);
	private static final Map<Class<? extends TinyFacade>, TinyFacade> FACADE_CONTAINER = 
			new HashMap<Class<? extends TinyFacade>, TinyFacade>();
	private static final Lock lock = new ReentrantLock();
	
	static {
		Thread hook = new ShutdownHookThread();
		hook.setName("FACADE-HOOK");
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	/**
	 * A shutdown hook thread to release all resources related to each
	 * {@link TinyFacade} instance.
	 * 
	 * @author Yanjun
	 */
	static class ShutdownHookThread extends Thread {
		@Override
		public void run() {
			Iterator<Entry<Class<? extends TinyFacade>, TinyFacade>> iter = FACADE_CONTAINER.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Class<? extends TinyFacade>, TinyFacade> entry = iter.next();
				try {
					entry.getValue().release();
					LOG.info("Rlease resources;facade=" + entry.getKey().getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get a facade instance registered before which should be casted to class <code>facadeImplClass</code>.
	 * @param facadeImplClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <F> F getFacade(Class<F> facadeImplClass) {
		F facade = null;
		facade = (F) FACADE_CONTAINER.get(facadeImplClass);
		if(facade == null) {
			throw new RuntimeException("Never register instance of class: " + facadeImplClass);
		}
		return facade;
	}
	
	/**
	 * Register a facade object. Instance <code>facade</code> must be able to cast to object of
	 * class <code>facadeImplClass</code> passed in.
	 * @param facade
	 * @param facadeImplClass
	 */
	public static final void register(final TinyFacade facade, final Class<? extends TinyFacade> facadeImplClass) {
		if(!(facade.getClass().getName().equals(facadeImplClass.getName()))) {
			LOG.error("Facade object type;class=" + facade.getClass().getName());
			LOG.error("Facade class should be casted to;class=" + facadeImplClass.getName());
			throw new RuntimeException(
					"Facade object must be able to cast to : " + facadeImplClass.getName());
		}
		try {
			lock.lock();
			if(!FACADE_CONTAINER.containsKey(facadeImplClass)) {
				// initialize the facade instance
				facade.initialize();
				FACADE_CONTAINER.put(facadeImplClass, facade);
				LOG.info("Register facade object;facadeImplClass=" + facadeImplClass.getName());
			} else {
				throw new RuntimeException(
						"Mustn't register instance of the facade implementation class repatedly: " + facadeImplClass);
			}
		} finally {
			lock.unlock();
		}
	}

}
