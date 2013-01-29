package org.shirdrn.tinyframework.core.facade;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.core.facade.TinyFacadeException;
import org.shirdrn.tinyframework.core.facade.TinyFacadeFactory;

public class TestGreetingFacade {
	
	@Before
	public void initialize() {
		
	}
	
	@Test
	public void register() throws TinyFacadeException {
		GreetingFacade f = new GreetingFacade();
		TinyFacadeFactory.register(f, GreetingFacade.class);
	}
	
	@Test
	public void registerUnmatched() throws TinyFacadeException {
		GreetingFacade f = new GreetingFacade();
		try {
			TinyFacadeFactory.register(f, SayingBadWordsFacade.class);
		} catch (Exception e) {
			assertEquals(true, e instanceof TinyFacadeException);
		}
	}
	
	@Test
	public void repeatToRegister() throws TinyFacadeException {
		GreetingFacade f = new GreetingFacade();
		TinyFacadeFactory.register(f, GreetingFacade.class);
		GreetingFacade f2 = new GreetingFacade();
		try {
			TinyFacadeFactory.register(f2, GreetingFacade.class);
		} catch (Exception e) {
			assertEquals(true, e instanceof TinyFacadeException);
		}
	}
	
	@Test
	public void getFacade() throws TinyFacadeException {
		register();
		GreetingFacade facade = TinyFacadeFactory.getFacade(GreetingFacade.class);
		String greetings = facade.getGreetingWords("Shirdrn");
		assertEquals("Hello, Shirdrn!!!", greetings);
	}
}
