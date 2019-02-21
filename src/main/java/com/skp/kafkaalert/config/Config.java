package com.skp.kafkaalert.config;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.util.FileHelper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

		return createFromResource(ConfigPath.getProcessConfPath(), ConfigPath.getRegexConfPath());
	}

	private static boolean check(String processConfPath, String regexConfPath) {
		if (!FileHelper.exist(processConfPath) || !FileHelper.exist(regexConfPath))
			return false;
		return true;
	}

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
