package org.shirdrn.tinyframework.core.box;

import org.shirdrn.tinyframework.core.box.TinyBox;

public class GreetingBox extends TinyBox {

	public String getGreetingWords(String toWho) {
		return "Hello, " + toWho + "!!!";
	}

	@Override
	protected void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}
