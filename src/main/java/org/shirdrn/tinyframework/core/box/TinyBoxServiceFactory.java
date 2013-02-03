package org.shirdrn.tinyframework.core.box;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Tiny box service factory who is responsible for managing the relationship between {@link TinyBox}
 * instance and {@link TinyBoxService} interface. It provides the following services:
 * <ol>
 * <li>Register a {@link TinyBoxService} object by invoking {@link #registerService(TinyBox, Class)}</li>
 * <li>Retrieve a {@link TinyBoxService} object registered by invoking {@link #getService(Class)}</li>
 * </ol>
 * Finally, the {@link TinyBoxServiceFactory} can release all resources related to each {@link TinyBox}
 * instance by executing a shutdown hook thread when JVM exits.
 * 
 * @author Yanjun
 */
public class TinyBoxServiceFactory {

	private static final Logger LOG = Logger.getLogger(TinyBoxServiceFactory.class);
	private static final Map<Class<? extends TinyBoxService>, TinyBox> SERVICE_CONTAINER = 
			new HashMap<Class<? extends TinyBoxService>, TinyBox>();
	private static final Lock serviceLock = new ReentrantLock();
	
	static {
		Thread hook = new ShutdownHookThread();
		hook.setName("BOX-SERVICE-HOOK");
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	/**
	 * A shutdown hook thread to release all resources related to each
	 * {@link TinyBox} instance.
	 * 
	 * @author Yanjun
	 */
	static class ShutdownHookThread extends Thread {
		@Override
		public void run() {
			Iterator<Entry<Class<? extends TinyBoxService>, TinyBox>> iter = SERVICE_CONTAINER.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Class<? extends TinyBoxService>, TinyBox> entry = iter.next();
				try {
					entry.getValue().destroy();
					LOG.info("Rlease resources;box=" + entry.getKey().getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Register a box service object related to a given service interface which should control the
	 * exposure of service methods. Instance <code>box</code> must be able to cast to object of
	 * class <code>boxServiceClass</code> passed in.
	 * @param box
	 * @param boxServiceClass
	 */
	public static final void registerService(final TinyBox box, final Class<? extends TinyBoxService> boxServiceClass) {
		if(!(box.getClass().getName().equals(boxServiceClass.getName()))) {
			LOG.error("Box object type;class=" + box.getClass().getName());
			LOG.error("Box class should be casted to;class=" + boxServiceClass.getName());
			throw new RuntimeException(
					"Box object must be able to cast to : " + boxServiceClass.getName());
		}
		try {
			serviceLock.lock();
			if(!SERVICE_CONTAINER.containsKey(boxServiceClass)) {
				// initialize the box instance
				box.create();
				SERVICE_CONTAINER.put(boxServiceClass, box);
				LOG.info("Register box object;boxImplClass=" + boxServiceClass.getName());
			} else {
				throw new RuntimeException(
						"Mustn't register box service repatedly: " + boxServiceClass);
			}
		} finally {
			serviceLock.unlock();
		}
	}
	
	/**
	 * Get a box service object registered before which should be casted to 
	 * class <code>boxServiceClass</code>.
	 * @param boxServiceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <S> S getService(Class<S> boxServiceClass) {
		S service = null;
		service = (S) SERVICE_CONTAINER.get(boxServiceClass);
		if(service == null) {
			throw new RuntimeException("Never register instance of class: " + boxServiceClass);
		}
		return service;
	}
}
