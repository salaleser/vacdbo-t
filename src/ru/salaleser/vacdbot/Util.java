package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Util {
	public static final long MIN_STEAMID64 = 76561197960265729L;
	public static final long MAX_STEAMID64 = 76561202255233023L;
	public static HashMap<String, String> mapSteamidDiscordid = storeToHashMapFromFile("txt/ids.txt", "=", false);
	public static HashMap<String, String> mapDiscordidSteamid = storeToHashMapFromFile("txt/ids.txt", "=", true);

	/**
	 * Проверяет аргумент на соответствие числу
	 *
	 * @param string проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isNumeric(String string) {
		return string.matches("\\d+") &&
				string.length() < 10 &&
				Integer.parseInt(string) > 0;
	}

	/**
	 * Проверяет аргумент на соответствие SteamID64
	 *
	 * @param string проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isSteamID64(String string) {
		return string.length() == 17 &&
				string.matches("\\d+") &&
				Long.parseLong(string) > MIN_STEAMID64 &&
				Long.parseLong(string) < MAX_STEAMID64;
	}

	/**
	 * Проверяет аргумент на соответствие Discord ID
	 * @param string проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isDiscordUser(String string) {
		return string.length() == 18 && string.matches("\\d+")||
				string.startsWith("<@") && string.endsWith(">");
	}

	/**
	 * Возвращает правильное окончание для слова "день"
	 *
	 * @param days количество дней
	 * @return окончание (и суффикс, но это не важно)
	 */
	public static String ending(int days) {
		if (String.valueOf(days).endsWith("1") && !String.valueOf(days).endsWith("11")) return "ень";
		if (String.valueOf(days).endsWith("2") ||
				String.valueOf(days).endsWith("3") ||
				String.valueOf(days).endsWith("4"))
			return "ня";
		return "ней";
	}

	/**
	 * Возвращает SteamID64, если такой Discord User есть в mapSteamidDiscordid
	 *
	 * @param discordid Discord String ID
	 * @return  SteamID64
	 */
	public static String getSteamidByDiscordUser(String discordid) {
		discordid = discordid.replaceAll("[<@!>]", "");
		String steamid = mapDiscordidSteamid.get(discordid);
		if (steamid == null) return "ноунейм какой-то";
		return steamid;
	}

	/**
	 * Возвращает Discord User, если такой SteamID64 есть в mapDiscordidSteamid
	 *
	 * @param steamid SteamID64
	 * @return Discord User
	 */
	public static String getDiscordUserBySteamid(String steamid) {
		String discordid = mapSteamidDiscordid.get(steamid);
		if (discordid == null) return "ноунейм какой-то";
		return "<@" + discordid + ">";
	}

	/**
	 * Загружает из файла filename построчно данные, разделённые separator
	 * в hashmap и возвращает его
	 *
	 * @param filename имя файла из которого надо считывать данные
	 * @param separator разделитель
	 * @param swap флаг прямого или обратного распределения пар key-value
	 *             (то есть если true, то значение слева от separator
	 *             будет key, а спарава — value; если false, то наоборот)
	 * @return hashmap из ключей и значений, считанных из filename
	 */
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
			Logger.error("Ошибка при чтении файла " + filename);
			return null;
		}
	}

	public static String code(String text) {
		return "`" + text + "`";
	}

	public static String block(String text) {
		return "```" + text + "```";
	}

	public static String i(String text) {
		return "*" + text + "*";
	}

	public static String b(String text) {
		return "**" + text + "**";
	}

	public static String bi(String text) {
		return "***" + text + "***";
	}

	public static String s(String text) {
		return "~~" + text + "~~";
	}

	public static String u(String text) {
		return "__" + text + "__";
	}

	public static String ui(String text) {
		return "__*" + text + "*__";
	}

	public static String ub(String text) {
		return "__**" + text + "**__";
	}

	public static String ubi(String text) {
		return "__***" + text + "***__";
	}
}

/*
http://steamcommunity.com/inventory/76561198095972970/730/2?l=russian&count=5000
http://steamcommunity.com/profiles/76561198095972970/inventory/json/753/1

http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970
http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970

http://api.steampowered.com/ISteamApps/GetAppList/v2
*/
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР