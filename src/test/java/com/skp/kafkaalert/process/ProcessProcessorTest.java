package com.skp.kafkaalert.process;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.datastore.AlarmMetaDatastore;
import com.skp.kafkaalert.datastore.AlarmStatsDatastore;
import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.generator.ProcessQueueGenerator;
import com.skp.kafkaalert.process.ProcessProcessor;
import com.skp.kafkaalert.process.ProcessQueue;

public class ProcessProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(ProcessProcessorTest.class);

	@Before
	public void setUp() {
	}

	@Test
	public void testAlarmMeta() {
		Config config = Config.createFromResource("process.conf", "regex.conf");
		AlarmMetaDatastore.getInstance().build(config.getProcess());
		logger.debug("fieldKeyList=" + AlarmMetaDatastore.getInstance().getFieldKeyList());
		logger.debug("alarmMap=" + AlarmMetaDatastore.getInstance().getAlarmMap());
	}

	@Test
	public void testSystemMetric() throws IOException, ParseException {
		// Get config
		Config config = Config.createFromResource("process.conf", "regex.conf");
		AlarmMetaDatastore.getInstance().build(config.getProcess());
		logger.debug("fieldKeyList=" + AlarmMetaDatastore.getInstance().getFieldKeyList());
		logger.debug("alarmMap=" + AlarmMetaDatastore.getInstance().getAlarmMap());

		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();

		// Generate sample system metric and process
		ProcessQueueGenerator.generateSampleMetric("sample-system-metric.log");
		int size = ProcessQueue.getInstance().size();
		for (int i=0; i<size; i++)
			pprocess.process();
		logger.debug("Processed queue size=" + size);

		// CPU alarm(id=1): on, off
		// CPU alarm(id=2): on, off
		// Network alarm(id=3): on, remind, remind, off
		assertEquals(3, AlarmStatsDatastore.getInstance().getOnCount());
		assertEquals(2, AlarmStatsDatastore.getInstance().getRemindCount());
		assertEquals(3, AlarmStatsDatastore.getInstance().getOffCount());

/*		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0); */
	}

}
