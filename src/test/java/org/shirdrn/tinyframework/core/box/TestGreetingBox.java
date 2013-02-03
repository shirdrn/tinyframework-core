package org.shirdrn.tinyframework.core.box;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.core.box.TinyBoxException;
import org.shirdrn.tinyframework.core.box.TinyBoxFactory;

public class TestGreetingBox {
	
	@Before
	public void initialize() {
		
	}
	
	@Test
	public void register() throws TinyBoxException {
		GreetingBox f = new GreetingBox();
		TinyBoxFactory.register(f, GreetingBox.class);
	}
	
	@Test
	public void registerUnmatched() throws TinyBoxException {
		GreetingBox f = new GreetingBox();
		try {
			TinyBoxFactory.register(f, SayingBadWordsBox.class);
		} catch (Exception e) {
			assertEquals(true, e instanceof TinyBoxException);
		}
	}
	
	@Test
	public void repeatToRegister() throws TinyBoxException {
		GreetingBox f = new GreetingBox();
		TinyBoxFactory.register(f, GreetingBox.class);
		GreetingBox f2 = new GreetingBox();
		try {
			TinyBoxFactory.register(f2, GreetingBox.class);
		} catch (Exception e) {
			assertEquals(true, e instanceof TinyBoxException);
		}
	}
	
	@Test
	public void getBox() throws TinyBoxException {
		register();
		GreetingBox facade = TinyBoxFactory.getBox(GreetingBox.class);
		String greetings = facade.getGreetingWords("Jeff");
		assertEquals("Hello, Jeff!!!", greetings);
	}
}
