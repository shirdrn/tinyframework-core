package org.shirdrn.tinyframework.core.constants;


/**
 * At present, we support 2 modes:
 * <ol>
 * <li>
 * DEVELOPMENT(0) : development environment running mode
 * 			After files are iterated, they can not be moved to
 * 			directory 'completed'.
 * </li>
 * <li>
 * PRODUCTION(1) : production environment running mode
 * 			After files are iterated, they should be moved to 
 * 			directory 'completed' from directory 'waiting'.
 * </ol>
 * 
 * @author Yanjun
 */
public enum RunnerMode {
	
	DEVELOPMENT(0), 
	PRODUCTION(1); 
	
	private final int code;
	private static final int FIRST_CODE = values()[0].code;
	
	public int getCode() {
		return code;
	}
	
	private RunnerMode(int code) {
		this.code = code;
	}
	
	public static RunnerMode valueOf(int code) {
		final int current = (code & 0xff) - FIRST_CODE;
		return current < 0 || current >= values().length ? null : values()[current];
	}
	
}
