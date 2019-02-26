package com.skp.kafkaalert.entity;

import java.util.Date;

import lombok.Data;

@Data
public class AlarmStatus {
	String key;
	int continuous = 0;
	Date startTime = new Date();

	public enum Action {
		NONE, ON, REMIND, OFF
	}

	public class AlarmFinal {
		int action;
	}

	public void addContinuous() {
		continuous++;
	}

	public Action getAction(AlarmRule rule) {
		if (continuous < rule.getConsecutive())
			return Action.NONE;
		else if (continuous == rule.getConsecutive())
			return Action.ON;
		else if ((continuous-rule.getConsecutive()) % rule.getRemind() == 0)
			return Action.REMIND;
		return Action.NONE;
	}
}
