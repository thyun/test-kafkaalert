package com.skp.kafkaalert.output;

import org.json.JSONObject;

import com.skp.kafkaalert.config.ConfigItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConfigOutputFile implements ConfigItem {
	String type;
	String path;
	int max;

	public ConfigOutputFile(JSONObject j) {
		init(j);
	}

	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		path = (String) j.get("path");
//		max = (String) j.get("max");

	}

	@Override
	public void prepare() {

	}

}
