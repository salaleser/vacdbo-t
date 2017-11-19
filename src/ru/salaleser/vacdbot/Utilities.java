package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Utilities {

	public static final long MIN_STEAMID64 = 76561197960265729L;
	public static final long MAX_STEAMID64 = 76561202255233023L;
	public static HashMap<String, String> mapSteamidDiscordid = storeToHashMapFromFile("txt/ids.txt", "=", false);
	public static HashMap<String, String> mapDiscordidSteamid = storeToHashMapFromFile("txt/ids.txt", "=", true);

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

	public static String getSteamidByDiscordUser(String discordid) {
		String steamid = mapDiscordidSteamid.get(discordid);
		if (steamid == null) return "ноунейм какой-то";
		return "```" + steamid + "```";
	}

	public static String getDiscordNameBySteamid(String steamid) {
		String discordid = mapSteamidDiscordid.get(steamid);
		if (discordid == null) return "ноунейм какой-то";
		return "<@" + discordid + ">";
	}

	public static HashMap<String, String> storeToHashMapFromFile(
			String filename, String separator, boolean swap) {
		File file = new File(filename);
		HashMap<String, String> hashMap = new HashMap<>();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] args = line.split(separator);
				if (swap) hashMap.put(args[1], args[0]);
				else hashMap.put(args[0], args[1]);
			}
			bufferedReader.close();
			return hashMap;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Ошибка при чтении файла " + filename);
			return null;
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
