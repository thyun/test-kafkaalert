package com.skp.kafkaalert.datastore;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.entity.Alarm;
import com.skp.kafkaalert.entity.AlarmFieldKey;
import com.skp.kafkaalert.entity.AlarmScheme;
import com.skp.kafkaalert.entity.AlarmValueKey;

import lombok.Data;

@Data
public class AlarmMetaDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static AlarmMetaDatastore instance = null;

	ConcurrentHashMap<String, AlarmFieldKey> fieldKeyMap = new ConcurrentHashMap<>();
	List<AlarmFieldKey> fieldKeyList;
	ConcurrentHashMap<String, Alarm> alarmMap = new ConcurrentHashMap<>();
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

		List<AlarmFieldKey> fieldKeys = scheme.buildAlarmFieldKey();
		for (AlarmFieldKey fieldKey: fieldKeys)
			fieldKeyMap.put(fieldKey.getKey(), fieldKey);

		List<AlarmValueKey> valueKeys = scheme.buildAlarmValueKey();
		for (AlarmValueKey valueKey: valueKeys)
			alarmMap.put(valueKey.getKey(), alarm);
	}

}
