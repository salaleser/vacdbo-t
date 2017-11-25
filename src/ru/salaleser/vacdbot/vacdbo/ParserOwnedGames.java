package ru.salaleser.vacdbot.vacdbo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.Config;

import java.sql.*;

public class ParserOwnedGames extends Parser {

	private long game_count;
	private long playtime_2weeks;
	private long playtime_forever;

	ParserOwnedGames() {
		table = "owned_games";
	}

	@Override
	public boolean parse(StringBuilder sb, String id) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONObject response = (JSONObject) jsonObject.get("response");
			if (response.isEmpty()) {
				System.out.println("Пользователь не настроил свой профиль");
				return true;
			}
			JSONArray games = (JSONArray) response.get("games");
			timeUpdated = System.currentTimeMillis() / 1000L;

			steamid = id;
			game_count = (long) response.get("game_count");

			if (game_count == 0) {
				System.out.println("У пользователя нет игр");
				return true;
			} else {
				for (Object g : games) {
					JSONObject game = (JSONObject) g;
					if ((long) game.get("appid") == Games.CS_GO) {
						if (game.get("playtime_2weeks") != null) playtime_2weeks = (long) game.get("playtime_2weeks");
						else playtime_2weeks = 0;
						playtime_forever = (long) game.get("playtime_forever");
						if (isExists(null, null)) {
							if (update()) {
								System.out.println("Обновлён. Пользователь наиграл " + playtime_forever / 60 + " часов");
								Config.addTotalUpdated(1);
							} else {
								return false;
							}
						} else {
							if (insert()) {
								System.out.println("Добавлен. Пользователь наиграл " + playtime_forever / 60 + " часов");
								Config.addTotalAdded(1);
							} else {
								return false;
							}
						}
					} else {
						System.out.println("Пользователь не имеет CS:GO");
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Ошибка парсера, повторяю операцию...");
			return false;
		}
		return true;
	}

	@Override
	public boolean insert() {
		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, steamid);
			statement.setLong(2, game_count);
			statement.setLong(3, playtime_2weeks);
			statement.setLong(4, playtime_forever);
			statement.setLong(5, timeUpdated);

			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Ошибка добавления в БД, повторяю операцию...");
			return false;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				System.out.println("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean update() {
		String sql = "UPDATE " + table + " SET " +
				"game_count = ?, " +
				"playtime_2weeks = ?, " +
				"playtime_forever = ?, " +
				"timeupdated = ? " +
				"WHERE steamid = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setLong(1, game_count);
			statement.setLong(2, playtime_2weeks);
			statement.setLong(3, playtime_forever);
			statement.setLong(4, timeUpdated);
			statement.setString(5, steamid);

			statement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Ошибка обновления БД, повторяю операцию...");
			return false;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				System.out.println("Cannot close connection");
				e.printStackTrace();
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР