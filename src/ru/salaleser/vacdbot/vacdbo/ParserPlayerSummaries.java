package ru.salaleser.vacdbot.vacdbo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.Config;

import java.sql.*;

public class ParserPlayerSummaries extends Parser {

	private long communityvisibilitystate;
	private long profilestate;
	private String personaname;
	private long lastlogoff;
	private long commentpermission;
	private String profileurl;
	private String avatar;
	private String avatarmedium;
	private String avatarfull;
	private long personastate;
	private String realname;
	private String primaryclanid;
	private long timecreated;
	private long personastateflags;
	private String loccountrycode;
	private String locstatecode;
	private long loccityid;

	ParserPlayerSummaries() {
		table = "player_summaries";
	}

	@Override
	public boolean parse(StringBuilder sb, String id) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONArray players = (JSONArray) response.get("players");
			if (players.isEmpty()) {
				System.out.println("SteamID не существуют");
				return true;
			}
			timeUpdated = System.currentTimeMillis() / 1000L;
			added = 0;
			updated = 0;

			for (Object p : players) {
				JSONObject player = (JSONObject) p;
				steamid = (String) player.get("steamid");
				long cvs;
				if (player.get("communityvisibilitystate") != null) cvs = (long) player.get("communityvisibilitystate");
				else cvs = -1;
				communityvisibilitystate = cvs;
				if (player.get("profilestate") != null) profilestate = (long) player.get("profilestate");
				else profilestate = -1;
				if (player.get("personaname") != null) personaname = (String) player.get("personaname");
				else personaname = null;
				if (player.get("lastlogoff") != null) lastlogoff = (long) player.get("lastlogoff");
				else lastlogoff = -1;
				if (player.get("commentpermission") != null) commentpermission = (long) player.get("commentpermission");
				else commentpermission = -1;
				if (player.get("profileurl") != null) profileurl = (String) player.get("profileurl");
				else profileurl = null;
				if (player.get("avatar") != null) avatar = (String) player.get("avatar");
				else avatar = null;
				if (player.get("avatarmedium") != null) avatarmedium = (String) player.get("avatarmedium");
				else avatarmedium = null;
				if (player.get("avatarfull") != null) avatarfull = (String) player.get("avatarfull");
				else avatarfull = null;
				if (player.get("personastate") != null) personastate = (long) player.get("personastate");
				else personastate = -1;
				if (player.get("realname") != null) realname = (String) player.get("realname");
				else realname = null;
				if (player.get("primaryclanid") != null) primaryclanid = (String) player.get("primaryclanid");
				else primaryclanid = null;
				if (player.get("timecreated") != null) timecreated = (long) player.get("timecreated");
				else timecreated = -1;
				if (player.get("personastateflags") != null) personastateflags = (long) player.get("personastateflags");
				else personastateflags = -1;
				if (player.get("loccountrycode") != null) loccountrycode = (String) player.get("loccountrycode");
				else loccountrycode = null;
				if (player.get("locstatecode") != null) locstatecode = (String) player.get("locstatecode");
				else locstatecode = null;
				if (player.get("loccityid") != null) loccityid = (long) player.get("loccityid");
				else loccityid = -1;

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

	@Override
	public boolean insert() {
		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, steamid);
			statement.setLong(2, communityvisibilitystate);
			statement.setLong(3, profilestate);
			statement.setString(4, personaname);
			statement.setLong(5, lastlogoff);
			statement.setLong(6, commentpermission);
			statement.setString(7, profileurl);
			statement.setString(8, avatar);
			statement.setString(9, avatarmedium);
			statement.setString(10, avatarfull);
			statement.setLong(11, personastate);
			statement.setString(12, realname);
			statement.setString(13, primaryclanid);
			statement.setLong(14, timecreated);
			statement.setLong(15, personastateflags);
			statement.setString(16, loccountrycode);
			statement.setString(17, locstatecode);
			statement.setLong(18, loccityid);
			statement.setLong(19, timeUpdated);

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

	@Override
	public boolean update() {
		String sql = "UPDATE " + table + " SET " +
				"communityvisibilitystate = ?, " +
				"profilestate = ?, " +
				"personaname = ?, " +
				"lastlogoff = ?, " +
				"commentpermission = ?, " +
				"profileurl = ?, " +
				"avatar = ?, " +
				"avatarmedium = ?, " +
				"avatarfull = ?, " +
				"personastate = ?, " +
				"realname = ?, " +
				"primaryclanid = ?, " +
				"timecreated = ?, " +
				"personastateflags = ?, " +
				"loccountrycode = ?, " +
				"locstatecode = ?, " +
				"loccityid = ?, " +
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

			statement.setLong(1, communityvisibilitystate);
			statement.setLong(2, profilestate);
			statement.setString(3, personaname);
			statement.setLong(4, lastlogoff);
			statement.setLong(5, commentpermission);
			statement.setString(6, profileurl);
			statement.setString(7, avatar);
			statement.setString(8, avatarmedium);
			statement.setString(9, avatarfull);
			statement.setLong(10, personastate);
			statement.setString(11, realname);
			statement.setString(12, primaryclanid);
			statement.setLong(13, timecreated);
			statement.setLong(14, personastateflags);
			statement.setString(15, loccountrycode);
			statement.setString(16, locstatecode);
			statement.setLong(17, loccityid);
			statement.setLong(18, timeUpdated);
			statement.setString(19, steamid);

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

	// FIXME: 18.11.2017 эта функция не нужна скорее всего, просто лежит
	public void createTable() {
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			String sql;

			statement = connection.createStatement();
			sql = "CREATE TABLE " + table + " (" +
					"steamid VARCHAR(17) PRIMARY KEY NULL," +
					"communityvisibilitystate INT8 NULL," +
					"profilestate INT8 NULL," +
					"personaname VARCHAR(64) NULL," +
					"lastlogoff INT8 NULL," +
					"commentpermission INT8 NULL," +
					"profileurl TEXT NULL," +
					"avatar TEXT NULL," +
					"avatarmedium TEXT NULL," +
					"avatarfull TEXT NULL," +
					"personastate INT8 NULL," +
					"realname VARCHAR(64) NULL," +
					"primaryclanid VARCHAR(48) NULL," +
					"timecreated INT8 NULL," +
					"personastateflags INT8 NULL," +
					"loccountrycode VARCHAR(4) NULL," +
					"locstatecode VARCHAR(4) NULL," +
					"loccityid INT8 NULL," +
					"timeupdated INT8 NULL" +
					")";

			statement.executeUpdate(sql);
			statement.close();
			connection.commit();
			System.out.println("Table " + table + " created successfully");
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Ошибка создания таблицы \"" + table + "\"");
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