package com.skp.kafkaalert.event;

import com.skp.kafkaalert.config.CommonFieldValue;

import lombok.Data;

@Data
public class AlarmScheme {
	Object metrics;
	Object targets;
	CommonFieldValue instance;
	CommonFieldValue value;
}
