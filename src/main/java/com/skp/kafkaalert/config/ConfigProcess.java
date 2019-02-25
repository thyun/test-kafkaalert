package com.skp.kafkaalert.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class ConfigProcess {
	ArrayList<Object> alarms;
	ArrayList<Object> notification_groups;
}
