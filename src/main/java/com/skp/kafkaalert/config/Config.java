package com.skp.kafkaalert.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.kafkaalert.event.LogEvent;
import com.skp.kafkaalert.process.ProcessScheme;
import com.skp.util.FileHelper;

import lombok.Data;

@Data
public class Config {
	static ObjectMapper objectMapper = new ObjectMapper();

	ConfigRegex regex;
	ConfigInput input;
	ConfigProcess process;
	ConfigOutput output;

	public static Config create() {
		if (!check(ConfigPath.getProcessConfPath(), ConfigPath.getRegexConfPath()))
			return null;

		String processConfStr = FileHelper.getFileFromPath(ConfigPath.getProcessConfPath());
		List<String> regexConfStrList = FileHelper.getFileLineListFromResource(ConfigPath.getRegexConfPath());

		try {
			Config config = objectMapper.readValue(processConfStr, Config.class);
			config.setRegex(ConfigRegex.create(regexConfStrList));
			return config;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static boolean check(String processConfPath, String regexConfPath) {
		if (!FileHelper.exist(processConfPath) || !FileHelper.exist(regexConfPath))
			return false;
		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// For test only
	/////////////////////////////////////////////////////////////////////////////////
	public static Config createFromResource(String processConfPath, String regexConfPath) {
		if (!checkFromResource(processConfPath, regexConfPath))
			return null;
		String processConfStr = FileHelper.getFileFromResource(processConfPath);
		List<String> regexConfStrList = FileHelper.getFileLineListFromResource(regexConfPath);

		try {
			Config config = objectMapper.readValue(processConfStr, Config.class);
			config.setRegex(ConfigRegex.create(regexConfStrList));
			return config;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static boolean checkFromResource(String processConfPath, String regexConfPath) {
		if (FileHelper.getFileFromResource(processConfPath) == null || FileHelper.getFileFromResource(regexConfPath) == null)
			return false;
		return true;
	}

}
