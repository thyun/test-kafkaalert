package com.skp.kafkaalert.config;

import lombok.Data;

@Data
public class ConfigOutput {
	String type;
	String path;
	int max;
}
