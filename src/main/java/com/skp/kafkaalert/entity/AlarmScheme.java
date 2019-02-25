package com.skp.kafkaalert.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.skp.kafkaalert.config.CommonFieldValue;

import lombok.Data;

@Data
public class AlarmScheme {
	List<CommonFieldValue> metrics;
	List<List<CommonFieldValue>> targets;
	CommonFieldValue instance;
	CommonFieldValue value;

	public List<AlarmFieldKey> buildAlarmFieldKey() {
		HashMap<String, AlarmFieldKey> map = new HashMap<>();
//		ArrayList<AlarmSchemeMeta> list = new ArrayList<>();

		// Get metric keys
		List<String> metricKeys = metrics.stream()
			.map(fv -> fv.getField())
			.collect(Collectors.toList());

		// Get target keys
		for (List<CommonFieldValue> target: targets) {
			List<String> targetKeys = target.stream()
				.map(fv -> fv.getField())
				.collect(Collectors.toList());

			ArrayList<String> fields = new ArrayList<String>(metricKeys);
			fields.addAll(targetKeys);
			AlarmFieldKey schemeMeta = new AlarmFieldKey(fields);

			map.put(schemeMeta.getKey(), schemeMeta);
//			list.add(schemeMeta);
		}

		return map.values().stream().collect(Collectors.toList());
	}

	public List<AlarmValueKey> buildAlarmValueKey() {
		ArrayList<AlarmValueKey> list = new ArrayList<>();

		// Get metric keys
		List<String> metricKeys = metrics.stream()
			.map(fv -> fv.getValue())
			.collect(Collectors.toList());

		// Get target keys
		for (List<CommonFieldValue> target: targets) {
			List<String> targetKeys = target.stream()
				.map(fv -> fv.getValue())
				.collect(Collectors.toList());

			ArrayList<String> values = new ArrayList<String>(metricKeys);
			values.addAll(targetKeys);
			AlarmValueKey lookupKey = new AlarmValueKey(values);

			list.add(lookupKey);
		}

		return list;
	}
}
