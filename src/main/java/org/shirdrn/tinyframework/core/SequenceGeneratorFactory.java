package org.shirdrn.tinyframework.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class SequenceGeneratorFactory {

	private static final Logger LOG = Logger.getLogger(SequenceGeneratorFactory.class);
	private static final Map<Object, SequenceGenerator> SEQ_GENERATOR_CONTAINER = 
			new HashMap<Object, SequenceGenerator>();
	
	public static SequenceGenerator getSequenceGenerator(Object holder) {
		SequenceGenerator sg = SEQ_GENERATOR_CONTAINER.get(holder);
		if(sg == null) {
			throw new RuntimeException("Never register a sequence generator for: " + holder);
		}
		return sg;
	}
	
	public static synchronized void createFor(Object holder) {
		if(!SEQ_GENERATOR_CONTAINER.containsKey(holder)) {
			SequenceGenerator generator = new DefaultSequenceGenerator();
			SEQ_GENERATOR_CONTAINER.put(holder, generator);
			LOG.info("Registration;holder=" + holder + ",generator=" + SEQ_GENERATOR_CONTAINER.get(holder));
		} else {
			String message = "Only permit to register one sequence generator for: " + holder;
			LOG.error(message);
			LOG.error("Existed registration;holder=" + holder + ",generator=" + SEQ_GENERATOR_CONTAINER.get(holder));
			throw new RuntimeException(message);
		}
	}
}
