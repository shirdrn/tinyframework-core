package org.shirdrn.tinyframework.commons.core.job.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.JobConf;
import org.shirdrn.tinyframework.commons.core.constants.RunningMode;
import org.shirdrn.tinyframework.commons.core.job.TinyJobRunner;
import org.shirdrn.tinyframework.commons.core.job.TinyTask;

public abstract class FileTinyJobRunner extends TinyJobRunner<TinyTask> 
	implements TextLineService {

	private static final Log LOG = LogFactory.getLog(FileTinyJobRunner.class);
	protected File waitingDir;
	protected File completedDir;
	protected String suffix;
	protected String charSet = Charset.defaultCharset().toString();
	
	public FileTinyJobRunner(JobConf jobConf) {
		super(jobConf);
	}
	
	@Override
	public void configure() {
		super.configure();
		// configure waiting directory
		String waitingPath = jobConf.getContext().get("commons.core.files.waiting.dir");
		waitingDir = new File(waitingPath);
		if(!waitingDir.exists()) {
			throw new RuntimeException("Waiting directory doesn't exist: " + waitingPath);
		}
		LOG.info("Waiting directory;dir=" + waitingDir.getAbsolutePath());
		// configure completed directory
		String completedPath = jobConf.getContext().get("commons.core.files.completed.dir");
		completedDir = new File(completedPath);
		if(!completedDir.exists()) {
			completedDir.mkdirs();
		}
		LOG.info("Completed directory;dir=" + completedDir.getAbsolutePath());
		// configure file suffix
		suffix = jobConf.getContext().get("commons.core.files.suffix");
		LOG.info("File suffix name;suffix=" + suffix);
		// configure file charset
		String encoding = jobConf.getContext().get("commons.core.files.charset");
		if(encoding!=null) {
			charSet = encoding;
			LOG.info("File encoding;charSet=" + charSet);
		} else {
			LOG.info("Use default file encoding;charSet=" + charSet);
		}
	}
	
	@Override
	protected void iterate() {
		try {
			File[] files = prepareWaitingFiles();
			for(File file : files) {
				LOG.info("Process file;file=" + file.getAbsolutePath());	
				FileInputStream fis = null;
				BufferedReader reader = null;
				boolean isOk = true;
				try {
					fis = new FileInputStream(file.getAbsolutePath());
					reader = new BufferedReader(new InputStreamReader(fis, Charset.defaultCharset()));
					String line = null;
					while((line=reader.readLine())!=null) {
						try {
							doLine(line.trim());
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error("Error to read domains; file=" + file.getAbsolutePath() + "," + "exception=" + e.getMessage());
							isOk = false;
						}
					}
				} finally {
					if(fis!=null) {
						fis.close();
					}
					if(reader!=null) {
						reader.close();
					}
					
					// move to completed directory?
					if(runningMode == RunningMode.PROD && isOk) {
						File completedFile = new File(completedDir, file.getName());
						file.renameTo(completedFile);
						LOG.info("Move file;action=RENAME" + ",src=" + file + ",dst=" + completedFile);
					} else {
						File completedFile = new File(completedDir, file.getName());
						LOG.info("Move file;action=IGNORE" + ",src=" + file + ",dst=" + completedFile);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File[] prepareWaitingFiles() {
		if(suffix==null) {
			return waitingDir.listFiles();
		}
		// file suffix configured
		File[] files = waitingDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(suffix)) {
					return true;
				}
				return false;
			}
		});
		return files;
	}


	@Override
	public void doLine(String line) {
		// TODO Auto-generated method stub
		
	}

}
