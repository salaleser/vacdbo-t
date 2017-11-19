package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.HttpClient;
import ru.salaleser.vacdbot.ParserFriends;
import ru.salaleser.vacdbot.ParserFriendsBans;
import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;

public class VacCommand extends Command {

	private final HttpClient httpClient = new HttpClient();

	public VacCommand() {
		super("vac", "**Описание:** Показывает забаненных друзей пользователя за указанный период времени.\n" +
				"**Использование:** `~vac [<количество_дней> [<SteamID64>]]`.\n" +
				"**Предустановки:** `~vac` — ваши друзья, получившие бан за прошедший день;\n" +
				"`~vac <количество_дней>` — ваши друзья, получившие бан за указанное количество дней;\n" +
				"**Пример:** `~vac 3 76561198095972970`.\n" +
				"**Примечание:** день может быть от 1 до 9999.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		//defaults:
		String steamid = Utilities.getSteamidByDiscordUser(message.getAuthor());
		int days = 1;

		if (args.length > 0) {
			if (Utilities.isNumeric(args[0])) {
				days = Integer.parseInt(args[0]);
				if (args.length > 1) {
					if (Utilities.isSteamID64(args[1])) {
						steamid = args[1];
					} else {
						message.reply("**ошибка в профиле** (проверяю баны друзей salaleser'а)");
					}
				} else {
					message.reply("**профиль не задан** (проверяю баны друзей salaleser'а)");
				}
			} else {
				message.reply("**ошибка в количестве дней** (проверяю баны за сегодня)");
			}
		} else {
			message.reply("**аргументы не заданы** (проверяю баны друзей salaleser'а за сегодня)");
		}

		String name = Utilities.getDiscordDisplayNameBySteamid(steamid, message.getGuild());
		message.getChannel().sendMessage("Проверяю на вшивость друзей " + name + "'а...");
		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetFriendList/v0001/" + "?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=" +
				steamid + "&relationship=friend");
		if (jsonFriends == null) {
			message.getChannel().sendMessage("*Время ожидания вышло! Повторите запрос...*");
			return;
		}
		ParserFriends parserFriends = new ParserFriends();
		ArrayList<StringBuilder> hundredsOfSteamIDs = parserFriends.parse(jsonFriends);

		message.getChannel().sendMessage("Всего друзей: __" +
				hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1) + "__\n" + "Проверяю друзей на VAC-баны...");
		ParserFriendsBans parserBans = new ParserFriendsBans();
		ArrayList<String> cheaters = new ArrayList<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
					"ISteamUser/GetPlayerBans/v1/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=" +
					hundredsOfSteamIDs.get(i));
			if (jsonBans == null) {
				message.getChannel().sendMessage("*Ошибка HTTP-соединения на *" + i +
						"*-ой итерации! Повторяю запрос...*");
				i--;
			} else {
				cheaters.addAll(parserBans.parse(jsonBans, days));
			}
		}
		StringBuilder bannedFriendsMessage = new StringBuilder();
		message.getChannel().sendMessage("Профили друзей, получивших VAC-бан за " + days +
				" д" + Utilities.ending(days) + ":");
		if (!cheaters.isEmpty()) {
			for (String cheaterID : cheaters) {
				bannedFriendsMessage.append("");
				bannedFriendsMessage.append("http://steamcommunity.com/profiles/");
				bannedFriendsMessage.append(cheaterID).append("\n");
			}
			message.getChannel().sendMessage(String.valueOf(bannedFriendsMessage));
		} else {
			message.getChannel().sendMessage("Забаненных друзей нет. Пока нет...");
		}
	}
}
