package com.skp.kafkaalert.process;

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
import com.skp.kafkaalert.entity.AlarmRule;
import com.skp.kafkaalert.entity.AlarmScheme;
import com.skp.kafkaalert.entity.AlarmStatus;
import com.skp.kafkaalert.entity.AlarmStatus.Action;
import com.skp.kafkaalert.entity.AlarmValueKey;
import com.skp.kafkaalert.entity.LogEvent;

public class ProcessAlarm {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();

	public void process(ConfigProcess cprocess, LogEvent e) {
		List<AlarmHolder> holders = AlarmMetaDatastore.getInstance().lookupAlarms(cprocess, e);
		for (AlarmHolder alarm: holders)
			processAlarm(cprocess, e, alarm);
	}

	private void processAlarm(ConfigProcess cprocess, LogEvent e, AlarmHolder holder) {
		logger.debug("processAlarm() start");
		logger.debug("e=" + e + ", alarm=" + holder.getAlarm());

		for (AlarmRule rule: holder.getAlarm().getRules()) {
			holder.setRule(rule);
			processAlarmRule(cprocess, e, holder);
		}

		logger.debug("processAlarm() end");
	}

	private void processAlarmRule(ConfigProcess cprocess, LogEvent e, AlarmHolder holder) {
		logger.debug("processAlarmRule() start");
		Alarm alarm = holder.getAlarm();
		AlarmRule rule = holder.getRule();

/*		CommonFieldValue fv = alarm.getScheme().getInstance();
		String instance = e.optString(fv.getField(), "");
		if (!instance.equals(fv.getValue()))
			return; */

		if (isOn(cprocess, e, holder))
			processOn(cprocess, e, holder);
		else
			processOff(cprocess, e, holder);
	}

	// Inequality: ">", ">=", "<", "<="
	// TODO Support double
	private boolean isOn(ConfigProcess cprocess, LogEvent e, AlarmHolder holder) {
		Alarm alarm = holder.getAlarm();
		AlarmRule rule = holder.getRule();

		CommonFieldValue fv = alarm.getScheme().getValue();
		long value = e.getLong(fv.getField());
		holder.setValue(value);
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

	private void processOn(ConfigProcess cprocess, LogEvent e, AlarmHolder holder) {
		Alarm alarm = holder.getAlarm();
		AlarmRule rule = holder.getRule();

		String statusKey = alarm.getAlarmStatusKey(rule);
		AlarmStatus status = AlarmStatusDatastore.getInstance().getOrCreate(statusKey);
		status.addContinuous();
		holder.setStatus(status);

		// Generate alarm notification (ON, REMIND)
		AlarmStatus.Action action = status.getAction(rule);
		if (action != AlarmStatus.Action.NONE) {
			generateAlarmNotification(holder, e, action);
			AlarmStatsDatastore.getInstance().count(action);
		}
	}

	private void processOff(ConfigProcess cprocess, LogEvent e, AlarmHolder holder) {
		Alarm alarm = holder.getAlarm();
		AlarmRule rule = holder.getRule();
		String statusKey = alarm.getAlarmStatusKey(rule);

		AlarmStatus status = AlarmStatusDatastore.getInstance().getAndDelete(statusKey);
		if (status == null)
			return;
		status.addContinuous();
		holder.setStatus(status);

		// Generate alarm notification (OFF)
		generateAlarmNotification(holder, e, AlarmStatus.Action.OFF);
		AlarmStatsDatastore.getInstance().count(AlarmStatus.Action.OFF);
	}

	// TODO if instance is not defined - 항상 blank로 값을 채워야?
	private void generateAlarmNotification(AlarmHolder holder, LogEvent e, Action action) {
		Alarm alarm = holder.getAlarm();
		AlarmScheme scheme = alarm.getScheme();
		AlarmStatus status = holder.getStatus();
		LogEvent noti = new LogEvent();
		noti.put("alarm", alarm);
		noti.put("action", action.name());
		noti.put("continuous", status.getContinuous());
		noti.put("message", makeAlarmMessage(holder, action));
		noti.put("metricName", scheme.getName());						// CPU, Disk, Network inbound, ...
		noti.put("metricInstance", scheme.getInstance().getValue());	// average, /home, eth0, ...
		noti.put("metricValue", scheme.getValue().getField());			// user, value, tx, ...
		noti.put("target", holder.getValueKey().getTargetMessage());	// myweb01
		noti.put("targetValue", holder.getValue());
		// TODO targetTimestamp

		logger.debug("ALARM " + action + ": message=" + noti.get("message"));
		logger.debug("notification=" + noti.toString());
	}

	private String makeAlarmMessage(AlarmHolder holder, Action action) {
		AlarmValueKey valueKey = holder.getValueKey();
		Alarm alarm = holder.getAlarm();
		AlarmScheme scheme = alarm.getScheme();
		AlarmRule rule = holder.getRule();
		AlarmStatus status = holder.getStatus();

		if (scheme.isInstance())
			return String.format("[%s #%d %dmin] %s [%s %s] %s %s %s", action.name(), alarm.getId(), status.getContinuous(),
				valueKey.getTargetMessage(), scheme.getName(), scheme.getInstance().getValue(),
				holder.getValue(), rule.getInequality(), rule.getThreshold());
		else
			return String.format("[%s #%d %dmin] %s [%s] %s %s %s", action.name(), alarm.getId(), status.getContinuous(),
				valueKey.getTargetMessage(), scheme.getName(),
				holder.getValue(), rule.getInequality(), rule.getThreshold());
	}



}
