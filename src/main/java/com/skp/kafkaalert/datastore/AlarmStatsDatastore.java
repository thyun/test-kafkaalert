package com.skp.kafkaalert.datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.entity.AlarmStatus;

import lombok.Data;

@Data
public class AlarmStatsDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static AlarmStatsDatastore instance = null;

	int onCount;
	int remindCount;
	int offCount;

	public static AlarmStatsDatastore getInstance() {
		if (instance == null) {
			instance = new AlarmStatsDatastore();
		}
		return instance;
	}

	public void reset() {
		onCount = remindCount = offCount = 0;
	}

	public void count(AlarmStatus.Action action) {
		switch (action) {
			case ON:
				onCount++; break;
			case REMIND:
				remindCount++; break;
			case OFF:
				offCount++; break;
			default:
		}
	}

}
