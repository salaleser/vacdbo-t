package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class JsonWriter {

	ParserSuspects parserSuspects = new ParserSuspects();

	void addSuspect(String steamID64, String description) throws IOException {

		JSONObject suspect = new JSONObject();
		suspect.put("timestamp", System.currentTimeMillis());
		suspect.put("steamid", steamID64);
		suspect.put("description", description);

		JSONArray suspects = new JSONArray();
		suspects.add(suspect);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suspects", suspects);

		try (FileWriter file = new FileWriter("suspects.json")) {
			file.write(jsonObject.toJSONString());
			System.out.println("JSON Object created: " + jsonObject);
		}
	}

	void addSuspect(String timestamp, String steamID64, String description) throws IOException {
		String filePath = "suspects.json";

		JSONObject suspect = new JSONObject();
		suspect.put("timestamp", timestamp);
		suspect.put("steamid", steamID64);
		suspect.put("description", description);

		JSONArray suspects = new JSONArray();
		suspects.add(suspect);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("suspects", suspects);

		try (FileWriter writer = new FileWriter(filePath, true)) {
			BufferedWriter bufferWriter = new BufferedWriter(writer);
			bufferWriter.write(jsonObject.toJSONString());
			bufferWriter.close();
			System.out.println("JSON Object added: " + jsonObject);
		}
	}

	void undo() {

	}

	void remove(String steamid) {
		StringBuilder json = getJsonFromFile();
		for (String item : parserSuspects.parse(json)) {
			if (item.startsWith(steamid)) continue;
			String items[] = item.split(" ");
			StringBuilder description = new StringBuilder();
			for (int i = 2; i < items.length; i++) {
				description.append(items[i]).append(" ");
			}
			try {
				addSuspect(items[0], items[1], description.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	ArrayList<String> getSuspects() {
		return parserSuspects.parse(getJsonFromFile());
	}

	StringBuilder getJsonFromFile() {
		File file = new File("suspects.json");
		StringBuilder jsonSuspects = new StringBuilder();
		String line;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				jsonSuspects.append(line);
				jsonSuspects.append("\n");
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonSuspects;
	}
}
