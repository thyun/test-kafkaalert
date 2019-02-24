package com.skp.kafkaalert.event;

import java.util.List;

import lombok.Data;

@Data
public class AlarmLookup {
	String key;
	List<String> words;

	public AlarmLookup(List<String> words) {
		this.key = String.join("-", words);
		this.words = words;
	}

	public boolean hasFields(LogEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getValueKey(LogEvent e) {
		// TODO Auto-generated method stub
		return null;
	}
}
