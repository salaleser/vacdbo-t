package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ParserFriendsBans {
	/**
	 * Парсит json с банами
	 * @param sb json
	 * @param days дней с последнего бана
	 * @return лист профилей с вак-банами
	 */
	public ArrayList<String> parse(StringBuilder sb, int days) {
		ArrayList<String> vacbannedFriends = new ArrayList<>();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray players = (JSONArray) jsonObject.get("players");
			for (Object item : players) {
				JSONObject player = (JSONObject) item;
				String steamid = (String) player.get("SteamId");
//				boolean communitybanned = (boolean) player.get("CommunityBanned");
				boolean vacbanned = (boolean) player.get("VACBanned");
//				long numberofvacbans = (long) player.get("NumberOfVACBans");
				long dayssincelastban = (long) player.get("DaysSinceLastBan");
//				long numberofgamebans = (long) player.get("NumberOfGameBans");
//				String economyban = (String) player.get("EconomyBan");
				if (vacbanned && dayssincelastban < days) {
					vacbannedFriends.add(steamid);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return vacbannedFriends;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР