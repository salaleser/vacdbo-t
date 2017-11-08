package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

class MessageManager {

	private IMessage message;
	private IChannel channel;
	private IUser author;
	private final HttpClient httpClient = new HttpClient();

	void handleMessage(IMessage msg) {
		this.message = msg;
		this.author = msg.getAuthor();
		this.channel = msg.getChannel();

		ArrayList<StringBuilder> hundredsOfSteamIDs;
		ArrayList<String> cheaters;

		int days = 1;
		String steamid = getIdByName();

		String messageText = message.getContent().substring(1).toLowerCase();
		String args[] = messageText.split(" ");

		try {
			if (args.length == 0) {
				help("**Нет аргументов**");
			} else if (args[0].equals("vac")) {
				if (args.length > 1 && args[1].matches("\\d+")) {
					days = Integer.parseInt(args[1]);
				} else {
					help("**Ошибка в количестве дней**");
				}
				if (args.length > 2) {
					if (args[2].length() == 17 && args[2].matches("\\d+") && Long.parseLong(args[2]) > 76561197960265729L && Long.parseLong(args[2]) < 76561202255233023L) {
						steamid = args[2];
					} else {
						help("**Ошибка в профиле**");
					}
				}
				hundredsOfSteamIDs = handleFriends(steamid);
				if (hundredsOfSteamIDs == null) channel.sendMessage("Ошибка соединения!");
				channel.sendMessage("Всего друзей: " + hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1));
				cheaters = handleVacBanned(hundredsOfSteamIDs, days);
				StringBuilder bannedFriendsMessage = new StringBuilder();
				channel.sendMessage("Профили друзей, получивших VAC-бан за " + days + " д" + ending(days));
				if (!cheaters.isEmpty()) {
					for (String cheaterID : cheaters) {
						bannedFriendsMessage.append(cheaterID).append("\n");
					}
					channel.sendMessage(String.valueOf(bannedFriendsMessage));
				} else {
					channel.sendMessage("Забаненных друзей нет");
				}
			} else {
				help("**Неправильный синтаксис**");
			}
			/* УПОМИНАНИЯ: */
			if (message.getMentions().size() != 0) { // Содержит ли текущее сообщение упоминание
				for (int i = 0; i < message.getMentions().size(); i++) { // Перебираю упомянутых пользователей
					// УПОМИНАНИЕ БОТА
					if (message.getMentions().get(i).getName().equals("VACDBO-T")) {
						channel.sendMessage(author + ", я выявляю недавно получивших VAC-бан друзей" + "(команда \"~vac\"");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private ArrayList<StringBuilder> handleFriends(String steamid) {
		ParserPlayerFriends parserFriends = new ParserPlayerFriends();
		channel.sendMessage("Сканирую друзей профиля с SteamID: " + steamid + "...");
		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/" + "ISteamUser/GetFriendList/v0001/" + "?key=393819FBF50B3E63C1C6B60515A1AD0B" + "&steamid=" + steamid + "&relationship=friend");
		if (jsonFriends == null) return null;
		return parserFriends.parse(jsonFriends);
	}

	private ArrayList<String> handleVacBanned(ArrayList<StringBuilder> hundredsOfSteamIDs, int days) {
		ParserPlayerBans parserBans = new ParserPlayerBans();
		channel.sendMessage("Проверяю друзей на VAC-баны...");
		ArrayList<String> cheaters = new ArrayList<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" + "ISteamUser/GetPlayerBans/v1/" + "?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=" + hundredsOfSteamIDs.get(i));
			if (jsonBans == null) channel.sendMessage("Ошибка HTTP-соединения! Повторите операцию.");
			cheaters.addAll(parserBans.parse(jsonBans, days));
		}
		return cheaters;
	}

	private String ending(int days) {
		if (String.valueOf(days).endsWith("1")) return "ень";
		if (String.valueOf(days).endsWith("2") || String.valueOf(days).endsWith("3") || String.valueOf(days).endsWith("4"))
			return "ня";
		return "ней";
	}

	private void help(String error) {
		Main.channelLog.sendMessage(error);
		Main.channelLog.sendMessage("Использование: ```~vac <количество_дней> [<SteamID64>]```\n" + "Пример: ```~vac 5 76561198095972970```");
	}

	private String getIdByName() {
		switch (message.getAuthor().getName()) {
			case "salaleser":
				return "76561198095972970";
			case "volevju":
				return "76561198103577490";
			case "WayToHell":
				return "76561198041743174";
			case "pchelka":
				return "76561198187239091";
			case "KondraT":
				return "76561198038873933";
			case "Yanica":
				return "76561198245710318";
			default:
				return "76561198095972970";
		}
	}
}