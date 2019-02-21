package com.skp.kafkaalert.config;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ConfigScheme {
	ArrayList<CommonFieldValue> metrics;
	ArrayList<ArrayList<CommonFieldValue>> targets;
}
