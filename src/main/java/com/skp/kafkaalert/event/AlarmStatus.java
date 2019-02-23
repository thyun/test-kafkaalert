package com.skp.kafkaalert.event;

import java.util.Date;

import lombok.Data;

@Data
public class AlarmStatus {
	String key;
	int repeat = 0;
	Date startTime = new Date();

	public class AlarmFinal {
		int action;
	}

	public void addRepeat() {
		repeat++;
	}

	public enum Action {
		NONE, ON, REMIND, OFF
	}
	public Action getAction(AlarmRule rule) {
		if (repeat < rule.getConsecutive())
			return Action.NONE;
		else if (repeat == rule.getConsecutive())
			return Action.ON;
		else if ((repeat-rule.getConsecutive()) % rule.getRemind() == 0)
			return Action.REMIND;
		return Action.NONE;
	}
}
