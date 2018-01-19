package com.amwater.rest.api.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONException;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtility {

	public Properties loadProperties() throws FileNotFoundException, IOException {
		/**
		 * Loading properties file
		 */
		Properties properties = new Properties();
		InputStream input = new FileInputStream(Constants.PROPERTIES_FILE_PATH);
		properties.load(input);
		/**
		 * Setting kafka properties
		 */
		properties.put("bootstrap.servers", properties.getProperty("bootstrap.servers"));
		/*properties.put("serializer.class", properties.getProperty("serializer.class"));
		properties.put("key.serializer", properties.getProperty("key.serializer"));
		properties.put("value.serializer", properties.getProperty("value.serializer"));
		properties.put("security.protocol", properties.getProperty("security.protocol"));*/
		
		properties.put("group.id", properties.getProperty("group.id"));
		properties.put("enable.auto.commit", properties.getProperty("enable.auto.commit"));
		properties.put("auto.commit.interval.ms", properties.getProperty("auto.commit.interval.ms"));
		properties.put("session.timeout.ms", properties.getProperty("session.timeout.ms"));
		properties.put("key.deserializer", properties.getProperty("key.deserializer"));
		properties.put("value.deserializer", properties.getProperty("value.deserializer"));
		return properties;
	}

	public static String createResponse(boolean response, String message) throws JSONException {
		org.json.JSONObject responseData = new org.json.JSONObject();
		responseData.put("success", response);
		responseData.put("reseponseDesc", message);
		responseData.put("reseponseCode", response ? 0 : 1);
		return responseData.toString();
	}
}
