package ru.salaleser.vacdbot.bot;

import java.io.*;
import java.util.HashMap;

public class Config {
	private static HashMap<String, String> config = new HashMap<>();

	static String getToken() {
		return config.get("Token");
	}

	public static String getTrainingServerAddress() {
		return config.get("TrainingServerAddress");
	}

	public static int getPollCountdown() {
		return Integer.parseInt(config.get("PollCountdown"));
	}

	public static void setPollCountdown(String c) {
		config.put("PollCountdown", c);
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
