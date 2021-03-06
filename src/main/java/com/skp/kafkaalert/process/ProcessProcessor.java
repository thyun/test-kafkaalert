package com.skp.kafkaalert.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.output.OutputProcessor;

import lombok.Data;

@Data
public class ProcessProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Config config;
	ProcessAlarm processAlarm = new ProcessAlarm();

	List<ProcessConsumer> consumerList = new ArrayList<>();
	ExecutorService executor;
	OutputProcessor outputProcessor;

	class ProcessConsumer implements Runnable {
		@Override
		public void run() {
			logger.debug("ProcessConsumer.run() start");
			while (true) {
				process();
			}

		}
	}

	public ProcessProcessor(Config config) {
		this.config = config;
	}

	public void init() {
		int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        for (int i=0; i<numConsumers; i++) {
        	consumerList.add(new ProcessConsumer());
        }
	}

	public void start() {
		for (ProcessConsumer consumer: consumerList)
			executor.submit(consumer);
	}

	public void stop() {
		executor.shutdown();
		try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
        	logger.error("Error", ex);
        }
	}

	public void process() {
		ArrayList<LogEvent> outList = new ArrayList<>();
		try {
			List<LogEvent> elist = ProcessQueue.getInstance().take();
//			logger.debug("elist size=" + elist.size());
			for (LogEvent e: elist) {
				LogEvent out = process(config, e);
				if (out != null)
					outList.add(out);
			}
		} catch (Exception ex) {
			logger.error("Error", ex);
		}

		if (outputProcessor != null && outList.size() > 0)
			outputProcessor.put(outList);
	}

	private LogEvent process(Config config, LogEvent e) {
//		logger.debug("process() start: e=" + e);
		LogEvent out=e;


		ConfigProcess cprocess = config.getProcess();
		processAlarm.process(cprocess, e);

//		logger.debug("ProcessProcessor.process() end");
		return null;
	}

}
