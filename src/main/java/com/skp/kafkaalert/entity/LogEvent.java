package com.skp.kafkaalert.entity;

import java.util.Date;

import org.json.JSONObject;

import com.skp.kafkaalert.config.ConfigValue;
import com.skp.util.CommonHelper;

import lombok.Getter;

@Getter
public class LogEvent extends JSONObject {
	public static String FIELD_TIMESTAMP = "@timestamp";
	Date timestamp;

	public static LogEvent parse(String value) {
		LogEvent e = new LogEvent(value);
		return e;
	}

	public LogEvent() {
		super();

		setTimestamp(new Date());
	}

	public LogEvent(String value) {
		super(value);
		if (super.isNull(FIELD_TIMESTAMP))
			setTimestamp(new Date());
	}

	public LogEvent(String value, Date timestamp) {
		super(value);
		setTimestamp(timestamp);
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		this.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}

	public void setTimestamp(Date timestamp, String tfield) {
		this.timestamp = timestamp;
		this.put(tfield, CommonHelper.timestamp2Str(timestamp));
	}

	public ConfigValue getConfigValue(String raw) {
		return ConfigValue.create(this, raw);
	}

	public String toString() {
		return super.toString();
/*		StringBuffer sb = new StringBuffer();
		sb.append("LogEvent " + super.toString());
		return sb.toString(); */
	}

}
