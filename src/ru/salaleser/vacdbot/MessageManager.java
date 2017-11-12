package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.util.ArrayList;

class MessageManager {

	private IMessage message;
	private IChannel channel;
	private IUser author;
	private IGuild guild;
	private final HttpClient httpClient = new HttpClient();
	private final HtmlParser htmlParser = new HtmlParser();

	private int days = 1;

	void handleMessage(IMessage msg) {
		this.message = msg;
		this.author = msg.getAuthor();
		this.guild = msg.getGuild();
		this.channel = msg.getChannel();

		String steamid = getIdByName(author);
		String messageText = message.getContent().substring(1).toLowerCase();
		String args[] = messageText.split(" ");
		if (args.length == 0) {
			help("**нет команды**");
		} else {
			String command = args[0];
			switch (command) {
				case "vac":
				case "вак":
					switch (args.length) {
						case 3:
							if (Utilities.isSteamID64(args[2])) steamid = args[2];
							else help("**ошибка в SteamID64** (использую профиль salaleser");
						case 2:
							if (Utilities.isNumeric(args[1])) days = Integer.parseInt(args[1]);
							else help("**ошибка в количестве дней** (использую 1 день");
						case 1:
							handleHelp(command);
							handleCommandVac(steamid);
							break;
						default:
							help("**количество аргументов превышает допустимое, бич**");
					}
					break;
				case "random":
				case "rnd":
				case "рандом":
					int min = 1;
					int max = 6;
					switch (args.length) {
						case 3:
							if (Utilities.isNumeric(args[2]) && Integer.parseInt(args[2]) < Integer.parseInt(args[1])) {
								min = Integer.parseInt(args[2]);
							} else {
								help("**ошибка во втором аргументе** (использую значение " + min + ")");
							}
						case 2:
							if (Utilities.isNumeric(args[1])) max = Integer.parseInt(args[1]);
							else help("**ошибка в первом аргументе** (использую значение " + max + ")");
						case 1:
							int range = (max - min) + 1;
							int random = (int) (Math.random() * range) + min;
							channel.sendMessage("Случайное число от " + min +
									" до " + max + ": `" + random + "`");
							break;
						default:
							help("**количество аргументов превышает допустимое, бич**");
					}
					break;
				case "server":
				case "сервер":
				case "serv":
				case "серв":
					channel.sendMessage("Тренировочный сервер: " +
							"steam://connect/m13.megakill.ru:28697//\n" +
							"```connect 46.243.253.210:28697; password 2002```" +
							"```connect 46.243.253.210:28697; password 2002; rcon_password ```");
					break;
				case "console":
				case "bind":
					channel.sendMessage("Получить бронежилет, каску, гранаты и AK-47 на **K**: " +
							"```bind k \"give weapon_ak47; give weapon_hegrenade; give weapon_flashbang;" +
							" give weapon_smokegrenade; give weapon_incgrenade; give item_assaultsuit\"```");
					channel.sendMessage("Для тренировки: " +
							"```rcon sv_cheats 1; rcon sv_infinite_ammo 2; rcon sv_showimpacts 1;" +
							" rcon ammo_grenade_limit_total 4; rcon mp_spectators_max 10; rcon mp_warmuptime 5400;" +
							" rcon mp_buy_anywhere 1; rcon mp_warmup_start; rcon sv_full_alltalk 1; rcon bot_kick```");
					channel.sendMessage("Показ местоположения бомбы: " +
							"```alias +bombfind \"+use; gameinstructor_enable 1; cl_clearhinthistory\"\n" +
							"alias -bombfind \"-use; gameinstructor_enable 0; cl_clearhinthistory\"\n" +
							"bind e +bombfind```");
					break;
				case "report":
					JsonWriter jsonWriter = new JsonWriter();
					StringBuilder description = new StringBuilder();
					if (args.length > 1) {
						if (!Utilities.isSteamID64(args[1])) {
							help("**ошибка в профиле**");
							break;
						} else {
							if (args[1].equals("remove")) {
								channel.sendMessage("Пока не умею.");
								if (Utilities.isSteamID64(args[2])) {
									jsonWriter.remove(args[2]);
								}
							}
							if (args[1].equals("undo")) {
								channel.sendMessage("Пока не умею. " +
										"Можно попробовать удалить: `~report remove <SteamID64>`");
								jsonWriter.undo();
							}
							for (int i = 2; i < args.length; i++) {
								description.append(args[i]).append(" ");
							}
							try {
								jsonWriter.addSuspect(args[1], description.toString());
								channel.sendMessage("http://steamcommunity.com/profiles/" + args[1] +
										"\nс описанием: ```" + description + "```" +
										"\nуспешно добавлен в список подозреваемых. " +
										"Для отмены наберите `~report undo`");
							} catch (IOException e) {
								e.printStackTrace();
								channel.sendMessage("Ошибка в добавлении подозреваемого!");
								Bot.log.sendMessage(e.getMessage());
							}
						}
					} else {
						for (String text : jsonWriter.getSuspects()) {
							channel.sendMessage(text);
						}
					}
					break;
				case "ready":
				case "готов":
					channel.sendMessage(guild.getRolesByName("КТО-С").get(0) +
							", " + guild.getRolesByName("КТО-О").get(0) +
							", " + guild.getRolesByName("КТО-Ж").get(0) +
							", " + guild.getRolesByName("КТО-З").get(0) +
							", " + guild.getRolesByName("КТО-Ф").get(0) +
							"!\n" + author + " готов играть в CS:GO");
					break;
				case "user":
				case "треня":
					help("**команда пока не поддерживается** *(в разработке)*");
					break;
				default:
					help("**неизвестная команда**");
			}
		}

			/* УПОМИНАНИЯ: */
		if (message.getMentions().size() != 0) { // Содержит ли текущее сообщение упоминание
			for (int i = 0; i < message.getMentions().size(); i++) { // Перебираю упомянутых пользователей
				// УПОМИНАНИЕ БОТА
				if (message.getMentions().get(i).getName().equals("VACDBO-T")) {
					channel.sendMessage(author + ", я выявляю недавно получивших VAC-бан друзей" +
							"(команда \"~vac\"");
				}
			}
		}
	}

