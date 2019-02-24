package com.skp.kafkaalert.process;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.config.CommonFieldValue;
import com.skp.kafkaalert.config.ConfigProcess;
import com.skp.kafkaalert.datastore.AlarmMetaDatastore;
import com.skp.kafkaalert.datastore.AlarmStatusDatastore;
import com.skp.kafkaalert.event.Alarm;
import com.skp.kafkaalert.event.AlarmLookup;
import com.skp.kafkaalert.event.AlarmRule;
import com.skp.kafkaalert.event.AlarmStatus;
import com.skp.kafkaalert.event.LogEvent;

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
	// TODO double 지원
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
		if (action != AlarmStatus.Action.NONE)
			logger.debug("ALARM " + action + ": key=" + key);
	}

	private void processOff(ConfigProcess cprocess, LogEvent e, Alarm alarm, AlarmRule rule) {
		String key = alarm.getAlarmStatusKey(rule);

		AlarmStatus status = AlarmStatusDatastore.getInstance().getAndDelete(key);
		if (status == null)
			return;

		// TODO Generate OFF event
		logger.debug("ALARM " + AlarmStatus.Action.OFF + ": key=" + key);
	}

	// TODO 구현
	private List<Alarm> lookupAlarms(ConfigProcess cprocess, LogEvent e) {
		ArrayList<Alarm> alarms = new ArrayList<>();

		// Field keys
		for (AlarmLookup lookup: AlarmMetaDatastore.getInstance().getLookupList()) {
			if (lookup.hasFields(e)) {
				String valueKey = lookup.getValueKey(e);
				Alarm alarm = AlarmMetaDatastore.getInstance().getAlarmMap().get(valueKey);
				if (alarm != null)
					alarms.add(alarm);
			}
		}

/*		if (!e.has("collectd_type"))
			return alarms;
		if (!e.getString("collectd_type").equals("if_octets"))
			return alarms;

		Alarm alarm = Alarm.create(cprocess.getAlarms().get(1));
//		Object o = cprocess.getAlarms().get(1);
//		Alarm alarm = objectMapper.convertValue(o, Alarm.class);

		CommonFieldValue fv = alarm.getScheme().getInstance();
		String instance = fv.getValue();
		if (instance.equals(e.getString(fv.getField()))
				&& e.getString("host").startsWith("SMONi-web"))
			alarms.add(alarm); */
		return alarms;
	}

	// TODO multiple scheme 지원하도록 개선
//	public List<ProcessScheme> lookupProcessSchemes(ConfigProcess cprocess, LogEvent e) {
//		return null;
/*		ArrayList<ProcessScheme> pschemes = new ArrayList<>();
		ConfigScheme cscheme = cprocess.getScheme();

		// Check if all scheme metrics fields exist
		List<CommonFieldValue> fvList = cscheme.getMetrics();
		for (CommonFieldValue fv: fvList) {
			if (e.isNull(fv.getField()))
				return pschemes;
		}
		pschemes.add(new ProcessScheme(cscheme));
		return pschemes; */
//	}


}
