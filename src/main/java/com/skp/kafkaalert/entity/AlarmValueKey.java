package com.skp.kafkaalert.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AlarmValueKey {
	String key;
	List<String> values;
	List<String> metricValues;
	List<String> targetValues;

	public AlarmValueKey(List<String> metricValues, List<String> targetValues) {
		this.metricValues = metricValues;
		this.targetValues = targetValues;

		values = new ArrayList<String>(metricValues);
		values.addAll(targetValues);
		this.key = String.join("-", values);
	}

	public String getTargetMessage() {
		return String.join(" ", targetValues);
	}

}
