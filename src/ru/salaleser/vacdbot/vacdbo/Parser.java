package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.Logger;

import java.sql.*;

public abstract class Parser {

	String table;
	String steamid;

	int added;
	int updated;
	long timeUpdated;

	private long time;

	void setTime() {
		time = System.currentTimeMillis();
	}

	long getElapsed() {
		return System.currentTimeMillis() - time;
	}

	/**
	 * Парсит json
	 *
	 * @param sb ответ от сервера
	 * @param id SteamID 64
	 * @return true если парсинг прошел успешно; false если произошла ошибка и надо
	 * повторить итерацию
	 */
	public abstract boolean parse(StringBuilder sb, String id);

	// FIXME: 30.11.2017 есть похожий код в DBHelper
	/**
	 * Проверяет на существование id в базе данных
	 *
	 * @return false if SteamID not exists
	 */
	public boolean isExists(String customTable, String customSteamId) {
		if (customTable == null) customTable = table;
		if (customSteamId == null) customSteamId = steamid;
		String sql = "SELECT * FROM " + customTable + " WHERE steamid = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());

			statement = connection.prepareStatement(sql);
			statement.setString(1, customSteamId);

			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			return resultSet.getRow() > 0;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Ошибка при запросе в базу данных");
			return true; // TODO: 01.11.17 почему тру?
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	public abstract boolean insert();

	public abstract boolean update();
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР