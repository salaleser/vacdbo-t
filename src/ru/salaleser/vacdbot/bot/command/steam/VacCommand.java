package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VacCommand extends Command {

	private final HttpClient httpClient = new HttpClient();

	public VacCommand() {
		super("vac", STEAM, "Показывает забаненных друзей пользователя за указанный период времени.", new String[]{"вак"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~vac [<количество_дней> [<SteamID64> | <пользователь_Discord> | <ссылка_на_профиль_Steam>]]`.",
				"`~vac` — ваши друзья, получившие бан за прошедший день;\n" +
						"`~vac <количество_дней>` — ваши друзья, получившие бан за указанное количество дней.",
				"`~vac 3 76561198095972970`, `~vac 30 @salaleser`.",
				"день может быть от 1 до 9999."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		//defaults:
		IChannel channel = message.getChannel();
		IUser user = message.getAuthor();
		String discordid = user.getStringID();
		String steamid = Util.getSteamidByDiscordid(user.getStringID());
		int days = 1;

		String daysString = DBHelper.getOption(guild.getStringID(), "vac", "days");
		if (Util.isNumeric(daysString)) days = Integer.parseInt(daysString);

		for (String arg : args) {
			if (Util.isNumeric(arg)) {
				days = Integer.parseInt(arg);
				continue;
			}
			if (Util.isCommunityID(arg)) arg = Util.getSteamidByCommunityid(arg);
			if (Util.isSteamID64(arg)) {
				steamid = arg;
				discordid = Util.getDiscordidBySteamid(steamid);
			} else if (Util.isDiscordUser(arg)) {
				discordid = arg.replaceAll("[<@!>]", "");
				steamid = Util.getSteamidByDiscordid(discordid);
			}
			user = guild.getUserByID(Long.parseLong(discordid));
		}

		channel.sendMessage("Проверяю друзей " + user.getName() + "…");

		if (Util.getSteamidByDiscordid(discordid) == null) {
			channel.sendMessage(Util.i("С пользователем " + user.getName() +
					" не ассоциирован SteamID, взаимодействие со Steam API невозможно."));
			return;
		}

		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/ISteamUser/GetFriendList/" +
				"v0001/?key=" + Config.getSteamWebApiKey() + "&steamid=" + steamid + "&relationship=friend");
		if (jsonFriends == null) {
			Logger.error("Время ожидания вышло! Повторите запрос...", guild);
			return;
		}

		ParserFriends parserFriends = new ParserFriends();
		ArrayList<StringBuilder> hundredsOfSteamIDs = parserFriends.parse(jsonFriends);

		channel.sendMessage("Всего друзей: " +
				Util.b(hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1).toString()) + "\nПроверяю друзей на баны…");
		ParserFriendsBans parserBans = new ParserFriendsBans();
		HashMap<String, Integer> cheaters = new HashMap<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
					"ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" +
					hundredsOfSteamIDs.get(i));
			if (jsonBans == null) {
				channel.sendMessage(Util.i("Ошибка HTTP-соединения на " + (i + 1) +
						"-ой итерации! Повторяю запрос..."));
				i--;
			} else {
				cheaters = parserBans.parse(jsonBans);
			}
		}

		int lastOnesNumber = 0;
		StringBuilder bMessage = new StringBuilder("Профили друзей " + user.getName() + ", получивших бан за последние " +
				days + " д" + Util.ending(days));
		for (Map.Entry<String, Integer> lastOne : cheaters.entrySet()) {
			if (lastOne.getValue() < days) {
				lastOnesNumber++;
				bMessage.append("\n").append("http://steamcommunity.com/profiles/").append(lastOne.getKey());
			}
		}

		if (lastOnesNumber > 0) {
			channel.sendMessage(String.valueOf(bMessage)); //fixme hardcode повтор кода
		} else {
			channel.sendMessage(Util.b("Забаненных друзей " + user.getName() + " за последние " + days + " д" + Util.ending(days) + " нет. Пока нет..."));
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР