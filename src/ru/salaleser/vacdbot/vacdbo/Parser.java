package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;

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

	/**
	 * Проверяет на существование id в базе данных
	 *
	 * @return false if SteamID not exists
	 */
	boolean isExists(String customTable, String customSteamId) {
		if (customTable == null) customTable = table;
		if (customSteamId == null) customSteamId = steamid;
		String sql = "SELECT * FROM " + customTable + " WHERE steamid = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());

			statement = connection.prepareStatement(sql);
			statement.setString(1, customSteamId);

			resultSet = statement.executeQuery();
			resultSet.next();
			return resultSet.getRow() > 0;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Ошибка при запросе в базу данных");
			return true; // TODO: 01.11.17 почему тру?
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				System.err.println("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	public abstract boolean insert();

	public abstract boolean update();
}