	private ArrayList<StringBuilder> handleFriends(String steamid) {
		ParserPlayerFriends parserFriends = new ParserPlayerFriends();
		String name = getNameById(steamid);
		channel.sendMessage("Сканирую друзей " + name + "...");
		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetFriendList/v0001/" +
				"?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=" + steamid + "&relationship=friend");
		if (jsonFriends == null) return null;
		return parserFriends.parse(jsonFriends);
	}

	private ArrayList<String> handleVacBanned(ArrayList<StringBuilder> hundredsOfSteamIDs, int days) {
		ParserPlayerBans parserBans = new ParserPlayerBans();
		channel.sendMessage("Проверяю друзей на VAC-баны...");
		ArrayList<String> cheaters = new ArrayList<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
					"ISteamUser/GetPlayerBans/v1/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=" +
					hundredsOfSteamIDs.get(i));
			if (jsonBans == null) {
				channel.sendMessage("Ошибка HTTP-соединения на " + i +
						" итерации! Повторяю операцию...");
				i--;
			} else {
				cheaters.addAll(parserBans.parse(jsonBans, days));
			}
		}
		return cheaters;
	}

	private String ending(int days) {
		if (String.valueOf(days).endsWith("1") && !String.valueOf(days).endsWith("11")) return "ень";
		if (String.valueOf(days).endsWith("2") ||
				String.valueOf(days).endsWith("3") ||
				String.valueOf(days).endsWith("4"))
			return "ня";
		return "ней";
	}

	private void help(String error) {

	}

	private String getIdByName(IUser author) {
		switch (author.getStringID()) {
			case "223559816239513601":
				return "76561198095972970";
			case "224234891762597889":
				return "76561198103577490";
			case "224129884727803904":
				return "76561198041743174";
			case "223807770552565762":
				return "76561198187239091";
			case "277876854134931457":
				return "76561198038873933";
			case "278897176271126528":
				return "76561198245710318";
			default:
				return "76561198095972970";
		}
	}

	private String getNameById(String steamID64) {
		// FIXME: 09.11.17 убрать хардкод дискорд айди
		switch (steamID64) {
			case "76561198095972970":
				return guild.getUserByID(223559816239513601L).getDisplayName(guild);
			case "76561198103577490":
				return guild.getUserByID(224234891762597889L).getDisplayName(guild);
			case "76561198041743174":
				return guild.getUserByID(224129884727803904L).getDisplayName(guild);
			case "76561198187239091":
				return guild.getUserByID(223807770552565762L).getDisplayName(guild);
			case "76561198038873933":
				return guild.getUserByID(277876854134931457L).getDisplayName(guild);
			case "76561198245710318":
				return guild.getUserByID(278897176271126528L).getDisplayName(guild);
			default:
				return "ноунейм";
		}
	}

	private void handleHelp(String command) {
		switch (command) {
			case "vac":
				Bot.log.sendMessage("Использование: ```~vac [<количество_дней> [<SteamID64>]]```\n" +
						"Пример: ```~vac 1 76561198095972970```\n" +
						"Допустимые значения дня: от 1 до 9999\n" +
						"Допустимые значения SteamID64: от 76561197960265729 до 76561202255233023");
				break;
			case "random":
				Bot.log.sendMessage("Использование:\t```~random [<конечное_значение> [<начальное_значение>]]```\n" +
						"Пример: ```~random 6 1```\n" +
						"Допустимые значения: от 1 до 9999");
				break;
			case "report":
				Bot.log.sendMessage("Использование:\t```~report <SteamID64> [<описание>]```\n" +
						"Пример: ```~report 76561198095972970 использовал ник Какер, играли на ньюке впятером```\n" +
						"");
				break;
			default:
				help("**неизвестная команда**");
		}
	}

	private void handleCommandVac(String steamid) {
		ArrayList<StringBuilder> hundredsOfSteamIDs;
		ArrayList<String> cheaters;
		hundredsOfSteamIDs = handleFriends(steamid);
		if (hundredsOfSteamIDs == null) {
			channel.sendMessage("Ошибка соединения!");
		} else {
			channel.sendMessage("Всего друзей: " + hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1));
		}
		cheaters = handleVacBanned(hundredsOfSteamIDs, days);
		StringBuilder bannedFriendsMessage = new StringBuilder();
		channel.sendMessage("Профили друзей, получивших VAC-бан за " + days + " д" + ending(days) + ":");
		if (!cheaters.isEmpty()) {
			for (String cheaterID : cheaters) {
				bannedFriendsMessage.append("");
				bannedFriendsMessage.append("http://steamcommunity.com/profiles/");
				bannedFriendsMessage.append(cheaterID).append("\n");
			}
			channel.sendMessage(String.valueOf(bannedFriendsMessage));
		} else {
			channel.sendMessage("Забаненных друзей нет");
		}
	}
}