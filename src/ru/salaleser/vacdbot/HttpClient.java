package ru.salaleser.vacdbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class HttpClient {

	private HttpURLConnection connection;
	private StringBuilder response;
	private int timeout;

	public HttpClient() {
		timeout = 1000;
	}

	/**
	 * Http-client
	 *
	 * @param query URL
	 * @return ответ от сервера
	 */
	public StringBuilder connect(String query) {
		try {
			connection = (HttpURLConnection) new URL(query).openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.connect();
			response = new StringBuilder();
			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				InputStream is = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				String line;
				while ((line = in.readLine()) != null) response.append(line).append("\n");
			} else {
				Logger.error("HttpClient: " + connection.getResponseCode() + ", " +
						connection.getResponseMessage() + "(" + query + ")");
			}
		} catch (SocketTimeoutException e) {
			Logger.error("HttpClient: " + e.getMessage() + "(" + query + ")");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) connection.disconnect();
		}
		return response;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР