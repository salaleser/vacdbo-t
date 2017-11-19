package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;

import java.io.*;
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

	public static boolean readConfigFile(String path) {
		Bot.gui.addText("Пытаюсь прочитать конфигурационный файл...");
		File file = new File(path + "vacdbot.cfg");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] args = line.split("=");
				config.put(args[0], args[1]);
			}
			bufferedReader.close();
			Bot.gui.addText("Конфигурационный файл считан успешно.");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Bot.gui.addText("Ошибка загрузки конфигурационного файла!");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Bot.gui.addText(e.getMessage());
			return false;
		}
	}
}
