package com.skp.kafkaalert.process;

import java.util.Collection;

import com.skp.kafkaalert.entity.Alarm;
import com.skp.kafkaalert.entity.AlarmFieldKey;
import com.skp.kafkaalert.entity.AlarmRule;
import com.skp.kafkaalert.entity.AlarmStatus;
import com.skp.kafkaalert.entity.AlarmValueKey;

import lombok.Data;

@Data
public class AlarmHolder {
	AlarmFieldKey fieldKey;
	AlarmValueKey valueKey;
	Alarm alarm;
	AlarmRule rule;
	AlarmStatus status;
	long value;
}
