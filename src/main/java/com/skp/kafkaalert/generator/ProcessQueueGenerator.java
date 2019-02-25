package com.skp.kafkaalert.generator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.process.ProcessQueue;
import com.skp.util.StreamFileHelper;

public class ProcessQueueGenerator {
	private static final Logger logger = LoggerFactory.getLogger(ProcessQueueGenerator.class);

	static long offset;
	public static void generateSampleMetric(String path) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
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
			e.printStackTrace();
		}

	}

}
