package org.shirdrn.tinyframework.commons.core;

public class DefaultSequenceGenerator implements SequenceGenerator {

	private volatile long seqNo = 1L;
	
	public DefaultSequenceGenerator(){
		super();
	}
	
	@Override
	public long next() {
		return ++seqNo;
	}

	@Override
	public long current() {
		return seqNo;
	}

	@Override
	public void reset(long initial) {
		seqNo = initial;		
	}

}
