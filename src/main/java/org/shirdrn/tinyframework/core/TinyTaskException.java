package org.shirdrn.tinyframework.core;


/**
 * Base exception class for {@link TinyTask}, by which we can
 * control the process of {@link TinyTask} instance executing.
 * 
 * @author Yanjun
 * @version 0.2.1
 */
public class TinyTaskException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TinyTaskException() {
		super();
	}
	
	public TinyTaskException(String message) {
		super(message);
	}
	
	public TinyTaskException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
