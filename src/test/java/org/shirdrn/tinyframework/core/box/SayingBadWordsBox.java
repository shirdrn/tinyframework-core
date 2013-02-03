package org.shirdrn.tinyframework.core.box;

import org.shirdrn.tinyframework.core.box.TinyBox;

public class SayingBadWordsBox extends TinyBox {

	public String getBadWords(String toWho) {
		return "Shit, " + toWho + "!!!";
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
