package org.shirdrn.tinyframework.core.facade;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Tiny facade service factory who is responsible for managing the relationship between {@link TinyFacade}
 * instance and {@link TinyFacadeService} interface. It provides the following services:
 * <ol>
 * <li>Register a {@link TinyFacadeService} object by invoking {@link #registerService(TinyFacade, Class)}</li>
 * <li>Retrieve a {@link TinyFacadeService} object registered by invoking {@link #getService(Class)}</li>
 * </ol>
 * Finally, the {@link TinyFacadeServiceFactory} can release all resources related to each {@link TinyFacade}
 * instance by executing a shutdown hook thread when JVM exits.
 * 
 * @author Yanjun
 */
public class TinyFacadeServiceFactory {

	private static final Logger LOG = Logger.getLogger(TinyFacadeServiceFactory.class);
	private static final Map<Class<? extends TinyFacadeService>, TinyFacade> SERVICE_CONTAINER = 
			new HashMap<Class<? extends TinyFacadeService>, TinyFacade>();
	private static final Lock serviceLock = new ReentrantLock();
	
	static {
		Thread hook = new ShutdownHookThread();
		hook.setName("FACADE-SERVICE-HOOK");
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
			Iterator<Entry<Class<? extends TinyFacadeService>, TinyFacade>> iter = SERVICE_CONTAINER.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Class<? extends TinyFacadeService>, TinyFacade> entry = iter.next();
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
	 * Register a facade service object related to a given service interface which should control the
	 * exposure of service methods. Instance <code>facade</code> must be able to cast to object of
	 * class <code>facadeServiceClass</code> passed in.
	 * @param facade
	 * @param facadeServiceClass
	 */
	public static final void registerService(final TinyFacade facade, final Class<? extends TinyFacadeService> facadeServiceClass) {
		if(!(facade.getClass().getName().equals(facadeServiceClass.getName()))) {
			LOG.error("Facade object type;class=" + facade.getClass().getName());
			LOG.error("Facade class should be casted to;class=" + facadeServiceClass.getName());
			throw new RuntimeException(
					"Facade object must be able to cast to : " + facadeServiceClass.getName());
		}
		try {
			serviceLock.lock();
			if(!SERVICE_CONTAINER.containsKey(facadeServiceClass)) {
				// initialize the facade instance
				facade.initialize();
				SERVICE_CONTAINER.put(facadeServiceClass, facade);
				LOG.info("Register facade object;facadeImplClass=" + facadeServiceClass.getName());
			} else {
				throw new RuntimeException(
						"Mustn't register facade service repatedly: " + facadeServiceClass);
			}
		} finally {
			serviceLock.unlock();
		}
	}
	
	/**
	 * Get a facade service object registered before which should be casted to 
	 * class <code>facadeServiceClass</code>.
	 * @param facadeServiceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <S> S getService(Class<S> facadeServiceClass) {
		S service = null;
		service = (S) SERVICE_CONTAINER.get(facadeServiceClass);
		if(service == null) {
			throw new RuntimeException("Never register instance of class: " + facadeServiceClass);
		}
		return service;
	}
}
