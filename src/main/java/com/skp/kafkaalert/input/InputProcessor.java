package com.skp.kafkaalert.input;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.input.kafka.ConfigInputKafka;
import com.skp.kafkaalert.input.kafka.InputKafka;

import lombok.Data;

@Data
public class InputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();

	private final Config config;
	ArrayList<InputPlugin> inputPluginList = new ArrayList<>();

	public InputProcessor(Config config) {
		this.config = config;
	}

	public void init() {
		addPlugin();
		for (InputPlugin ip: inputPluginList) {
			ip.init();
		}
	}

	public void start() {
		for (InputPlugin ip: inputPluginList) {
			ip.start();
		}
//		int count = inputPluginList.size();
//		executor = Executors.newFixedThreadPool(count);
//		for (InputPlugin ip: inputPluginList) {
//			executor.submit(ip);
//		}
	}

	public void stop() {
		for (InputPlugin ip: inputPluginList) {
			ip.stop();
		}
//		executor.shutdown();
	}

	private void addPlugin() {
		ConfigInputKafka cp = objectMapper.convertValue(config.getInput(), ConfigInputKafka.class);
		inputPluginList.add(new InputKafka((ConfigInputKafka) cp));

/*		ConfigInputOld configInput = config.getConfigInput();
		for (ConfigItem cp: configInput.getConfigInputList()) {
			if (cp instanceof ConfigInputKafka) {
				inputPluginList.add(new InputKafka((ConfigInputKafka) cp));
			}
		} */
	}

}
