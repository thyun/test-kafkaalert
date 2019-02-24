package com.skp.kafkaalert.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.datastore.AlarmMetaDatastore;
import com.skp.kafkaalert.event.LogEvent;
import com.skp.kafkaalert.input.GeneralConsumerTest;
import com.skp.kafkaalert.process.ProcessMetricsService;
import com.skp.kafkaalert.process.ProcessProcessor;
import com.skp.kafkaalert.process.ProcessQueue;
import com.skp.util.FileHelper;
import com.skp.util.StreamFileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class ProcessProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(ProcessProcessorTest.class);

	@Before
	public void setUp() {
	}

	@Test
	public void testAlarmMeta() {
		Config config = Config.createFromResource("process.conf", "regex.conf");
		AlarmMetaDatastore.getInstance().build(config.getProcess());
		logger.debug("lookupMap=" + AlarmMetaDatastore.getInstance().getLookupMap());
		logger.debug("alarmMap=" + AlarmMetaDatastore.getInstance().getAlarmMap());
	}

	@Test
	public void testSystemMetric() throws IOException, ParseException {
		// Get config
		Config config = Config.createFromResource("process.conf", "regex.conf");

		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();

		// Generate sample system metric
		generateSampleSystemMetric();

		// Process
		int size = ProcessQueue.getInstance().size();
		for (int i=0; i<size; i++)
			pprocess.process();
		logger.debug("Processed queue size=" + size);

/*		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0); */
	}

	public static void generateSampleSystemMetric() {
		offset = 0;
		StreamFileHelper.getFileFromResource("sample-system-metric.log")
		.forEach(line -> {
			generateLogEventList(line);
		});
	}

	public static void generateSampleAccessMetric() {
		offset = 0;
		StreamFileHelper.getFileFromResource("sample-access-metric.log")
		.forEach(line -> {
			generateLogEventList(line);
		});
	}

	// TODO JSONArray parsing 후 다시 String 변환하여 LogEvent 생성하므로 비효율적
	private static void generateLogEventList(String line) {
		List<LogEvent> elist = new ArrayList<>();

		// Make LogEvent list
		JSONArray ja = new JSONArray(line);
		for (int i=0; i<ja.length(); i ++) {
			JSONObject jo = ja.getJSONObject(i);
			elist.add(new LogEvent(jo.toString()));
		}

		// Put to queue
		try {
			ProcessQueue.getInstance().put(elist);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// TODO Stream 사용하여 변경 - 참조 GeneralConsumerTest
	static long offset;
	public static void generateSampleJson() {
	    offset = 0;
		FileHelper.processFileFromResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				try {
					List<LogEvent> elist1 = createLogEventList(produceSampleJson("web01", line));
					List<LogEvent> elist2 = createLogEventList(produceSampleJson("web02", line));
					ProcessQueue.getInstance().put(elist1);
					ProcessQueue.getInstance().put(elist2);
//					ProcessQueue.getInstance().put(createLogEvent(produceJson("web01", line)));
//					ProcessQueue.getInstance().put(createLogEvent(produceJson("web02", line)));
				} catch (InterruptedException e) {
					logger.error(e.toString());
				}
			}
		});
	}

	protected static List<LogEvent> createLogEventList(String value) {
		ArrayList<LogEvent> elist = new ArrayList<>();
		elist.add(LogEvent.parse(value));
		return elist;
	}

	private static String produceSampleJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("host", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	}

	/*
	 * Filebeat sample (Filebeat -> Logstash):
	 * {"@timestamp":"2018-09-11T08:53:23.104Z","@version":"1","offset":1488771,"input":{"type":"log"},"tags":["beats_input_codec_plain_applied"],"beat":{"version":"6.4.0","name":"SMONi-web-dev01","hostname":"SMONi-web-dev01"},"prospector":{"type":"log"},"source":"/app/nginx/logs/access.log","message":"172.21.43.140 - - [11/Sep/2018:17:53:15 +0900] \"GET /v1/instances/list HTTP/1.1\" 200 575 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_51)\" \"0.007\"","host":{"name":"SMONi-web-dev01"}}
	 */
	@Ignore
	@Test
	public void testFilebeatJson() throws IOException, ParseException {
		// Get config
		Config config = Config.createFromResource("process.conf", "regex.conf");

		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();

		// Generate sample log
		generateFilebeatJson();

		// Process
		for (int i=0; i<200; i++)
			pprocess.process();

		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0);
	}

	private void generateFilebeatJson() {
	    offset = 0;
		FileHelper.processFileFromResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				try {
					List<LogEvent> elist1 = createLogEventList(produceFilebeatJson("web01", line));
					List<LogEvent> elist2 = createLogEventList(produceFilebeatJson("web02", line));
					ProcessQueue.getInstance().put(elist1);
					ProcessQueue.getInstance().put(elist2);
				} catch (InterruptedException e) {
					logger.error(e.toString());
				}
			}
		});
	}

	protected String produceFilebeatJson(String host, String line) {
		JSONObject j = new JSONObject();
		JSONArray jtags = new JSONArray();
		jtags.put("beats_input_codec_plain_applied");
		JSONObject jbeat = new JSONObject();
		jbeat.put("version", "6.4.0");
		jbeat.put("name", host);
		jbeat.put("hostname", host);
		JSONObject jhost = new JSONObject();
		jhost.put("name", host);

		j.put("tags", jtags);
		j.put("beat", jbeat);
		j.put("message",  line);
		j.put("host", jhost);
		return j.toString();
	}

}
