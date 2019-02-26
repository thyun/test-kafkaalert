package com.skp.kafkaalert.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.CommonFieldValue;
import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.entity.Alarm;
import com.skp.kafkaalert.entity.AlarmFieldKey;
import com.skp.kafkaalert.entity.AlarmScheme;
import com.skp.kafkaalert.entity.AlarmValueKey;
import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.process.AlarmHolder;
import com.skp.util.MultiMap;

import lombok.Data;

@Data
public class AlarmMetaDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static AlarmMetaDatastore instance = null;

	ConcurrentHashMap<String, AlarmFieldKey> fieldKeyMap = new ConcurrentHashMap<>();
	List<AlarmFieldKey> fieldKeyList;
//	ConcurrentHashMap<String, Alarm> alarmMap = new ConcurrentHashMap<>();
	MultiMap<String, Alarm> alarmMap = new MultiMap<>();
	public static AlarmMetaDatastore getInstance() {
		if (instance == null) {
			instance = new AlarmMetaDatastore();
		}
		return instance;
	}

	public void build(ConfigProcess cprocess) {
		for (Object o: cprocess.getAlarms()) {
			Alarm alarm = Alarm.create(o);
			buildAlarmMeta(alarm);
		}
		fieldKeyList = fieldKeyMap.values().stream().collect(Collectors.toList());

	}

	private void buildAlarmMeta(Alarm alarm) {
		AlarmScheme scheme = alarm.getScheme();

		List<AlarmFieldKey> fieldKeys = buildAlarmFieldKey(scheme, alarm.getTargets());
//		List<AlarmFieldKey> fieldKeys = scheme.buildAlarmFieldKey();
		for (AlarmFieldKey fieldKey: fieldKeys)
			fieldKeyMap.put(fieldKey.getKey(), fieldKey);

		List<AlarmValueKey> valueKeys = buildAlarmValueKey(scheme, alarm.getTargets());
//		List<AlarmValueKey> valueKeys = scheme.buildAlarmValueKey();
		for (AlarmValueKey valueKey: valueKeys)
			alarmMap.put(valueKey.getKey(), alarm);
	}

	private List<AlarmFieldKey> buildAlarmFieldKey(AlarmScheme scheme, List<List<CommonFieldValue>> targets) {
		HashMap<String, AlarmFieldKey> map = new HashMap<>();
//			ArrayList<AlarmSchemeMeta> list = new ArrayList<>();

		// Get metric keys
		List<String> metricFields = scheme.getMetrics().stream()
			.map(fv -> fv.getField())
			.collect(Collectors.toList());

		// Get target keys
		for (List<CommonFieldValue> target: targets) {
			List<String> targetFields = target.stream()
				.map(fv -> fv.getField())
				.collect(Collectors.toList());

			AlarmFieldKey schemeMeta = new AlarmFieldKey(metricFields, targetFields);
//				ArrayList<String> fields = new ArrayList<String>(metricKeys);
//				fields.addAll(targetKeys);
//				AlarmFieldKey schemeMeta = new AlarmFieldKey(fields);

			map.put(schemeMeta.getKey(), schemeMeta);
//				list.add(schemeMeta);
		}

		return map.values().stream().collect(Collectors.toList());
	}

	private List<AlarmValueKey> buildAlarmValueKey(AlarmScheme scheme, List<List<CommonFieldValue>> targets) {
		ArrayList<AlarmValueKey> list = new ArrayList<>();

		// Get metric values
		List<String> metricValues = scheme.getMetrics().stream()
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
	}

	// TODO call scheme.isInstance() and call filter or not
	public List<AlarmHolder> lookupAlarms(ConfigProcess cprocess, LogEvent e) {
		ArrayList<AlarmHolder> list = new ArrayList<>();

		for (AlarmFieldKey fieldKey: AlarmMetaDatastore.getInstance().getFieldKeyList()) {
			if (fieldKey.hasFields(e)) {
				AlarmValueKey valueKey = fieldKey.getValueKey(e);
				Collection<Alarm> alarms = AlarmMetaDatastore.getInstance().getAlarmMap().get(valueKey.getKey());
				if (alarms == null)
					continue;

				List<AlarmHolder> holders = alarms.stream()
					.filter(alarm -> matchFieldValue(e, alarm.getScheme().getInstance()))
					.map(alarm -> {
						AlarmHolder holder = new AlarmHolder();
						holder.setAlarm(alarm);
						holder.setFieldKey(fieldKey);
						holder.setValueKey(valueKey);
						return holder;
					})
					.collect(Collectors.toList());
				list.addAll(holders);
			}
		}
		return list;
	}

	private boolean matchFieldValue(LogEvent e, CommonFieldValue fv) {
		if (fv == null || fv.getField() == null || fv.getField().length() == 0)
			return true;

		if (!e.has(fv.getField()))
			return false;
		String value = e.optString(fv.getField(), "");
		if (!value.equals(fv.getValue()))
			return false;
		return true;
	}

}
