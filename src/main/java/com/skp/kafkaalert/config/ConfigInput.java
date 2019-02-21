package com.skp.kafkaalert.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConfigInput {
	String type;
	String broker;
	String topic;
	String group;
}
