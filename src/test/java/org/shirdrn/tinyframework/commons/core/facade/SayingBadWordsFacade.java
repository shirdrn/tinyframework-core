package org.shirdrn.tinyframework.commons.core.facade;

import org.shirdrn.tinyframework.commons.core.facade.TinyFacade;

public class SayingBadWordsFacade extends TinyFacade {

	public String getBadWords(String toWho) {
		return "Shit, " + toWho + "!!!";
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