package com.skp.kafkaalert.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommonFieldValue {
	String field;
	String value;
}
