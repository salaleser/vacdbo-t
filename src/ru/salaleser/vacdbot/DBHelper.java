package ru.salaleser.vacdbot;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper {

	private static String driver = Config.getDBDriver();
	private static String url = Config.getDBUrl();
	private static String login = Config.getDBLogin();
	private static String password = Config.getDBPassword();

	/**
	 * Обёртка для упрощения доступа к базе данных.
	 * Частный случай общего метода executeQuery()
	 *
	 * @return значение
	 */
	public static String getValueFromSettings(String name, String key) {
		String sql = "SELECT value FROM settings WHERE command = '" + name + "' AND key LIKE '" + key + "'";
		String[][] value = executeQuery(sql);
		return value[0][0];
	}

	public static boolean isAlreadyExistToday(String table, String column, String value, String date) {
		String sql = "SELECT " + column + " FROM " + table + " " +
				"WHERE " + column + " = '" + value + "' AND date = '" + date + "'";
		return executeQuery(sql).length != 0;
	}

	public static String[][] executeQuery(String sql) {
		Connection connection = null;
		Statement statement = null;
		ArrayList<String[]> resultSets = new ArrayList<>();
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			//TODO: 30.11.2017 почему бы не заменить на preparedstatement как в Parser.isExists?
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
		if (resultSets.isEmpty()) return new String[0][0];
		//ужасный отстой... этот дикий каст из листа массивов в двумерный массив пришлось сделать потому, что
		//я не знаю как объявить массив не зная заранее его размер, поэтому я вынужден создать сначала лист fixme
		String[][] resultArray = new String[resultSets.size()][resultSets.get(0).length];
		for (int i = 0; i < resultSets.size(); i++) {
			System.arraycopy(resultSets.get(i), 0, resultArray[i], 0, resultSets.get(i).length);
		}
		return resultArray;
	}

	/**
	 * Вставляет новую строку в базу данных.
	 * Первым элементом массива должен быть ключ
	 *
	 * @param table таблица
	 * @param args массив столбцов таблицы
	 * @return true если операция завершена успешно
	 */
	public static boolean insert(String table, String[] args) {
		String sql = "INSERT INTO " + table + " VALUES (" + Util.getQMarks(args.length) + ")";
		return commit(table, sql, args);
	}

	/**
	 * Обновляет строку в базе данных.
	 * Первый элемент должен быть последним, поэтому начинаю со второго, а первый элемент массива вставляю последним
	 *
	 * @param table таблица
	 * @param args массив столбцов таблицы
	 * @return true если операция завершена успешно
	 */
	public static boolean update(String table, String[] args) {
		String[][] colNames = Util.getColNames(table, new String[]{"*"});
		StringBuilder sqlBuilder = new StringBuilder("UPDATE " + table + " SET ");
		for (int i = 1; i < colNames.length; i++) sqlBuilder.append(colNames[i][0]).append(" = ?, ");
		sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length() - 1);
		sqlBuilder.append("WHERE ").append(colNames[0][0]).append(" = ? ");
		//первый элемент вставляю последним:
		//только так умею, листом все меняю местами:
		ArrayList<String> newArgsList = new ArrayList<>();
		newArgsList.addAll(Arrays.asList(args).subList(1, args.length));
		newArgsList.add(args[0]);
		//...и кастую обратно в массив:
		String[] newArgs = newArgsList.toArray(new String[newArgsList.size()]);
		return commit(table, sqlBuilder.toString(), newArgs);
	}

	public static boolean commit(String table, String sql, String[] args) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, login, password);
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);
			//если args[]=null значит операция DELETE, иначе — INSERT или UPDATE:
			if (args != null) {
				//выясняю тип данных в таблице:
				String getDataTypesQuery = "SELECT data_type FROM information_schema.columns" +
						" WHERE information_schema.columns.table_name = '" + table + "'";
				String[][] dataTypes = DBHelper.executeQuery(getDataTypesQuery);
				ArrayList<String> dataTypesList = new ArrayList<>();
				for (String[] dataType : dataTypes) dataTypesList.add(dataType[0]);
				if (sql.startsWith("UPDATE")) {
					dataTypesList.add(dataTypesList.get(0));
					dataTypesList.remove(0);
				}
				//кастую в тип данных из таблицы:
				for (int i = 0; i < args.length; i++) {
					switch (dataTypesList.get(i)) {
						case "boolean":
							statement.setBoolean(i + 1, Boolean.valueOf(args[i]));
							break;
						case "integer":
							statement.setInt(i + 1, Integer.valueOf(args[i]));
							break;
						case "character varying":
						case "character":
						case "text":
							statement.setString(i + 1, args[i]);
							break;
					}
				}
			}
			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Ошибка операции с базой данных: " + e.getMessage());
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