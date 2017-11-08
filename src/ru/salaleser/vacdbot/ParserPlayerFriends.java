package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

class ParserPlayerFriends {

	ArrayList<StringBuilder> parse(StringBuilder sb) {
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<StringBuilder> stringBuildersList = new ArrayList<>();
		int steamids = 0;
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONObject friendslist = (JSONObject) jsonObject.get("friendslist");
			JSONArray friends = (JSONArray) friendslist.get("friends");

			for (Object item : friends) {
				JSONObject friend = (JSONObject) item;
				stringBuilder.append((String) friend.get("steamid"));
				stringBuilder.append(",");
				steamids++;
				if (steamids == 100) {
					stringBuildersList.add(stringBuilder);
					steamids = 0;
				}
				stringBuildersList.add(stringBuilder);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return stringBuildersList;
	}
}
