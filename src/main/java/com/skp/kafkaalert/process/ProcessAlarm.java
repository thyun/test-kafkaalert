package com.skp.kafkaalert.process;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.config.CommonFieldValue;
import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.datastore.AlarmMetaDatastore;
import com.skp.kafkaalert.datastore.AlarmStatsDatastore;
import com.skp.kafkaalert.datastore.AlarmStatusDatastore;
import com.skp.kafkaalert.entity.Alarm;
import com.skp.kafkaalert.entity.AlarmFieldKey;
import com.skp.kafkaalert.entity.AlarmRule;
import com.skp.kafkaalert.entity.AlarmStatus;
import com.skp.kafkaalert.entity.AlarmValueKey;
import com.skp.kafkaalert.entity.LogEvent;

public class ProcessAlarm {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();

	public void process(ConfigProcess cprocess, LogEvent e) {
		List<Alarm> alarms = lookupAlarms(cprocess, e);
		for (Alarm alarm: alarms)
			processAlarm(cprocess, e, alarm);
	}

	private void processAlarm(ConfigProcess cprocess, LogEvent e, Alarm alarm) {
		logger.debug("processAlarm() start");
		logger.debug("e=" + e + ", alarm=" + alarm);

		for (AlarmRule rule: alarm.getRules())
			processAlarmRule(cprocess, e, alarm, rule);

		logger.debug("processAlarm() end");
	}

	private void processAlarmRule(ConfigProcess cprocess, LogEvent e, Alarm alarm, AlarmRule rule) {
		logger.debug("processAlarmRule() start");
		CommonFieldValue fv = alarm.getScheme().getInstance();
		String instance = e.optString(fv.getField(), "");
		if (!instance.equals(fv.getValue()))
			return;

		if (isOn(cprocess, e, alarm, rule))
			processOn(cprocess, e, alarm, rule);
		else
			processOff(cprocess, e, alarm, rule);
	}

	// Inequality: ">", ">=", "<", "<="
	// TODO Support double
	private boolean isOn(ConfigProcess cprocess, LogEvent e, Alarm alarm, AlarmRule rule) {
		CommonFieldValue fv = alarm.getScheme().getValue();
		long value = e.getLong(fv.getField());
		logger.debug("isOn(): value=" + value + ", threshold=" + rule.getThreshold() + ", inequality=" + rule.getInequality());

		if (">".equals(rule.getInequality()) && value > rule.getThreshold())
			return true;
		else if (">=".equals(rule.getInequality()) && value >= rule.getThreshold())
			return true;
		else if ("<".equals(rule.getInequality()) && value < rule.getThreshold())
			return true;
		else if ("<=".equals(rule.getInequality()) && value <= rule.getThreshold())
			return true;
		return false;
	}

	private void processOn(ConfigProcess cprocess, LogEvent e, Alarm alarm, AlarmRule rule) {
		String key = alarm.getAlarmStatusKey(rule);
		AlarmStatus status = AlarmStatusDatastore.getInstance().getOrCreate(key);

		// TODO Generate ON event
		status.addRepeat();
		AlarmStatus.Action action = status.getAction(rule);
		if (action != AlarmStatus.Action.NONE) {
			logger.debug("ALARM " + action + ": key=" + key);
			AlarmStatsDatastore.getInstance().count(action);
		}
	}

	private void processOff(ConfigProcess cprocess, LogEvent e, Alarm alarm, AlarmRule rule) {
		String key = alarm.getAlarmStatusKey(rule);

		AlarmStatus status = AlarmStatusDatastore.getInstance().getAndDelete(key);
		if (status == null)
			return;

		// TODO Generate OFF event
		logger.debug("ALARM " + AlarmStatus.Action.OFF + ": key=" + key);
		AlarmStatsDatastore.getInstance().count(AlarmStatus.Action.OFF);
	}

	private List<Alarm> lookupAlarms(ConfigProcess cprocess, LogEvent e) {
		ArrayList<Alarm> alarms = new ArrayList<>();

		for (AlarmFieldKey fieldKey: AlarmMetaDatastore.getInstance().getFieldKeyList()) {
			if (fieldKey.hasFields(e)) {
				AlarmValueKey valueKey = fieldKey.getValueKey(e);
				Alarm alarm = AlarmMetaDatastore.getInstance().getAlarmMap().get(valueKey.getKey());
				if (alarm != null)
					alarms.add(alarm);
			}
		}
		return alarms;
	}

}
