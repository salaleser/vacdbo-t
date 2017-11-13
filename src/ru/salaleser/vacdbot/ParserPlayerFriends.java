package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ParserPlayerFriends {
	/**
	 * Парсит json с друзьями
	 *
	 * @param sb json
	 * @return массив из сотен SteamIDs
	 */
	public ArrayList<StringBuilder> parse(StringBuilder sb) {
		StringBuilder steamIDs = new StringBuilder();
		ArrayList<StringBuilder> hundredsOfSteamIDs = new ArrayList<>();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONObject friendslist = (JSONObject) jsonObject.get("friendslist");
			JSONArray friends = (JSONArray) friendslist.get("friends");
			for (int i = 0; i < friends.size(); i++) {
				JSONObject friend = (JSONObject) friends.get(i);
				steamIDs.append((String) friend.get("steamid"));
				if (i % 100 == 0 && i != 0) {
					hundredsOfSteamIDs.add(steamIDs);
					steamIDs = new StringBuilder();
				} else if (i != friends.size() - 1) {
					steamIDs.append(",");
				}
			}
			hundredsOfSteamIDs.add(steamIDs);
			//последним элементом добавляю количество друзей
			hundredsOfSteamIDs.add(new StringBuilder(String.valueOf(friends.size())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return hundredsOfSteamIDs;
	}
}
