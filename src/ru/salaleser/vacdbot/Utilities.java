package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {

	public static final long MIN_STEAMID64 = 76561197960265729L;
	public static final long MAX_STEAMID64 = 76561202255233023L;

	public static boolean isNumeric(String string) {
		return string.matches("\\d+") && string.length() < 10 && Integer.parseInt(string) > 0;
	}

	public static boolean isSteamID64(String steamID64) {
		return steamID64.length() == 17 &&
				steamID64.matches("\\d+") &&
				Long.parseLong(steamID64) > MIN_STEAMID64 &&
				Long.parseLong(steamID64) < MAX_STEAMID64;
	}

	public static String ending(int days) {
		if (String.valueOf(days).endsWith("1") && !String.valueOf(days).endsWith("11")) return "ень";
		if (String.valueOf(days).endsWith("2") ||
				String.valueOf(days).endsWith("3") ||
				String.valueOf(days).endsWith("4"))
			return "ня";
		return "ней";
	}

	public static String convertTime(long unixTime) {
		Date date = new Date(unixTime * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
		return sdf.format(date);
	}

	public static String getSteamidByDiscordUser(IUser author) {
		switch (author.getStringID()) {
			case "223559816239513601":
				return "76561198095972970";
			case "224234891762597889":
				return "76561198103577490";
			case "224129884727803904":
				return "76561198041743174";
			case "223807770552565762":
				return "76561198187239091";
			case "277876854134931457":
				return "76561198038873933";
			case "278897176271126528":
				return "76561198245710318";
			default:
				return "ноунейм какой-то";
		}
	}

	public static String getDiscordDisplayNameBySteamid(String steamID64, IGuild guild) {
		// FIXME: 09.11.17 убрать хардкод дискорд айди
		switch (steamID64) {
			case "76561198095972970":
				return guild.getUserByID(223559816239513601L).getDisplayName(guild);
			case "76561198103577490":
				return guild.getUserByID(224234891762597889L).getDisplayName(guild);
			case "76561198041743174":
				return guild.getUserByID(224129884727803904L).getDisplayName(guild);
			case "76561198187239091":
				return guild.getUserByID(223807770552565762L).getDisplayName(guild);
			case "76561198038873933":
				return guild.getUserByID(277876854134931457L).getDisplayName(guild);
			case "76561198245710318":
				return guild.getUserByID(278897176271126528L).getDisplayName(guild);
			default:
				return "ноунейм";
		}
	}
}

/*
http://steamcommunity.com/inventory/76561198095972970/730/2?l=russian&count=5000
http://steamcommunity.com/profiles/76561198095972970/inventory/json/753/1

http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970
http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970

http://api.steampowered.com/ISteamApps/GetAppList/v2
*/
