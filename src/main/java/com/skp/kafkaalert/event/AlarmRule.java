package com.skp.kafkaalert.event;

import lombok.Data;

@Data
public class AlarmRule {
	String name;
	long threshold;
	String inequality;
	long consecutive;
	long remind;
	int is_used;
}
