package com.skp.kafkaalert.event;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class Alarm {
	static ObjectMapper objectMapper = new ObjectMapper();

	int id;
	String display_name;
	int is_used;
	AlarmScheme scheme;
	ArrayList<AlarmRule> rules;
	Object notifications;
	Object notification_groups;

	public static Alarm create(Object o) {
		return objectMapper.convertValue(o, Alarm.class);
	}

	public String getAlarmStatusKey(AlarmRule rule) {
		return "" + id + "-" + rule.getName();
	}

}
