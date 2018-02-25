package ru.salaleser.vacdbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
		return string.matches("^\\d{1,9}$");
	}

	/**
	 * Проверяет аргумент на соответствие SteamID64
	 *
	 * @param verifiable проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isSteamID64(String verifiable) {
		return verifiable.matches("^\\d{17}$") &&
//				Long.parseLong(verifiable) > FIRST_STEAMID64 && //почему-то попадаются стимайди меньше этого числа О_о
				Long.parseLong(verifiable) < LAST_STEAMID64;
	}

	/**
	 * Проверяет аргумент на соответствие Discord ID
	 *
	 * @param verifiable проверяемая строка
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isDiscordUser(String verifiable) {
		if (!verifiable.matches("^<?@?!?\\d{18}>?$")) return false;
		String userId = verifiable.replaceAll("[<@!>]", "");
		return Bot.getClient().getUserByID(Long.parseLong(userId)) != null;
	}

	/**
	 * Проверяет аргумент на соответствие Discord ID
	 *
	 * @param verifiable проверяемая строка
	 * @param guild гильдия
	 * @return true, если строка успешно прошла проверку, false — если нет
	 */
	public static boolean isDiscordRole(String verifiable, IGuild guild) {
		if (!verifiable.matches("^<?@?&?\\d{18}>?$")) return false;
		String roleId = verifiable.replaceAll("[<@&>]", "");
		return guild.getRoleByID(Long.parseLong(roleId)) != null;
	}

	public static boolean isCommunityID(String verifiable) {
		return verifiable.matches("^https?://steamcommunity.com/\\S+$");
	}

	/**
	 * Возвращает правильное окончание для слова "день"
	 *
	 * @param days количество дней
	 * @return окончание (и суффикс, но это не важно)
	 */
	public static String ending(int days) {
		if (String.valueOf(days).endsWith("1") && !String.valueOf(days).endsWith("11")) return "ень";
		if (String.valueOf(days).endsWith("2") || String.valueOf(days).endsWith("3") || String.valueOf(days).endsWith("4"))
			return "ня";
		return "ней";
	}

	/**
	 * Возвращает SteamID64, если он присвоен указанному пользователю в таблице "users"
	 *
	 * @param discordid Discord ID
	 * @return SteamID64
	 */
	public static String getSteamidByDiscordid(String discordid) {
		discordid = discordid.replaceAll("[<@!>]", "");
		//если такого пользователя еще не было в базе данных, то добавить его:
		if (!DBHelper.isExists("users", "discordid", discordid)) refreshUsers();
		String sql = "SELECT steamid FROM users WHERE discordid = '" + discordid + "'";
		String steamid = DBHelper.executeQuery(sql)[0][0];
		if (steamid == null || !Util.isSteamID64(steamid)) steamid = "noname";
		return steamid;
	}

	/**
	 * Возвращает SteamID64 пользователя на основании его ссылки на профиль.
	 * Парсит xml-версию страницы профиля Steam с адресом вида "http://steamcommunity.com/id/salaleser/".
	 *
	 * @param communityid URL профиля пользователя Steam
	 * @return SteamID64
	 */
	public static String getSteamidByCommunityid(String communityid) {
		Document document;
		String request = communityid + "?xml=1";
		try {
			document = Jsoup.connect(request).get();
		} catch (IllegalArgumentException e) {
			return "неправильный адрес";
		} catch (IOException e) {
			e.printStackTrace();
			return "пустой ответ";
		}
		return document.select("steamID64").first().text();
	}

	/**
	 * Возвращает Discord ID, если такой есть в базе данных
	 *
	 * @param steamid SteamID64
	 * @return Discord ID
	 */
	public static String getDiscordidBySteamid(String steamid) {
		if (!DBHelper.isExists("users", "steamid", steamid)) return "noname";
		String sql = "SELECT discordid FROM users WHERE steamid = '" + steamid + "'";
		return DBHelper.executeQuery(sql)[0][0];
	}

	/**
	 * Перебирает всех пользователей и проверяет на наличие их ID в таблице "users" базы данных.
	 * Если пользователя нет, то добавляет его Discord ID в таблицу.
	 *
	 * @return количество добавленных пользователей в базу данных
	 */
	public static int refreshUsers() {
		List<IUser> users = Bot.getClient().getUsers();
		String table = "users";
		int counter = 0;
		for (IUser user : users) {
			if (!DBHelper.isExists(table, "discordid", user.getStringID())) {
				if (DBHelper.insert(table, new String[]{null, user.getStringID()})) {
					counter++;
				}
			}
		}
		return counter;
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
	static HashMap<String, String> storeToHashMapFromFile(String filename, String separator, boolean swap) {
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
		byte[] utf8Bytes;
		try {
			utf8Bytes = unicoded.getBytes("UTF8");
			return new String(utf8Bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return unicoded;
	}

	/**
	 * Возвращает приветствие на основании текущего времени суток
	 *
	 * @return приветствие
	 */
	public static String getTimeOfDay() {
		String timeOfDay = "Доброй ночи!";
		int currentHour = LocalDateTime.now().getHour();
		if (currentHour >= 6 && currentHour < 12) timeOfDay = "Доброе утро!";
		else if (currentHour >= 12 && currentHour < 18) timeOfDay = "Добрый день!";
		else if (currentHour >= 18 && currentHour < 23) timeOfDay = "Добрый вечер!";
		return timeOfDay;
	}

	public static String toRubKop(String rubkop) {
		if (rubkop.length() == 1) rubkop = "00" + rubkop;
		if (rubkop.length() == 2) rubkop = "0" + rubkop;
		String rub = rubkop.substring(0, rubkop.length() - 2);
		String kop = rubkop.substring(rubkop.length() - 2, rubkop.length());
		return rub + "," + kop + "₽";
	}

	/**
	 * Возвращает названия колонок
	 *
	 * @param table таблица
	 * @param columnNames названия колонок (fixme я уже сам запутался зачем это надо)
	 * @return массив названий колонок
	 */
	static String[][] getColNames(String table, String[] columnNames) {
		StringBuilder columnNamesBuilder = new StringBuilder();
		if (!columnNames[0].equals("*")) {
			columnNamesBuilder.append(" AND column_name = '").append(columnNames[0]).append("'");
			for (int i = 1; i < columnNames.length; i++) {
				columnNamesBuilder.append(" OR column_name = '").append(columnNames[i]).append("'");
			}
		}
		String sql = "SELECT column_name FROM information_schema.columns" +
				" WHERE information_schema.columns.table_name = '" + table + "'" + columnNamesBuilder;
		return DBHelper.executeQuery(sql);
	}

	/**
	 * Формирует строку-таблицу псевдографикой на основе таблицы из базы данных
	 *
	 * @param table таблица в БД из которой брать данные для формирования таблицы
	 * @param columnNames имена столбцов (* — все столбцы таблицы) fixme если указать свои столбцы таблица сбивается
	 * @param data двумерный массив строк со рядами (rows) из БД для заполнения таблицы
	 * @return сформированная таблица в виде строки
	 */
	public static String makeTable(String table, String[] columnNames, String[][] data) {
		// TODO: 17.02.2018 добавить заголовок в некоторых случаях
		//сначала выясню какая колонка самая широкая:
		//получаю названия колонок в массив:
		String[][] colNames = getColNames(table, columnNames);
		//заполняю массив lengths длинами названий колонок:
		int[] lengths = new int[colNames.length];
		for (int i = 0; i < colNames.length; i++) {
			String query = "SELECT LENGTH(CAST(" + colNames[i][0] + " AS TEXT)) FROM " + table +
					" WHERE " + colNames[i][0] + " IS NOT NULL ORDER BY length DESC";
			try {
				lengths[i] = Integer.parseInt(DBHelper.executeQuery(query)[0][0]);
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				lengths[i] = 4;
			}
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

	/**
	 * Возвращает уровень доступа пользователя todo для каждой гильдии должны быть свои настройки
	 *
	 * @param discordid Discord ID пользователя, для которого надо узнать ранг
	 * @return уровень доступа пользователя (ранг)
	 */
	public static int getPriority(String discordid) {
		String steamid = getSteamidByDiscordid(discordid);
		String sql = "SELECT priority FROM users WHERE steamid = '" + steamid + "'";
		return Integer.parseInt(DBHelper.executeQuery(sql)[0][0]);
	}

	/**
	 * Оболочка для DBHelper.getOption() для быстрого доступа к уровню доступа для команды
	 *
	 * @param guildid гильдия
	 * @param commandName имя команды
	 * @return минимальный уровень доступа для использования команды
	 */
	public static int getPermission(String guildid, String commandName) {
		String permission = DBHelper.getOption(guildid, commandName, "level");
		if (permission == null) permission = "1";
		return Integer.parseInt(permission);
	}

	public static String getSound(String discordid, String column) {
		String steamid = getSteamidByDiscordid(discordid);
		String sql = "SELECT " + column + " FROM users WHERE steamid = '" + steamid + "'";
		return DBHelper.executeQuery(sql)[0][0];
	}

	/**
	 * Метод для короткой записи задержки для избежания RateLimitException
	 */
	public static void delay(long timeout) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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