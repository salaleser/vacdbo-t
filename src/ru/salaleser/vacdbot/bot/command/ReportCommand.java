package ru.salaleser.vacdbot.bot.command;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.HttpClient;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.vacdbo.ParserPlayerSummaries;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.*;
import java.util.Arrays;

public class ReportCommand extends Command {

	private String table = "suspects";

	public ReportCommand() {
		super("report", "обавляет в базу данных подозрительного игрока для отслеживания его профиля" +
				"Использование: ```~report <SteamID64> [<описание>]```\n" +
				"Пример: ```~report 76561198095972970 играли на ньюке впятером на даст2```");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			message.getChannel().sendMessage(Util.i("Проверяю подозреваемых..."));
			String sql = "SELECT steamid FROM " + table;
			Connection connection = null;
			Statement statement = null;
			StringBuilder suspects = new StringBuilder();
			try {
				Class.forName(Config.getDBDriver());
				connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBLogin(), Config.getDBPassword());
				connection.setAutoCommit(false);
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					suspects.append("http://steamcommunity.com/profiles/");
					suspects.append(resultSet.getString("steamid"));
					suspects.append("\n");
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
			if (suspects.length() == 0) message.reply("база данных пуста!");
			else message.getChannel().sendMessage(suspects.toString());
			return;
		}

		String steamid;
		if (Util.isSteamID64(args[0])) {
			steamid = args[0];
		} else {
			Logger.error("Ошибка в SteamID!");
			message.reply("ошибка в SteamID!");
			return;
		}
		ParserPlayerSummaries parserPlayerSummaries = new ParserPlayerSummaries();
		if (parserPlayerSummaries.isExists("suspects", steamid)) {
			message.reply("этот SteamID уже есть в базе данных!");
			Logger.error("SteamID уже есть в базе данных!");
			return;
		}
		String personaname = null;
		String description = null;
		if (args.length > 1) {
			//собираю описание из оставшихся аргументов:
			String[] descriptionArray = Arrays.copyOfRange(args, 1, args.length);
			StringBuilder descriptionBuilder = new StringBuilder();
			for (String word : descriptionArray) descriptionBuilder.append(" ").append(word);
			description = descriptionBuilder.substring(1);
		}
		long time = System.currentTimeMillis() / 1000L;
		String realname = null;
		String avatarfull = null;

		HttpClient httpClient = new HttpClient();
		String url = Config.BASE_URL + "/ISteamUser/GetPlayerSummaries/v0002/?key=" +
				Config.getSteamWebApiKey() + "&steamids=" + steamid;
		StringBuilder jsonPlayerSummaries = httpClient.connect(url);
		if (jsonPlayerSummaries == null) {
			message.reply("Превышено время ожидания!");
			Logger.error("Превышено время ожидания!");
			return;
		}

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(jsonPlayerSummaries));
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONArray players = (JSONArray) response.get("players");
			if (players.isEmpty()) {
				Logger.error("SteamID не существует!");
				return;
			}
			for (Object p : players) {
				JSONObject player = (JSONObject) p;
				if (player.get("personaname") != null) personaname = (String) player.get("personaname");
				else personaname = null;
				if (player.get("avatarfull") != null) avatarfull = (String) player.get("avatarfull");
				else avatarfull = null;
				if (player.get("realname") != null) realname = (String) player.get("realname");
				else realname = null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			Logger.error("Ошибка парсера!");
			return;
		}

		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);

			statement.setString(1, steamid);
			statement.setString(2, personaname);
			statement.setString(3, description);
			statement.setString(4, realname);
			statement.setString(5, avatarfull);
			statement.setLong(6, time);

			statement.executeUpdate();
			connection.commit();

			message.getChannel().sendMessage("Подозреваемый успешно добавлен в базу данных");
		} catch (SQLException | ClassNotFoundException e) {
			Logger.error("Ошибка добавления в БД!");
			e.printStackTrace();
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
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР