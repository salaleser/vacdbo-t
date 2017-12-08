package ru.salaleser.vacdbot.vacdbo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.DBHelper;

public class ParserPlayerBans extends Parser {

	ParserPlayerBans() {
		table = "player_bans";
	}

	@Override
	public boolean parse(StringBuilder sb, String id) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray players = (JSONArray) jsonObject.get("players");
			if (players.isEmpty()) {
				System.out.println("SteamID не существуют");
				return true;
			}
			timeUpdated = System.currentTimeMillis() / 1000L;
			added = 0;
			updated = 0;

			for (Object p : players) {
				JSONObject player = (JSONObject) p;
				steamid = (String) player.get("SteamId");
				boolean communitybanned = player.get("CommunityBanned") != null && (boolean) player.get("CommunityBanned");
				boolean vacbanned = player.get("VACBanned") != null && (boolean) player.get("VACBanned");
				long numberofvacbans;
				if (player.get("NumberOfVACBans") != null) numberofvacbans = (long) player.get("NumberOfVACBans");
				else numberofvacbans = -1;
				long dayssincelastban;
				if (player.get("DaysSinceLastBan") != null) dayssincelastban = (long) player.get("DaysSinceLastBan");
				else dayssincelastban = -1;
				long numberofgamebans;
				if (player.get("NumberOfGameBans") != null) numberofgamebans = (long) player.get("NumberOfGameBans");
				else numberofgamebans = -1;
				String economyban;
				if (player.get("EconomyBan") != null) economyban = (String) player.get("EconomyBan");
				else economyban = null;

				String[] columns = new String[] {
						steamid,
						String.valueOf(communitybanned),
						String.valueOf(vacbanned),
						String.valueOf(numberofvacbans),
						String.valueOf(dayssincelastban),
						String.valueOf(numberofgamebans),
						economyban,
						String.valueOf(timeUpdated)
				};
				if (isExists(null, null)) {
					if (DBHelper.update(table, columns)) updated++;
				} else {
					if (DBHelper.insert(table, columns)) added++;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Ошибка парсера, повторяю операцию...");
			return false;
		}
		System.out.println("Добавлено: " + added + " / Обновлено: " + updated);
		Config.addTotalUpdated(updated);
		Config.addTotalAdded(added);
		return true;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР