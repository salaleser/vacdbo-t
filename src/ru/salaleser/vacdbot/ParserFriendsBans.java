package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class ParserFriendsBans {
	/**
	 * Парсит json с банами
	 *
	 * @param sb json
	 * @return map профилей с вак-банами и дни с момента последнего бана
	 */
	public HashMap<String, Integer> parse(StringBuilder sb) {
		HashMap<String, Integer> vacbannedMap = new HashMap<>();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray players = (JSONArray) jsonObject.get("players");
			for (Object item : players) {
				JSONObject player = (JSONObject) item;
				String steamid = (String) player.get("SteamId");
				boolean communitybanned = (boolean) player.get("CommunityBanned");
				boolean vacbanned = (boolean) player.get("VACBanned");
//				long numberofvacbans = (long) player.get("NumberOfVACBans");
				long dayssincelastban = (long) player.get("DaysSinceLastBan");
//				long numberofgamebans = (long) player.get("NumberOfGameBans");
//				String economyban = (String) player.get("EconomyBan");
				if (vacbanned || communitybanned) vacbannedMap.put(steamid, (int) dayssincelastban);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return vacbannedMap;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР