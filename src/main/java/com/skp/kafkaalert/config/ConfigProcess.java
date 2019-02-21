package com.skp.kafkaalert.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class ConfigProcess {
	ConfigScheme scheme;
	ArrayList<Object> alarms;
	ArrayList<Object> notification_groups;

/*	JSONArray ja;
	List<ConfigItem> configProcessList = new ArrayList<ConfigItem>();

	public ConfigProcess(JSONArray ja) {
		super();
		this.ja = ja;
		init();
	}

	private void init() {
		for (int i=0; i<ja.length(); i++) {
			JSONObject j = (JSONObject) ja.get(i);
			ConfigItem item = createConfigProcessPlugin(j);
			if (item != null)
				configProcessList.add(item);
		}

	}

	private ConfigItem createConfigProcessPlugin(JSONObject j) {
		String type = (String) j.get("type");
		if ("match".equals(j.get("type"))) {
			return new ConfigProcessMatch(j);
		} else if ("date".equals(type)) {
			return new ConfigProcessDate(j);
		} else if ("metrics".equals(type)) {
			return new ConfigProcessMetrics(j);
		}
		return null;
	}

	public void prepare() {
		for (ConfigItem item : configProcessList) {
			item.prepare();
		}
	} */
}