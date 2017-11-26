package ru.salaleser.vacdbot;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class Util {
	public static final long MIN_STEAMID64 = 76561197960265729L;
	public static final long MAX_STEAMID64 = 76561202255233023L;

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
		String table = "ids";
		String sql = "SELECT steamid FROM " + table + " WHERE discordid = \'" + discordid + "\'";
		Connection connection = null;
		Statement statement = null;
		String steamid = null;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				steamid = resultSet.getString("steamid");
			}
		} catch (SQLException | ClassNotFoundException e) {
			Logger.error("Ошибка чтения из базы данных: " + e.getMessage());
		} finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
			}
		}
		if (steamid == null) return "ноунейм какой-то";
		return steamid;
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

	public static String decode(String unicoded) {
		byte[] utf8Bytes = new byte[0];
		try {
			utf8Bytes = unicoded.getBytes("UTF8");
			return new String(utf8Bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return unicoded;
	}

	public static String toRubKop(String rubkop) {
		if (rubkop.length() == 1) rubkop = "00" + rubkop;
		if (rubkop.length() == 2) rubkop = "0" + rubkop;
		String rub = rubkop.substring(0, rubkop.length() - 2);
		String kop = rubkop.substring(rubkop.length() - 2, rubkop.length());
		return rub + "," + kop + "₽";
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
http://steamcommunity.com/inventory/76561198095972970/730/2?l=russian&count=1
http://steamcommunity.com/profiles/76561198095972970/inventory/json/730/2

http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970
http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970

http://api.steampowered.com/ISteamApps/GetAppList/v2
*/
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР