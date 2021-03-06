package com.skp.kafkaalert.process;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.skp.kafkaalert.entity.LogEvent;
import lombok.Getter;

@Getter
public class ProcessQueue extends LinkedBlockingQueue<List<LogEvent>> {
	private static final long serialVersionUID = 2418692025826323462L;
	final static int QUEUE_SIZE = 1000;
	static ProcessQueue processQueue = null;

	public ProcessQueue(int queueSize) {
		super(queueSize);
	}

	public static ProcessQueue getInstance() {
		if (processQueue == null) {
			processQueue = new ProcessQueue(QUEUE_SIZE);
		}
		return processQueue;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProcessQueue: ");
		Iterator<List<LogEvent>> it = this.iterator();
		while (it.hasNext()) {
			List<LogEvent> elist = it.next();
			for (LogEvent e: elist)
				sb.append(" " + e);
			sb.append("\n");
		}
		return sb.toString();
	}
}
