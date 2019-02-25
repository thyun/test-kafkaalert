package com.skp.kafkaalert.generator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.output.OutputQueue;
import com.skp.kafkaalert.process.ProcessQueue;
import com.skp.util.StreamFileHelper;

public class OutputQueueGenerator {
	private static final Logger logger = LoggerFactory.getLogger(OutputQueueGenerator.class);

	static long offset;
	public static void generateSampleCnxlogJson(String path, OutputQueue outputQueue) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
		.forEach(line -> {
			generateLogEventList(line, outputQueue);
		});
	}

	private static void generateLogEventList(String line, OutputQueue outputQueue) {
		List<LogEvent> elist = new ArrayList<>();

		// Make LogEvent list
		elist.add(new LogEvent(produceJson("web01", line)));

		// Put to queue
		try {
			outputQueue.put(elist);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

/*		// Make LogEvent list
		JSONArray ja = new JSONArray(line);
		for (int i=0; i<ja.length(); i ++) {
			JSONObject jo = ja.getJSONObject(i);
			elist.add(new LogEvent(jo.toString()));
		}

		// Put to queue
		try {
			ProcessQueue.getInstance().put(elist);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
	}

	private static String produceJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	}

}
