package com.skp.kafkaalert.entity;

import java.util.List;
import com.skp.kafkaalert.config.CommonFieldValue;

import lombok.Data;

@Data
public class AlarmScheme {
	String name;
	List<CommonFieldValue> metrics;
//	List<List<CommonFieldValue>> targets;
	CommonFieldValue instance;
	CommonFieldValue value;

	public boolean isInstance() {
		if (instance == null || instance.getField() == null || instance.getField().length() == 0)
			return false;
		return true;
	}

/*	public List<AlarmFieldKey> buildAlarmFieldKey() {
		HashMap<String, AlarmFieldKey> map = new HashMap<>();
//		ArrayList<AlarmSchemeMeta> list = new ArrayList<>();

		// Get metric keys
		List<String> metricFields = metrics.stream()
			.map(fv -> fv.getField())
			.collect(Collectors.toList());

		// Get target keys
		for (List<CommonFieldValue> target: targets) {
			List<String> targetFields = target.stream()
				.map(fv -> fv.getField())
				.collect(Collectors.toList());

			AlarmFieldKey schemeMeta = new AlarmFieldKey(metricFields, targetFields);
//			ArrayList<String> fields = new ArrayList<String>(metricKeys);
//			fields.addAll(targetKeys);
//			AlarmFieldKey schemeMeta = new AlarmFieldKey(fields);

			map.put(schemeMeta.getKey(), schemeMeta);
//			list.add(schemeMeta);
		}

		return map.values().stream().collect(Collectors.toList());
	}

	public List<AlarmValueKey> buildAlarmValueKey() {
		ArrayList<AlarmValueKey> list = new ArrayList<>();

		// Get metric values
		List<String> metricValues = metrics.stream()
			.map(fv -> fv.getValue())
			.collect(Collectors.toList());

		// Get target values
		for (List<CommonFieldValue> target: targets) {
			List<String> targetValues = target.stream()
				.map(fv -> fv.getValue())
				.collect(Collectors.toList());

			ArrayList<String> values = new ArrayList<String>(metricValues);
			values.addAll(targetValues);
			AlarmValueKey valueKey = new AlarmValueKey(metricValues, targetValues);

			list.add(valueKey);
		}

		return list;
	} */
}
