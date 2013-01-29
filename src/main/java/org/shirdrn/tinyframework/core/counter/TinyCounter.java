package org.shirdrn.tinyframework.core.counter;

public interface TinyCounter {

	void incSuccessCount();
	
	int getSuccessCount();
	
	void incFailureCount();
	
	int getFailuerCount();
	
	void incSuccessTinyTaskCount();
	
	int getSuccessTinyTaskCount();
	
	void incFailureTinyTaskCount();
	
	int getFailureTinyTaskCount();
	
	void incCurrentCount();
	
	int getCurrentCount();
	
	void incTotalCount();
	
	int getTotalCount();
	
}
