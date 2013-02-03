package org.shirdrn.tinyframework.core.sequence;

public class DefaultSequenceGenerator implements SequenceGenerator {

	private volatile long seqNo = 0L;
	
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
