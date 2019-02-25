package com.skp.kafkaalert.output.kafka;

import lombok.Data;

@Data
public class ConfigOutputKafka {
	String type;
	String broker;
	String topic;

/*	public ConfigOutputKafka(JSONObject j) {
		init(j);
	}

	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		broker = (String) j.get("broker");
		topic = (String) j.get("topic");
	}

	@Override
	public void prepare() {

	} */

}
