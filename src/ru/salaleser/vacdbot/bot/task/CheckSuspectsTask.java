package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.Bot;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class CheckSuspectsTask extends TimerTask {

	public void run() {
		Bot.channelKTOGeneral.sendMessage(Util.i("Проверяю подозреваемых..."));
		String table = "suspects";
		String sql = "SELECT steamid FROM " + table;
		Connection connection = null;
		Statement statement = null;
		StringBuilder steamidsBuilder = new StringBuilder();
		String steamids = null;
		int suspectsNumber = 0;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				suspectsNumber++;
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

		// TODO: 27.11.2017 на данный момент проверяется только первая сотня профилей, надо бы увеличить как-нибудь...
		HttpClient httpClient = new HttpClient();
		HashMap<String, Integer> cheaters = new HashMap<>();
		ParserFriendsBans parserBans = new ParserFriendsBans();
		int days = 1;
		StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" + steamids);
		if (jsonBans == null) {
			Logger.error("Ошибка HTTP-соединения при проверке подозреваемых! Повторяю операцию...");
			run();
		} else {
			cheaters.putAll(parserBans.parse(jsonBans));
			int lastOnesNumber = 0;
			Bot.channelKTOGeneral.sendMessage(Util.i("Всего в списке подозреваемых: " + suspectsNumber +
					" подозрительных профилей, из них уже отлетело: " + cheaters.size() + " читерастов."));
			StringBuilder bMessage = new StringBuilder("Профили подозреваемых, получивших бан за последние " +
					days + " д" + Util.ending(days));
			for (Map.Entry<String, Integer> lastOne : cheaters.entrySet()) {
				if (lastOne.getValue() < days) {
					lastOnesNumber++;
					bMessage.append("http://steamcommunity.com/profiles/").append(lastOne.getKey()).append("\n");
				}
			}
			bMessage.append(" (").append(lastOnesNumber).append("читерков):\n");

			if (lastOnesNumber > 0) {
				Bot.channelKTOGeneral.sendMessage(String.valueOf(bMessage)); //fixme hardcode
			} else {
				Bot.channelKTOGeneral.sendMessage(Util.b("За последние " + days + " д" + Util.ending(days) +
						" пока никто не спалился. Ждём дальше...\n"));
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР