package org.shirdrn.tinyframework.core;

import org.shirdrn.tinyframework.core.conf.TaskConf;

public interface TinyTaskProtocol<T extends Task, J extends Job<T>> {

	/**
	 * Submit a {@link Runnable} task owned by the <code>tinyJob</code> and encapsulated in 
	 * <code>appConf</code>, invoking  by implementation class of base inteface {@link JobRunner}.</br>
	 * 
	 * Each {@link TinyTask} instance must own a name called <code>taskName</code>.
	 * @param tinyJob
	 * @param taskName
	 * @param taskConf
	 * @throws TinyTaskException
	 */
	void submitTask(final J tinyJob, final String taskName, final TaskConf taskConf) throws TinyTaskException;
}
