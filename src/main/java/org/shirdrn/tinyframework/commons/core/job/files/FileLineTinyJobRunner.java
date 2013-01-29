package org.shirdrn.tinyframework.commons.core.job.files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.JobConf;
import org.shirdrn.tinyframework.commons.core.conf.TaskConf;
import org.shirdrn.tinyframework.commons.core.constants.KeyName;
import org.shirdrn.tinyframework.commons.core.job.TinyTaskException;

public class FileLineTinyJobRunner extends FileTinyJobRunner {

	public FileLineTinyJobRunner(JobConf jobConf) {
		super(jobConf);
	}

	private static final Log LOG = LogFactory.getLog(FileLineTinyJobRunner.class);
	
	@Override
	public void doLine(String line) {
		if(line!=null && !line.trim().isEmpty()) {
			line = line.trim();
			LOG.info("Read;line=" + line);
			// submit task
			try {
				TaskConf taskConf = new TaskConf();
				taskConf.getWriteableContext().set(KeyName.LINE, line);
				submitTask(line, taskConf);
			} catch (TinyTaskException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void releaseAll() {
		// TODO Auto-generated method stub
		
	}
	
}
