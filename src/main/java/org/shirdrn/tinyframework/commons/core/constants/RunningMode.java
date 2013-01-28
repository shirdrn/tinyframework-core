package org.shirdrn.tinyframework.commons.core.constants;


/**
 * At present, we support 2 modes:
 * <ol>
 * <li>
 * DEVP(0) : development environment running mode
 * 			After files are iterated, they can not be moved to
 * 			directory 'completed'.
 * </li>
 * <li>
 * PROD(1) : production environment running mode
 * 			After files are iterated, they should be moved to 
 * 			directory 'completed' from directory 'waiting'.
 * </ol>
 * 
 * @author Yanjun
 */
public enum RunningMode {
	
	DEVP(0), 
	PROD(1); 
	
	private final int code;
	private static final int FIRST_CODE = values()[0].code;
	
	public int getCode() {
		return code;
	}
	
	private RunningMode(int code) {
		this.code = code;
	}
	
	public static RunningMode valueOf(int code) {
		final int current = (code & 0xff) - FIRST_CODE;
		return current < 0 || current >= values().length ? null : values()[current];
	}
	
}
