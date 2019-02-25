package com.skp.kafkaalert.entity;

import java.util.List;

import lombok.Data;

@Data
public class AlarmValueKey {
	String key;
	List<String> values;

	public AlarmValueKey(List<String> fields) {
		this.key = String.join("-", fields);
		this.values = fields;
	}

}
