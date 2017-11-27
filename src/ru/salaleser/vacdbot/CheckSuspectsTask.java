package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;

import java.sql.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class CheckSuspectsTask extends TimerTask {

	public void run() {
		String table = "suspects";
		String sql = "SELECT steamid FROM " + table;
		Connection connection = null;
		Statement statement = null;
		StringBuilder steamidsBuilder = new StringBuilder();
		String steamids = null;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				steamidsBuilder.append(",").append(resultSet.getString("steamid"));
			}
			steamids = steamidsBuilder.substring(1);
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

		HttpClient httpClient = new HttpClient();
		ArrayList<String> cheaters = new ArrayList<>();
		ParserFriendsBans parserBans = new ParserFriendsBans();
		int days = 2;
		StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" + steamids);
		if (jsonBans == null) {
			Logger.error("Ошибка HTTP-соединения при проверке подозреваемых! Повторяю операцию...");
			run();
		} else {
			cheaters.addAll(parserBans.parse(jsonBans, days));

			StringBuilder bannedFriendsMessage = new StringBuilder("Профили подозреваемых, " +
					"получивших VAC-бан за " + days + " д" + Util.ending(days) + ":\n");
			if (!cheaters.isEmpty()) {
				for (String cheaterID : cheaters) {
					bannedFriendsMessage.append("http://steamcommunity.com/profiles/");
					bannedFriendsMessage.append(cheaterID).append("\n");
				}
				Bot.channelKTOGeneral.sendMessage(String.valueOf(bannedFriendsMessage)); //fixme hardcode
			} else {
				Bot.channelKTOGeneral.sendMessage(Util.b("Забаненных подозреваемых нет. Пока нет..."));
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР