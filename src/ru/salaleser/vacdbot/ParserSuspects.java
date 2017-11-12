package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Timestamp;
import java.util.ArrayList;

class ParserSuspects {
	/**
	 * Парсит json подозреваемых
	 *
	 * @param sb json
	 * @return лист профилей с вак-банами
	 */
	ArrayList<String> parse(StringBuilder sb) {
		ArrayList<String> suspectsList = new ArrayList<>();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray suspects = (JSONArray) jsonObject.get("suspects");
			for (Object item : suspects) {
				JSONObject suspect = (JSONObject) item;
				long timestamp = (long) suspect.get("timestamp");
				Timestamp date = new Timestamp(timestamp);
				String steamid = (String) suspect.get("steamid");
				String description = (String) suspect.get("description");
				suspectsList.add(steamid + " " +
						date.toLocalDateTime().getDayOfMonth() + "." +
						date.toLocalDateTime().getMonthValue() + "." +
						date.toLocalDateTime().getYear() + "-" +
						date.toLocalDateTime().getHour() + ":" +
						date.toLocalDateTime().getMinute() + " " +
						description);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return suspectsList;
	}
}
