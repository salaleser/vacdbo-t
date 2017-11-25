package ru.salaleser.vacdbot.vacdbo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.Config;

import java.sql.*;

public class ParserPlayerBans extends Parser {

	private boolean communitybanned;
	private boolean vacbanned;
	private long numberofvacbans;
	private long dayssincelastban;
	private long numberofgamebans;
	private String economyban;

	ParserPlayerBans() {
		table = "player_bans";
	}

	@Override
	public boolean parse(StringBuilder sb, String id) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray players = (JSONArray) jsonObject.get("players");
			if (players.isEmpty()) {
				System.out.println("SteamID не существуют");
				return true;
			}
			timeUpdated = System.currentTimeMillis() / 1000L;
			added = 0;
			updated = 0;

			for (Object p : players) {
				JSONObject player = (JSONObject) p;
				steamid = (String) player.get("SteamId");
				communitybanned = player.get("CommunityBanned") != null && (boolean) player.get("CommunityBanned");
				vacbanned = player.get("VACBanned") != null && (boolean) player.get("VACBanned");
				if (player.get("NumberOfVACBans") != null) numberofvacbans = (long) player.get("NumberOfVACBans");
				else numberofvacbans = -1;
				if (player.get("DaysSinceLastBan") != null) dayssincelastban = (long) player.get("DaysSinceLastBan");
				else dayssincelastban = -1;
				if (player.get("NumberOfGameBans") != null) numberofgamebans = (long) player.get("NumberOfGameBans");
				else numberofgamebans = -1;
				if (player.get("EconomyBan") != null) economyban = (String) player.get("EconomyBan");
				else economyban = null;

				if (isExists(null, null)) {
					if (update()) updated++;
				} else {
					if (insert()) added++;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Ошибка парсера, повторяю операцию...");
			return false;
		}
		System.out.println("Добавлено: " + added + " / Обновлено: " + updated);
		Config.addTotalUpdated(updated);
		Config.addTotalAdded(added);
		return true;
	}

	/**
	 * Вставляет новую строку в базу данных
	 *
	 * @return true if inserted
	 */
	@Override
	public boolean insert() {
		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, steamid);
			statement.setBoolean(2, communitybanned);
			statement.setBoolean(3, vacbanned);
			statement.setLong(4, numberofvacbans);
			statement.setLong(5, dayssincelastban);
			statement.setLong(6, numberofgamebans);
			statement.setString(7, economyban);
			statement.setLong(8, timeUpdated);

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
				System.err.println("Cannot close connection");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Обновляет строку в базе данных
	 *
	 * @return true if updated
	 */
	@Override
	public boolean update() {
		String sql = "UPDATE " + table + " SET " +
				"communitybanned = ?, " +
				"vacbanned = ?, " +
				"numberofvacbans = ?, " +
				"dayssincelastban = ?, " +
				"numberofgamebans = ?, " +
				"economyban = ?, " +
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

			statement.setBoolean(1, communitybanned);
			statement.setBoolean(2, vacbanned);
			statement.setLong(3, numberofvacbans);
			statement.setLong(4, dayssincelastban);
			statement.setLong(5, numberofgamebans);
			statement.setString(6, economyban);
			statement.setLong(7, timeUpdated);
			statement.setString(8, steamid);

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
				System.err.println("Cannot close connection");
				e.printStackTrace();
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР