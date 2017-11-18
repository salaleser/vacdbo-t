package ru.salaleser.vacdbot.vacdbo;

public class Settings {
	private static final long MIN_ID = 76561197960265729L;
	private static final long MAX_ID = 76561202255233023L;
	static final String BASE_URL = "https://api.steampowered.com";
	private static final String VERSION = "0.0.1";

	private static String key;
	private static String url;
	private static String login;
	private static String password;

	private static int threads;
	private static long starts, ends;
	private static int totalScanned, totalAdded, totalUpdated;
	private static Settings ourInstance = new Settings();

	private Settings() {
		threads = 20;
		starts = MIN_ID;
		ends = MAX_ID;
		totalScanned = 0;
		totalAdded = 0;
		totalUpdated = 0;

		key = "393819FBF50B3E63C1C6B60515A1AD0B";
		url = "jdbc:postgresql://localhost:5432/postgres";
		login = "postgres";
		password = "postgres";
	}

	static Settings set() {
		return ourInstance;
	}

	static String getVersion() {
		return VERSION;
	}

	static String getKey() {
		return key;
	}

	static void setKey(String key) {
		Settings.key = key;
	}

	static String getUrl() {
		return url;
	}

	static void setUrl(String url) {
		Settings.url = url;
	}

	static String getLogin() {
		return login;
	}

	static void setLogin(String login) {
		Settings.login = login;
	}

	static String getPassword() {
		return password;
	}

	static void setPassword(String password) {
		Settings.password = password;
	}

	static int getThreads() {
		return threads;
	}

	static void setThreads(int threads) {
		Settings.threads = threads;
	}

	static long getStarts() {
		return starts;
	}

	static void setStarts(long starts) {
		Settings.starts = starts;
	}

	static long getEnds() {
		return ends;
	}

	static void setEnds(long ends) {
		Settings.ends = ends;
	}

	static void addTotalAdded(int ids) {
		totalAdded += ids;
	}

	static int getTotalAdded() {
		return totalAdded;
	}

	static void addTotalUpdated(int ids) {
		totalUpdated += ids;
	}

	static int getTotalUpdated() {
		return totalUpdated;
	}

	static void addTotalScanned(int ids) {
		totalScanned += ids;
	}

	static int getTotalScanned() {
		return totalScanned;
	}
}
