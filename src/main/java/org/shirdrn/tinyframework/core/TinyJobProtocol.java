package org.shirdrn.tinyframework.core;


public interface TinyJobProtocol<T extends TinyTask, J extends TinyJob<T>> {
	
	RunningTinyJob<T, J> submitJob(final TinyJobClient jobClient, J tinyJob) throws TinyJobException;

}
