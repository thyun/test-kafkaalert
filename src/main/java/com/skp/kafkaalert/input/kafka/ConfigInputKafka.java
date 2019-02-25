package com.skp.kafkaalert.input.kafka;

import lombok.Data;

@Data
public class ConfigInputKafka {
	String type;
	String broker;
	String topic;
	String group;

}
