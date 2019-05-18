package com.ab.util;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Common {
	private final static Logger LOGGER = LoggerFactory.getLogger(Common.class);

	/**
	 * Save JSON string to JSON file
	 * 
	 * @param json
	 * @param filePath
	 */
	public void saveJson(String json, String filePath) {
		try {
			LOGGER.info("Writing data to JSON file.");
			FileWriter writer = new FileWriter(filePath);
			writer.write(json);
			writer.close();

		} catch (IOException e) {
			LOGGER.error("Failed to write data to JSON file. Message: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
