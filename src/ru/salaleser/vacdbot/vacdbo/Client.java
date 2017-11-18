package ru.salaleser.vacdbot.vacdbo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Client extends Thread {

	private HttpURLConnection connection;
	private StringBuilder response;
	private int timeout;

	Client() {
		timeout = 1000;
	}

	StringBuilder connect(String query) {
		try {
			connection = (HttpURLConnection) new URL(query).openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.connect();
			response = new StringBuilder();
			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				InputStream is = connection.getInputStream(); //самая долгая операция
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				String line;
				while ((line = in.readLine()) != null) {
					response.append(line);
					response.append("\n");
				}
			} else {
				System.out.println("fail: " + connection.getResponseCode() +
						", " + connection.getResponseMessage());
			}
		} catch (final java.net.SocketTimeoutException e) {
			Log.add("Время ожидания вышло, повторяю операцию...");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return response;
	}
}
