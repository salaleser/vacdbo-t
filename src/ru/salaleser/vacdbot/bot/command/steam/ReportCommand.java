package ru.salaleser.vacdbot.bot.command.steam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.HttpClient;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.ParserFriendsBans;
import ru.salaleser.vacdbot.bot.command.Command;
import ru.salaleser.vacdbot.vacdbo.ParserPlayerSummaries;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ru.salaleser.vacdbot.Config.BASE_URL;
import static ru.salaleser.vacdbot.Config.getSteamWebApiKey;
import static ru.salaleser.vacdbot.Util.*;

public class ReportCommand extends Command {

	private int days = 1;
	private IChannel channel;
	private StringBuilder resultBuilder;

	public ReportCommand() {
		super("report", STEAM, "Добавляет подозрительный профиль в базу данных и позволяет отслеживать получение банов.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~report [<SteamID64> [<описание>]]`.",
				"`~report` — проверяет наличие банов у подозреваемых.",
				"`~report 76561198446059611 nuke подрубил на счёте 13-8 играли впятером`.",
				"можно установить количество дней."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		this.guild = guild;
		days = Integer.parseInt(DBHelper.getOption(guild.getStringID(), name, "days"));
		channel = message.getChannel();
		resultBuilder = new StringBuilder();

		if (args.length == 0) {
			checkSuspects();
			return;
		}

		String steamid = args[0];
		if (isCommunityURL(steamid)) steamid = getSteamID64ByCommunityURL(steamid);
		if (!isSteamID64(steamid)) {
			message.reply("ошибка в SteamID!");
			return;
		}

		ParserPlayerSummaries parserPlayerSummaries = new ParserPlayerSummaries();
		if (parserPlayerSummaries.isExists("suspects", steamid)) {
			message.reply("этот SteamID уже есть в базе данных!");
			return;
		}
		String description = null;
		if (args.length > 1) {
			//собираю описание из оставшихся аргументов:
			String[] descriptionArray = Arrays.copyOfRange(args, 1, args.length);
			StringBuilder descriptionBuilder = new StringBuilder();
			for (String word : descriptionArray) descriptionBuilder.append(" ").append(word);
			description = descriptionBuilder.substring(1);
		}
		int time = (int) (System.currentTimeMillis() / 1000L);

		HttpClient httpClient = new HttpClient();
		String url = BASE_URL + "/ISteamUser/GetPlayerSummaries/v0002/?key=" +
				getSteamWebApiKey() + "&steamids=" + steamid;
		StringBuilder jsonPlayerSummaries = httpClient.connect(url);

		if (jsonPlayerSummaries == null) {
			message.reply("Превышено время ожидания!");
			return;
		}

		String[] playerSummaries = parseJson(jsonPlayerSummaries);

		String[] columns = new String[] {
				steamid,
				playerSummaries[0],
				description,
				playerSummaries[1],
				playerSummaries[2],
				String.valueOf(time)
		};
		if (DBHelper.insert("suspects", columns)) {
			message.getChannel().sendMessage("Подозреваемый успешно добавлен в базу данных");
		}
	}

	private void checkSuspects() {
		channel.sendMessage(i("Проверяю подозреваемых...") + "\n");
		StringBuilder steamidsBuilder = new StringBuilder();
		String[][] suspects = DBHelper.executeQuery("SELECT steamid FROM suspects");
		//перечисляю steamid через запятую:
		for (String[] row : suspects) steamidsBuilder.append(",").append(row[0]);
		String steamids = steamidsBuilder.substring(1);

		resultBuilder.append("Всего в списке подозреваемых ").append(suspects.length).append(" подозрительных профилей,");

		//если вернется false более 10 раз, то считаю, что есть проблемы с подключением к интернету:
		for (int i = 0; i < 10; i++) if (getBannedProfiles(steamids)) return;
		Logger.error("Проверьте подключение к интернету!", guild);
	}

	private boolean getBannedProfiles(String steamids) {
		// TODO: 27.11.2017 на данный момент проверяется только первая сотня профилей, надо бы увеличить как-нибудь...
		HttpClient httpClient = new HttpClient();
		ParserFriendsBans parserBans = new ParserFriendsBans();
		StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetPlayerBans/v1/?key=" + getSteamWebApiKey() + "&steamids=" + steamids);
		if (jsonBans == null) {
			Logger.error("Ошибка HTTP-соединения при проверке подозреваемых! Повторяю операцию...", guild);
			return false;
		}
		HashMap<String, Integer> cheaters = parserBans.parse(jsonBans);
		StringBuilder profilesBuilder = new StringBuilder();
		for (Map.Entry<String, Integer> cheater : cheaters.entrySet()) {
			if (cheater.getValue() < days) {
				if (profilesBuilder.length() == 0) {
					profilesBuilder.append("*Профили читерков, получивших бан за последние ")
							.append(days).append(getEnding("день", days)).append(":*").append("\n");
				}
				profilesBuilder.append("http://steamcommunity.com/profiles/").append(cheater.getKey()).append("\n");
			}
		}

		resultBuilder.append(" из них уже отлетело ").append(cheaters.size()).append(" читерастов.");
		if (profilesBuilder.length() == 0) {
			resultBuilder.append(" За последние ").append(days).append(getEnding("день", days))
					.append(" никто не спалился. Ждём дальше...");
		}
		channel.sendMessage(i(resultBuilder.toString()) + "\n" + profilesBuilder.toString());
		return true;
	}

	// FIXME: 30.11.2017 такой же код в Parser!
	private String[] parseJson(StringBuilder json) {
		String personaname = null;
		String realname = null;
		String avatarfull = null;
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(json));
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONArray players = (JSONArray) response.get("players");
			if (players.isEmpty()) {
				Logger.error("SteamID не существует!", guild);
				return null;
			}
			for (Object p : players) {
				JSONObject player = (JSONObject) p;
				if (player.get("personaname") != null) personaname = (String) player.get("personaname");
				else personaname = null;
				if (player.get("realname") != null) realname = (String) player.get("realname");
				else realname = null;
				if (player.get("avatarfull") != null) avatarfull = (String) player.get("avatarfull");
				else avatarfull = null;
			}
			return new String[] {personaname, realname, avatarfull};
		} catch (ParseException e) {
			e.printStackTrace();
			Logger.error("Ошибка парсера!", guild);
			return null;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР