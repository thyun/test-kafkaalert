package com.skp.kafkaalert.event;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Alarm {
	int id;
	String display_name;
	int is_used;
	AlarmScheme scheme;
	ArrayList<AlarmRule> rules;
	Object notifications;
	Object notification_groups;

	public String getAlarmStatusKey(AlarmRule rule) {
		return "" + id + "-" + rule.getName();
	}
}
