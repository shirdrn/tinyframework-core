package org.shirdrn.tinyframework.core;

import java.util.Random;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.core.constants.KeyName;

public class PrintLineTask extends TinyTask {

	private static final Logger LOG = Logger.getLogger(PrintLineTask.class);
	private Random random = new Random();
	
	@Override
	protected void execute() throws TinyTaskException {
		String line = (String) taskConf.getContext().getObject(KeyName.OBJECT);
		LOG.info("line=" + line);
		try {
			Thread.sleep(random.nextInt(2000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
