package com.skp.kafkaalert.config;

import lombok.Data;

@Data
public class ConfigInput {
	String type;
	String broker;
	String topic;
	String group;
}
