package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

class ParserPlayerBans {

	ArrayList<String> parse(StringBuilder sb) {
		ArrayList<String> vacbannedFriends = new ArrayList<>();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray players = (JSONArray) jsonObject.get("players");

			for (Object item : players) {
				JSONObject player = (JSONObject) item;
				String steamid = (String) player.get("SteamId");
//				boolean communitybanned = (boolean) player.get("CommunityBanned");
//				boolean vacbanned = (boolean) player.get("VACBanned");
//				int numberofvacbans = (int) player.get("NumberOfVACBans");
				int dayssincelastban = (int) player.get("DaysSinceLastBan");
//				int numberofgamebans = (int) player.get("NumberOfGameBans");
//				String economyban = (String) player.get("EconomyBan");
				if (dayssincelastban < 7) {
					vacbannedFriends.add(steamid);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return vacbannedFriends;
	}
}
