package ru.salaleser.vacdbot;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {
	public static final long FIRST_STEAMID64 = 76561197960265729L;
	public static final long LAST_STEAMID64 = 76561202255233023L;

	/**
	 * Проверяет аргумент на соответствие числу
	 *
	 * @param string проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isNumeric(String string) {
		return string.matches("\\d+") && string.length() < 10 && Integer.parseInt(string) > 0;
	}

	/**
	 * Проверяет аргумент на соответствие SteamID64
	 *
	 * @param string проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isSteamID64(String string) {
		return string.length() == 17 && string.matches("\\d+") &&
				Long.parseLong(string) > FIRST_STEAMID64 &&
				Long.parseLong(string) < LAST_STEAMID64;
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
		String sql = "SELECT steamid FROM ids WHERE discordid = \'" + discordid + "\'";
		return DBHelper.executeQuery(sql)[0][0];
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

	public static String makeTable(String table, String[] columnNames, String[][] data) {
		//сначала выясню какая колонка самая широкая:
		//получаю названия колонок в массив:
		StringBuilder columnNamesBuilder = new StringBuilder();
		if (!columnNames[0].equals("*")) {
			columnNamesBuilder.append(" AND column_name = '").append(columnNames[0]).append("'");
			for (int i = 1; i < columnNames.length; i++) {
				columnNamesBuilder.append(" OR column_name = '").append(columnNames[i]).append("'");
			}
		}
		String sql = "SELECT column_name FROM information_schema.columns" +
				" WHERE information_schema.columns.table_name = '" + table + "'" + columnNamesBuilder;
		String[][] colNames = DBHelper.executeQuery(sql);
		//заполняю массив lengths длинами названий колонок:
		int[] lengths = new int[colNames.length];
		for (int i = 0; i < colNames.length; i++) {
			String query = "SELECT LENGTH(CAST(" + colNames[i][0] + " AS TEXT)) FROM " + table +
					" WHERE " + colNames[i][0] + " IS NOT NULL ORDER BY length DESC";
			lengths[i] = Integer.parseInt(DBHelper.executeQuery(query)[0][0]);
		}

		StringBuilder rowBuilder = new StringBuilder();

		int size = data[0].length;
		//первый ряд таблицы (верхняя граница):
		for (int i = 0; i < size; i++) {
			if (i == 0) rowBuilder.append("┌");
			else rowBuilder.append("┬");
			rowBuilder.append(dublicate("─", lengths[i] - 1));
		}
		rowBuilder.append("┐\n");

		//названия столбцов таблицы:
		for (int i = 0; i < size; i++) {
			String colName = colNames[i][0];
			if (colNames[i][0].length() > lengths[i]) colName = colNames[i][0].substring(0, lengths[i] - 1) + "…";
			rowBuilder.append("│").append(addSpaces(colName, lengths[i]));
		}
		rowBuilder.append("│\n");

		//первый ряд таблицы (средняя граница):
		for (int i = 0; i < size; i++) {
			if (i == 0) rowBuilder.append("├");
			else rowBuilder.append("┼");
			rowBuilder.append(dublicate("─", lengths[i] - 1));
		}
		rowBuilder.append("┤\n");

		// TODO: 01.12.2017 продолжить извращаться с таблицей
		for (String[] row : data) {
			rowBuilder.append("│");
			for (int i = 0; i < row.length; i++) {
				if (row[i] == null) rowBuilder.append(addSpaces("NULL", lengths[i])).append("│");
				else if (row[i].equals("t")) rowBuilder.append(addSpaces("TRUE", lengths[i])).append("│");
				else if (row[i].equals("f")) rowBuilder.append(addSpaces("FALSE", lengths[i])).append("│");
				else rowBuilder.append(addSpaces(row[i], lengths[i])).append("│");
			}
			rowBuilder.append("\n");
		}

		for (int i = 0; i < size; i++) {
			if (i == 0) rowBuilder.append("└");
			else rowBuilder.append("┴");
			rowBuilder.append(dublicate("─", lengths[i] - 1));
		}
		rowBuilder.append("┘\n");

		return block(rowBuilder.toString());
	}

	/**
	 * Добавляет пробелы к слову, чтобы таблица в дискорде выглядела красиво
	 *
	 * @param element исходная строка
	 * @return строка с добавленными пробелами
	 */
	private static String addSpaces(String element, int columnWidth) {
		int spacesToAdd = columnWidth - element.length();
		if (spacesToAdd < 0) return element;
		StringBuilder stringBuilder = new StringBuilder(element);
		for (int i = 0; i < spacesToAdd; i++) stringBuilder.append(" ");
		return stringBuilder.toString();
	}

	private static String dublicate(String character, int times) {
		StringBuilder stringBuilder = new StringBuilder(character);
		for (int i = 0; i < times; i++) stringBuilder.append(character);
		return stringBuilder.toString();
	}

	static String getQMarks(int number) {
		StringBuilder qMarks = new StringBuilder();
		for (int i = 0; i < number; i++) qMarks.append(",?");
		return qMarks.substring(1);
	}

	// Методы для упрощения форматирования текста в дискорде:
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
	public static String did(String text) {
		return "<@" + text + ">";
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