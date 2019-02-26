package com.skp.kafkaalert.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class AlarmFieldKey {
	String key;
	List<String> fields;
	List<String> metricFields;
	List<String> targetFields;

	public AlarmFieldKey(List<String> metricFields, List<String> targetFields) {
		this.metricFields = metricFields;
		this.targetFields = targetFields;

		fields = new ArrayList<String>(metricFields);
		fields.addAll(targetFields);
		this.key = String.join("-", fields);
	}

	public boolean hasFields(LogEvent e) {
		for (String field: fields)
			if (!e.has(field))
				return false;
		return true;
	}

	public AlarmValueKey getValueKey(LogEvent e) {
		List<String> metricValues = metricFields.stream().map(field -> e.getString(field))
			.collect(Collectors.toList());
		List<String> targetValues = targetFields.stream().map(field -> e.getString(field))
				.collect(Collectors.toList());
		return new AlarmValueKey(metricValues, targetValues);

//		List<String> values = fields.stream().map(field -> e.getString(field))
//			.collect(Collectors.toList());
//		return new AlarmValueKey(values);
	}
}
