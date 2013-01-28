package org.shirdrn.tinyframework.commons.core;

public interface SequenceGenerator {

	/**
	 * Generate a unique and increment sequence number.
	 * @return
	 */
	long next();
	
	/**
	 * Current sequence number.
	 * @return
	 */
	long current();
	
	/**
	 * Reset sequence number to the given initial value.
	 * @param initial
	 */
	void reset(long initial);
	
}
