package ru.salaleser.vacdbot;

import java.sql.*;
import java.util.ArrayList;

public class DBHelper {

	private static String driver;
	private static String url;
	private static String login;
	private static String password;

	public DBHelper() {
		driver = Config.getDBDriver();
		url = Config.getDBUrl();
		login = Config.getDBLogin();
		password = Config.getDBPassword();
	}

	/**
	 * Обёртка для упрощения доступа к базе данных.
	 * Частный случай общего метода executeQuery()
	 *
	 * @return значение
	 */
	public static String getValueFromSettings(String name, String key) {
		String sql = "SELECT value FROM settings WHERE command = '" + name + "' AND key LIKE '" + key + "'";
		ArrayList<String[]> value = executeQuery(sql);
		return value.get(0)[0];
	}

	public static boolean isAlreadyExistsToday(String table, String column, String value, String date) {
		String sql = "SELECT " + column + " FROM " + table + " " +
				"WHERE " + column + " = '" + value + "' AND date = '" + date + "'";
		return !executeQuery(sql).isEmpty();
	}

	public static ArrayList<String[]> executeQuery(String sql) {
		Connection connection = null;
		Statement statement = null;
		ArrayList<String[]> resultSets = new ArrayList<>();
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			// TODO: 30.11.2017 почему бы не заменить на preparedstatement как в Parser.isExists?
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int columnCount = resultSet.getMetaData().getColumnCount();
				String[] row = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					row[i] = resultSet.getString(i + 1);
				}
				resultSets.add(row);
			}
		} catch (SQLException | ClassNotFoundException e) {
			Logger.error("Ошибка чтения из базы данных: " + e.getMessage());
		} finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
			}
		}
		return resultSets;
	}

	public static boolean setSettings(String name, String key, String value) {
		String sql = "UPDATE settings SET value = ? WHERE command = ? AND key = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, value);
			statement.setString(2, name);
			statement.setString(3, key);

			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Ошибка обновления БД.");
			return false;
		} finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	public static boolean insert(String table, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// FIXME: 30.11.2017 убрать хардкод
		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, arg1);
			statement.setString(2, arg2);
			statement.setString(3, arg3);
			statement.setString(4, arg4);
			statement.setString(5, arg5);

			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Ошибка чтения из базы данных: " + e.getMessage());
			return false;
		} finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	// TODO: 30.11.2017 есть повтор кода, и вообще бы объединить это с insert, просто в параметрах передавать оператор
	public static boolean delete(String sql) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Ошибка удаления из базы данных: " + e.getMessage());
			return false;
		} finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
				e.printStackTrace();
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР