package org.shirdrn.tinyframework.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.conf.Context;
import org.shirdrn.tinyframework.core.constants.RunnerMode;

public class DefaultTinyJobRunner extends TinyJobRunner<TinyTask, TinyJob<TinyTask>> {

	private static final Log LOG = LogFactory.getLog(DefaultTinyJobRunner.class);
	
	public DefaultTinyJobRunner() {
		super();
	}

	@Override
	protected void configure(final Context context) {
		context.addResource("core-default.xml");
		runnerMode = RunnerMode.valueOf(context.getInt("tiny.core.job.runner.mode",
				RunnerMode.PRODUCTION.getCode()));
		LOG.info("Job runner mode: " + runnerMode);	
	}
	
}
