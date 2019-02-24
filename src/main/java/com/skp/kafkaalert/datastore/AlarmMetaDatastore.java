package com.skp.kafkaalert.datastore;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.event.Alarm;
import com.skp.kafkaalert.event.AlarmScheme;
import com.skp.kafkaalert.event.AlarmLookup;
import lombok.Data;

@Data
public class AlarmMetaDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static AlarmMetaDatastore instance = null;

	ConcurrentHashMap<String, AlarmLookup> lookupMap = new ConcurrentHashMap<>();
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

	}

	private void buildAlarmMeta(Alarm alarm) {
		AlarmScheme scheme = alarm.getScheme();

		List<AlarmLookup> lookupKeys = scheme.buildAlarmLookupKey();
		for (AlarmLookup lookupKey: lookupKeys)
			lookupMap.put(lookupKey.getKey(), lookupKey);

		List<AlarmLookup> lookupValues = scheme.buildAlarmLookupValue();
		for (AlarmLookup lookupValue: lookupValues)
			alarmMap.put(lookupValue.getKey(), alarm);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
/*		for (AlarmStatus me : hashMap.values()) {
			sb.append("\n");
//			sb.append(me.toString());
		} */
		return sb.toString();
	}


}
