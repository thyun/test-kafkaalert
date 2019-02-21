package com.skp.kafkaalert.input.kafka;

import org.json.JSONObject;

import com.skp.kafkaalert.config.ConfigItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConfigInputKafka implements ConfigItem {
	String type;
	String broker;
	String topic;
	String group;

	// TODO Delete from here
	public ConfigInputKafka(JSONObject j) {
		init(j);
	}

	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		broker = (String) j.get("broker");
		topic = (String) j.get("topic");
		group = (String) j.get("group");
	}

	@Override
	public void prepare() {

	}

}
