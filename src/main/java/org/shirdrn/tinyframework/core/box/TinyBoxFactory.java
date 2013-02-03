package org.shirdrn.tinyframework.core.box;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;

/**
 * Tiny box factory who is responsible for managing {@link TinyBox}
 * instances. It provides the following services:
 * <ol>
 * <li>Register a {@link TinyBox} object by invoking {@link #register(TinyBox, Class)}</li>
 * <li>Retrieve a {@link TinyBox} object registered by invoking {@link #getBox(Class)}</li>
 * </ol>
 * Finally, the {@link TinyBoxFactory} can release all resources related to each {@link TinyBox}
 * instance by executing a shutdown hook thread when JVM exits.
 * 
 * @author Yanjun
 */
public class TinyBoxFactory {

	private static final Logger LOG = Logger.getLogger(TinyBoxFactory.class);
	private static final Map<Class<? extends TinyBox>, TinyBox> BOX_CONTAINER = 
			new HashMap<Class<? extends TinyBox>, TinyBox>();
	private static final Map<String, TinyBox> BOX_NAME_CONTAINER = 
			new HashMap<String, TinyBox>();
	private static final Lock lock = new ReentrantLock();
	
	static {
		Thread hook = new ShutdownHookThread();
		hook.setName("BOX-HOOK");
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
			Iterator<Entry<Class<? extends TinyBox>, TinyBox>> iter = BOX_CONTAINER.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Class<? extends TinyBox>, TinyBox> entry = iter.next();
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
	 * Get a box instance registered before which should be casted to class <code>boxImplClass</code>.
	 * @param boxImplClass
	 * @return
	 * @throws TinyBoxException 
	 */
	@SuppressWarnings("unchecked")
	public static final <F> F getBox(Class<F> boxImplClass) throws TinyBoxException {
		F box = null;
		box = (F) BOX_CONTAINER.get(boxImplClass);
		if(box == null) {
			throw new TinyBoxException("Never register instance of class: " + boxImplClass);
		}
		return box;
	}
	
	/**
	 * Get a box instance registered which <code>name</code> as the key.
	 * @param name
	 * @return
	 * @throws TinyBoxException 
	 */
	public static final TinyBox getBox(String name) throws TinyBoxException {
		TinyBox box = BOX_NAME_CONTAINER.get(name);
		if(box == null) {
			throw new TinyBoxException("Never register box instance with name: " + name);
		}
		return box;
	}
	
	/**
	 * Register a box object. Instance <code>box</code> must be able to cast to object of
	 * class <code>boxImplClass</code> passed in.
	 * @param box
	 * @param boxImplClass
	 * @throws TinyBoxException 
	 */
	public static final void register(final TinyBox box, final Class<? extends TinyBox> boxImplClass) throws TinyBoxException {
		if(!(box.getClass().getName().equals(boxImplClass.getName()))) {
			LOG.error("Box object type;class=" + box.getClass().getName());
			LOG.error("Box class should be casted to;class=" + boxImplClass.getName());
			throw new TinyBoxException(
					"Box object must be able to cast to : " + boxImplClass.getName());
		}
		try {
			lock.lock();
			if(!BOX_CONTAINER.containsKey(boxImplClass)) {
				// initialize the box instance
				box.create();
				BOX_CONTAINER.put(boxImplClass, box);
				LOG.info("Register box object;boxImplClass=" + boxImplClass.getName());
			} else {
				throw new TinyBoxException(
						"Mustn't register instance of the box implementation class repatedly: " + boxImplClass);
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Register a box object. Instance of class <code>boxClass</code> must be able to cast to object of
	 * class <code>{@link TinyBox}</code>.
	 * @param name
	 * @param boxClass
	 * @param classLoader
	 * @throws TinyBoxException 
	 */
	public static final void register(final String name, final String boxClass, ClassLoader classLoader) throws TinyBoxException {
		Object obj = ObjectFactory.getInstance(boxClass, classLoader);
		if(name == null || name.trim().isEmpty()) {
			throw new TinyBoxException(
					"Box object must have a unique identifier!");
		}
		if(!(obj instanceof TinyBox)) {
			LOG.error("Box object type;class=" + obj.getClass().getName());
			LOG.error("Box class should be casted to;class=" + TinyBox.class.getName());
			throw new TinyBoxException(
					"Box object must be able to cast to : " + TinyBox.class.getName());
		}
		try {
			lock.lock();
			if(!BOX_NAME_CONTAINER.containsKey(name)) {
				// initialize the box instance
				TinyBox box = (TinyBox) obj;
				box.create();
				BOX_NAME_CONTAINER.put(name, box);
				LOG.info("Register box object;boxClass=" + box.getClass().getName());
			} else {
				throw new RuntimeException(
						"Mustn't register box with the same name: " + name);
			}
		} finally {
			lock.unlock();
		}
	}

}
