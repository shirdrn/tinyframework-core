package org.shirdrn.tinyframework.core;

public interface JobClientProtocol<T extends TinyTask, J extends TinyJob<T>> 
	extends TinyJobProtocol<T, J>, TinyTaskProtocol<T, J> {

}
