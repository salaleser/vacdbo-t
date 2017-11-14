package ru.salaleser.vacdbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {
	private static HashMap<String, String> config = new HashMap<>();

	Config() {
		readConfigFile();
	}

	static String getToken() {
		return config.get("Token");
	}

	public static String getTrainingServerAddress() {
		return config.get("TrainingServerAddress");
	}

	private static void readConfigFile() {
		File file = new File("vacdbot.cfg");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] args = line.split("=");
				config.put(args[0], args[1]);
			}
			bufferedReader.close();
			System.out.println("Конфигурационный файл считан успешно.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Ошибка при чтении конфигурационного файла!");
			new Gui();
		}
	}
}
