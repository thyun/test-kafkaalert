package com.skp.kafkaalert.entity;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class AlarmFieldKey {
	String key;
	List<String> fields;

	public AlarmFieldKey(List<String> fields) {
		this.key = String.join("-", fields);
		this.fields = fields;
	}

	public boolean hasFields(LogEvent e) {
		for (String field: fields)
			if (!e.has(field))
				return false;
		return true;
	}

	public AlarmValueKey getValueKey(LogEvent e) {
		List<String> values = fields.stream().map(field -> e.getString(field))
			.collect(Collectors.toList());
		return new AlarmValueKey(values);
	}
}
