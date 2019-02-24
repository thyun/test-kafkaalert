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
}
