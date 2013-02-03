package org.shirdrn.tinyframework.core;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.core.box.TinyBoxException;
import org.shirdrn.tinyframework.core.box.TinyBoxFactory;
import org.shirdrn.tinyframework.core.conf.Context;
import org.shirdrn.tinyframework.core.conf.JobConf;
import org.shirdrn.tinyframework.core.conf.ReadableContext;
import org.shirdrn.tinyframework.core.utils.ObjectFactory;

public class TinyJobClient extends Thread {
	
	private static final Log LOG = LogFactory.getLog(TinyJobClient.class);
	private final TinyJobRunner<? extends TinyTask, TinyJob<? extends TinyTask>> jobRunner;
	private final static TinyJobClient CLIENT = new TinyJobClient();
	private static DecimalFormat formatter = new DecimalFormat("00");
	
	static {
		// try to start job runner
		if(!CLIENT.jobRunner.isRunning()) {
			CLIENT.jobRunner.startRunner();
		}
	}
	
	@SuppressWarnings("unchecked")
	private TinyJobClient() {
		super();
		ReadableContext context = TinyJobRunner.getCoreContext();
		String jobRunnerClass = context.get("tiny.core.job.runner.class", 
				"org.shirdrn.tinyframework.core.DefaultTinyJobRunner");
		LOG.info("Load job runner class: " + jobRunnerClass);
		
		jobRunner = ObjectFactory.getInstance(jobRunnerClass, 
				TinyJobRunner.class, this.getClass().getClassLoader());
		LOG.info("Create job runner instance: " + jobRunner);
		
		// initialize boxes
		try {
			initializeBoxes();
		} catch (TinyBoxException e) {
			LOG.info("Fail to initialize boxes!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void initializeBoxes() throws TinyBoxException {
		final Context boxContext = new Context(false);
		boxContext.addResource("core-box.xml");
		Iterator<Entry<String, String>> iter = boxContext.iterator();
		
		while(iter.hasNext()) {
			Entry<String, String> pair = iter.next();
			String name = pair.getKey();
			String boxClass = pair.getValue();
			TinyBoxFactory.register(name, boxClass, this.getClass().getClassLoader());
		}
		LOG.info("Create box instances: boxCount=" + boxContext.size());
	}

	public void submitJob(final TinyJob<? extends TinyTask> tinyJob) {
		try {
			if(!this.isAlive()) {
				throw new TinyJobException("Tiny job client isn't started!");
			}
			checkJob(tinyJob);
			prepareForJob(tinyJob);
			final TinyJobProtocol<? extends TinyTask, TinyJob<? extends TinyTask>> jobProtocol = this.jobRunner;
			RunningTinyJob<? extends TinyTask, TinyJob<? extends TinyTask>> runningJob = jobProtocol.submitJob(this, tinyJob);
			
			
		} catch (TinyJobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void prepareForJob(TinyJob<? extends TinyTask> tinyJob) throws TinyJobException {
		JobConf jobConf = tinyJob.getJobConf();
		jobConf.addJobResource("core-job.xml");
		String taskClasses = jobConf.getContext().get("tiny.core.job.task.classes");
		if(taskClasses == null || taskClasses.trim().isEmpty()) {
			throw new TinyJobException("tiny.core.job.task.classes == null");
		}
		String[] aClass = taskClasses.trim().split("[,\\|\\s]+");
		long number = 0;
		for(String className : aClass) {
			if(!className.trim().isEmpty()) {
				Class<? extends TinyTask> taskClass = 
						ObjectFactory.getClass(className, TinyTask.class, tinyJob.getClass().getClassLoader());
				String taskId = formatter.format(++number);
				tinyJob.registerTask(taskId, taskClass);
				LOG.info("Load and register tiny task: taskId=" + taskId + ",taskClass=" + className);
			}
		}
	}

	private static void checkJob(TinyJob<? extends TinyTask> tinyJob) throws TinyJobException {
		if(tinyJob == null) {
			throw new TinyJobException("tinyJob == null");
		}
		if(tinyJob.getJobConf() == null) {
			throw new TinyJobException("jobConf == null");
		}
		
	}
	
	public static synchronized TinyJobClient startup() {
		if(!CLIENT.isAlive()) {
			CLIENT.setName("CLIENT");
			CLIENT.start();
		} else {
			LOG.info("Tiny job client has started!");
		}
		return CLIENT;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOG.info("Job client exits.");
		}
	}
	
	
}
