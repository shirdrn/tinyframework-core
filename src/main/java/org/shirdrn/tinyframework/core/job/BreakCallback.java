package org.shirdrn.tinyframework.core.job;
/**
 * After setting the breaking flag, {@link TinyJobRunner} could
 * know whether should terminate the {@link TinyTask} chain by the callback
 * interface.</br>
 * 
 * Usually a real task should <code>extends</code> {@link TinyTask}, and 
 * <code>implements</code> {@link BreakCallback}, only to do like that we should
 * receive the breaking signal.</br>
 * Define a task class, for example:
 * <pre>
	class ExampleTask extends TinyTask implements BreakCallback {
 
		<code>@Override</code>
		public boolean isBreak() {
			// TODO Auto-generated method stub
			return false;
		}
	
		<code>@Override</code>
		protected void execute() throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
 * </pre>
 * 
 * @author Yanjun
 */
public interface BreakCallback {

	/**
	 * If return false, the {@link TinyJobRunner} instance can ignore it and
	 * go on to execute next {@link TinyTask} instance submitted. Or the
	 * {@link TinyTask} chain is terminated at once.
	 * @return
	 */
	boolean isBreak();
	
}
