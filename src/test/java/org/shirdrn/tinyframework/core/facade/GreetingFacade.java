package org.shirdrn.tinyframework.core.facade;

import org.shirdrn.tinyframework.core.facade.TinyFacade;

public class GreetingFacade extends TinyFacade {

	public String getGreetingWords(String toWho) {
		return "Hello, " + toWho + "!!!";
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void release() {
		// TODO Auto-generated method stub
		
	}
	
}
