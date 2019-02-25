package com.skp.kafkaalert.output;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.entity.LogEvent;

import lombok.Data;

@Data
public class OutputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();

	private final Config config;
	ArrayList<OutputPlugin> outputPluginList = new ArrayList<>();

	public OutputProcessor(Config config) {
		this.config = config;
	}

	public void init() {
		addPlugin();
		for (OutputPlugin ip: outputPluginList) {
			ip.init();
		}
	}

	public void start() {
		for (OutputPlugin ip: outputPluginList) {
			ip.start();
		}
	}

	public void stop() {
		for (OutputPlugin ip: outputPluginList) {
			ip.stop();
		}
	}

	public void put(List<LogEvent> elist) {
		for (OutputPlugin op: outputPluginList) {
			try {
				op.getOutputQueue().put(elist);
			} catch (InterruptedException ex) {
				logger.error("Error", ex);
			}
		}
	}

	private void addPlugin() {
		ConfigOutputFile cp = objectMapper.convertValue(config.getOutput(), ConfigOutputFile.class);
		outputPluginList.add(new OutputFile((ConfigOutputFile) cp));

/*		ConfigOutputOld configOutput = config.getConfigOutput();
		for (ConfigItem cp: configOutput.getConfigOutputList()) {
			if (cp instanceof ConfigOutputFile) {
				outputPluginList.add(new OutputFile((ConfigOutputFile) cp));
			} else if (cp instanceof ConfigOutputKafka) {
				outputPluginList.add(new OutputKafka((ConfigOutputKafka) cp));
			}
		} */
	}

}
