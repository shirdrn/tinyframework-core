package org.shirdrn.tinyframework.core.counter;

public class DefaultTinyCounter implements TinyCounter {

	private volatile int successCount;
	private volatile int failureCount;
	private volatile int successTinyTaskCount;
	private volatile int failureTinyTaskCount;
	private volatile int currentCount;
	private volatile int totalCount;
	
	@Override
	public void incSuccessCount() {
		++successCount;		
	}

	@Override
	public int getSuccessCount() {
		return successCount;
	}

	@Override
	public void incFailureCount() {
		++failureCount;		
	}

	@Override
	public int getFailuerCount() {
		return failureCount;
	}

	@Override
	public void incSuccessTinyTaskCount() {
		++successTinyTaskCount;		
	}

	@Override
	public int getSuccessTinyTaskCount() {
		return successTinyTaskCount;
	}

	@Override
	public void incFailureTinyTaskCount() {
		++failureTinyTaskCount;		
	}

	@Override
	public int getFailureTinyTaskCount() {
		return failureTinyTaskCount;
	}
	
	@Override
	public void incCurrentCount() {
		++currentCount;		
	}

	@Override
	public int getCurrentCount() {
		return currentCount;
	}

	@Override
	public void incTotalCount() {
		++totalCount;		
	}

	@Override
	public int getTotalCount() {
		return totalCount;
	}

}
