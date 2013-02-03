package org.shirdrn.tinyframework.core;


/**
 * Base exception class for {@link TinyJob}, by which we can
 * control the exceptional events when {@link TinyJob} instance throws a
 * {@link Exception}.
 * 
 * @author Yanjun
 */
public class TinyJobException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TinyJobException() {
		super();
	}
	
	public TinyJobException(String message) {
		super(message);
	}
	
	public TinyJobException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
