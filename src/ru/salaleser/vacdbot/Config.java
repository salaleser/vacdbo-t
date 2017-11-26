package ru.salaleser.vacdbot;

import java.util.HashMap;

public class Config {
	private static HashMap<String, String> config = new HashMap<>();
	public static final String BASE_URL = "https://api.steampowered.com";
	public static int totalScanned, totalAdded, totalUpdated;

	public static String getToken() {
		return config.get("Token");
	}
	public static String getTrainingServerAddress() {
		return config.get("TrainingServerAddress");
	}
	public static String getSteamWebApiKey() {
		return config.get("SteamWebApiKey");
	}
	public static String getCsgotmApiKey() {
		return config.get("CsgotmApiKey");
	}

	public static String getDBDriver() {
		return config.get("DBDriver");
	}
	public static String getDBUrl() {
		return config.get("DBUrl");
	}
	public static String getDBLogin() {
		return config.get("DBLogin");
	}
	public static String getDBPassword() {
		return config.get("DBPassword");
	}

	public static int getScannerThreads() {
		return Integer.parseInt(config.get("ScannerThreads"));
	}
	public static void setScannerThreads(String value) {
		config.put("ScannerThreads", value);
	}

	public static int getPollCountdown() {
		return Integer.parseInt(config.get("PollCountdown"));
	}
	public static void setPollCountdown(String value) {
		config.put("PollCountdown", value);
	}

	public static int getTotalScanned() {
		return totalScanned;
	}
	public static void addTotalScanned(int count) {
		totalScanned += count;
	}
	public static int getTotalAdded() {
		return totalAdded;
	}
	public static void addTotalAdded(int count) {
		totalAdded += count;
	}
	public static int getTotalUpdated() {
		return totalUpdated;
	}
	public static void addTotalUpdated(int count) {
		totalUpdated += count;
	}

	/**
	 * Читает конфигурационный файл
	 * @param filename имя конфигурационного файла (обычно это vacdbot.cfg, но
	 *                 путь к нему может отличаться
	 * @return true, если файл загрузился успешно, false — если нет
	 */
	public static boolean readConfigFile(String filename) {
		Logger.info("Пытаюсь прочитать конфигурационный файл " + filename + "...");
		config = Util.storeToHashMapFromFile(filename, "=", false);
		if (config != null) {
			Logger.info("Конфигурационный файл считан успешно.");
			return true;
		} else {
			Logger.info("Ошибка загрузки конфигурационного файла!");
			return false;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР