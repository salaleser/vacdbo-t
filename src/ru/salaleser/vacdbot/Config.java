package ru.salaleser.vacdbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class Config {
	private String token;

	Config() {
		readToken();
	}

	String getToken() {
		return token;
	}

	private void readToken() {
		File file = new File("vacdbo-t.token");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			token = bufferedReader.readLine();
			bufferedReader.close();
		} catch (IOException e) {
			DLog.add(e.getMessage());
			e.printStackTrace();
		}
	}
}
