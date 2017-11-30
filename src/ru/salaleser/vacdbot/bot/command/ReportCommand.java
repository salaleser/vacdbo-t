package ru.salaleser.vacdbot.bot.command;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.vacdbo.ParserPlayerSummaries;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReportCommand extends Command {

	private String steamid;
	private String personaname;
	private String description;
	private int time;
	private String realname;
	private String avatarfull;

	private int days = 1;
	private IChannel channel;
	private IMessage resultMessage;

	public ReportCommand() {
		super("report", "" +
				Util.b("Описание:") + " Добавляет подозрительный профиль в базу данных и позволяет отслеживать" +
						"получение банов.\n" +
				Util.b("Использование:") + " `~report [<SteamID64> [<описание>]]`.\n" +
				Util.b("Предустановки:") + " `~report` — показывает список подозреваемых;\n" +
						"`~report check` — проверяет наличие банов у подозреваемых.\n" +
				Util.b("Пример:") + " `~report 76561198446059611 nuke подрубил на счёте 13-8 играли впятером`.\n" +
				Util.b("Примечание:") + " можно установить количество дней.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		days = Integer.parseInt(DBHelper.getValueFromSettings(name, "days"));
		channel = message.getChannel();

		if (args.length == 0) {
			showSuspects();
			return;
		}

		if (args[0].equals("check")) {
			checkSuspects();
			return;
		}

		if (!Util.isSteamID64(args[0])) {
			Logger.error("Ошибка в SteamID!");
			message.reply("ошибка в SteamID!");
			return;
		}

		steamid = args[0];
		ParserPlayerSummaries parserPlayerSummaries = new ParserPlayerSummaries();
		if (parserPlayerSummaries.isExists("suspects", steamid)) {
			message.reply("этот SteamID уже есть в базе данных!");
			Logger.error("SteamID уже есть в базе данных!");
			return;
		}
		personaname = null;
		description = null;
		if (args.length > 1) {
			//собираю описание из оставшихся аргументов:
			String[] descriptionArray = Arrays.copyOfRange(args, 1, args.length);
			StringBuilder descriptionBuilder = new StringBuilder();
			for (String word : descriptionArray) descriptionBuilder.append(" ").append(word);
			description = descriptionBuilder.substring(1);
		}
		time = (int) (System.currentTimeMillis() / 1000L);
		realname = null;
		avatarfull = null;

		HttpClient httpClient = new HttpClient();
		String url = Config.BASE_URL + "/ISteamUser/GetPlayerSummaries/v0002/?key=" +
				Config.getSteamWebApiKey() + "&steamids=" + steamid;
		StringBuilder jsonPlayerSummaries = httpClient.connect(url);

		if (jsonPlayerSummaries == null) {
			message.reply("Превышено время ожидания!");
			Logger.error("Превышено время ожидания!");
			return;
		}

		parseJson(jsonPlayerSummaries);

		if (insertSuspect()) {
			message.getChannel().sendMessage("Подозреваемый успешно добавлен в базу данных");
		}

	}

	private void showSuspects() {
		StringBuilder settingsBuilder = new StringBuilder();
		ArrayList<String> suspects = getSuspects();
		if (suspects.isEmpty()) settingsBuilder.append("База данных пуста!");
		for (String steamid : suspects) {
			settingsBuilder.append("\n").append("http://steamcommunity.com/profiles/").append(steamid);
		}
		channel.sendMessage(settingsBuilder.toString());
	}

	private ArrayList<String> getSuspects() {
		String sql = "SELECT steamid FROM suspects";
		ArrayList<String[]> settings = DBHelper.executeQuery(sql);
		ArrayList<String> suspects = new ArrayList<>();
		for (String[] row : settings) suspects.add(row[0]);
		return suspects;
	}

	public void checkSuspects() {
		resultMessage = channel.sendMessage(Util.i("Проверяю подозреваемых...") + "\n");
		StringBuilder steamidsBuilder = new StringBuilder();
		ArrayList<String> suspects = getSuspects();
		for (String steamid : suspects) steamidsBuilder.append(",").append(steamid);
		String steamids = steamidsBuilder.substring(1); //убирает первую запятую если чо

		resultMessage.edit(resultMessage.getContent() + "\n" +
				Util.i("Всего в списке подозреваемых: " + suspects.size() + " подозрительных профилей,..."));

		for (int i = 0; i < 10; i++) if (getBannedProfiles(steamids)) return;
		Logger.error("Проверьте подключение к интернету!");
	}

	private boolean getBannedProfiles(String steamids) {
		// TODO: 27.11.2017 на данный момент проверяется только первая сотня профилей, надо бы увеличить как-нибудь...
		HttpClient httpClient = new HttpClient();
		ParserFriendsBans parserBans = new ParserFriendsBans();
		StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" + steamids);
		if (jsonBans == null) {
			Logger.error("Ошибка HTTP-соединения при проверке подозреваемых! Повторяю операцию...");
			return false;
		}
		HashMap<String, Integer> cheaters = parserBans.parse(jsonBans);
		StringBuilder profilesBuilder = new StringBuilder();
		for (Map.Entry<String, Integer> cheater : cheaters.entrySet()) {
			if (cheater.getValue() < days) {
				if (profilesBuilder.length() == 0) {
					profilesBuilder.append("Профили читерков, получивших бан за последние ")
							.append(days).append(" д").append(Util.ending(days));
				}
				profilesBuilder.append("http://steamcommunity.com/profiles/").append(cheater.getKey()).append("\n");
			}
		}

		resultMessage.edit(" из них уже отлетело: " + cheaters.size() + " читерастов.");
		if (profilesBuilder.length() != 0) {
			resultMessage.edit(resultMessage.getContent() + profilesBuilder);
		} else {
			resultMessage.edit(resultMessage.getContent() +
					"За последние " + days + " д" + Util.ending(days) + " никто не спалился. Ждём дальше...");
		}
	return true;
	}

	// FIXME: 30.11.2017 такой же код в Parser!
	private void parseJson(StringBuilder json) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(json));
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
		}
	}

	private boolean insertSuspect() {
		String table = "suspects";
		String sql = "INSERT INTO " + table + " VALUES (?, ?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			Class.forName(Config.getDBDriver());
			connection = DriverManager.getConnection(Config.getDBUrl(),
					Config.getDBLogin(), Config.getDBPassword());
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, steamid);
			preparedStatement.setString(2, personaname);
			preparedStatement.setString(3, description);
			preparedStatement.setString(4, realname);
			preparedStatement.setString(5, avatarfull);
			preparedStatement.setInt(6, time);

			preparedStatement.executeUpdate();
			connection.commit();
			return true;
		} catch (SQLException | ClassNotFoundException e) {
			Logger.error("Ошибка добавления в БД!");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				Logger.error("Cannot close connection");
				e.printStackTrace();
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР