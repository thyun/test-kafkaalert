package com.skp.kafkaalert.config;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.config.ConfigRegex;
import com.skp.kafkaalert.config.ConfigValue;
import com.skp.kafkaalert.entity.LogEvent;
import com.skp.util.FileHelper;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);
	ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp() {
	}

	@Test
	public void testConfig() throws IOException {
		ConfigRegex regex = ConfigRegex.create(FileHelper.getFileLineListFromResource("regex.conf"));
		logger.debug("regex=" + regex);

		Config config = Config.createFromResource("process.conf", "regex.conf");
		logger.debug("config=" + config);
//		logger.debug("schemes=" + config.getProcess().getSchemes());
	}

	@Ignore
	@Test
	public void testConfig1() throws IOException {
		ConfigInput configInput = new ConfigInput();
		ConfigProcess configProcess = new ConfigProcess();
		ArrayList<CommonFieldValue> fvs = new ArrayList<CommonFieldValue>();
		fvs.add(new CommonFieldValue("f2", "v2"));

		Config config = new Config();
		config.setInput(configInput);
		config.setProcess(configProcess);

		logger.debug("config=" + config);

		String json = objectMapper.writeValueAsString(config);
		logger.debug("json=" + json);
		logger.debug("config=" + objectMapper.readValue(json, Config.class));
	}

	@Test
	public void testConfigValue() {
		String s = "{\"date\":\"31/Jul/2018:17:48:29 +0900\",\"request\":\"/assets/PMON_icon1-a6c18ea37d8809bb7521e9594e7e758e.png?20180528\",\"referer\":\"http://pmon-dev.skplanet.com/hosts?f_field=hostname&f_service=&search=SMONi\",\"responseTime\":0.01,\"ip\":\"10.202.212.58\",\"identd\":\"-\",\"userid\":\"-\",\"byteSent\":2687,\"tags\":[\"beats_input_codec_plain_applied\"],\"responseCode\":200,\"@timestamp\":\"2018-09-13T09:06:45.151Z\",\"beat\":{\"hostname\":\"web01\",\"name\":\"web01\",\"version\":\"6.4.0\"},\"host\":{\"name\":\"web01\"},\"client\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36\"}";
		LogEvent e = new LogEvent(s);
		ConfigValue cv = ConfigValue.create(e, "request");
		logger.debug("cv=" + cv);
		cv = ConfigValue.create(e, "%{request}");
		logger.debug("cv=" + cv);
		cv = ConfigValue.create(e, "%{[host][name]}");
		logger.debug("cv=" + cv);
	}

}
