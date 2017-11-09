package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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
				case "help":
				case "помощь":
				case "?":
					switch (args.length) {
						case 2:
							handleHelp(args[1]);
						case 1:
							handleHelp(command);
							break;
						default:
							help("**количество аргументов превышает допустимое, бич**");
					}
					break;
				case "vac":
				case "вак":
					switch (args.length) {
						case 3:
							if (isSteamID64(args[2])) steamid = args[2];
							else help("**ошибка в SteamID64** (использую профиль salaleser");
						case 2:
							if (isNumeric(args[1])) days = Integer.parseInt(args[1]);
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
							if (isNumeric(args[2]) && Integer.parseInt(args[2]) < Integer.parseInt(args[1])) {
								min = Integer.parseInt(args[2]);
							} else {
								help("**ошибка во втором аргументе** (использую значение " + min + ")");
							}
						case 2:
							if (isNumeric(args[1])) max = Integer.parseInt(args[1]);
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
				case "map":
				case "карта":
					String[] mapList = new String[]{
							"de_train",
							"de_nuke",
							"de_dust2",
							"de_cache",
							"de_mirage",
							"de_inferno",
							"de_cobblestone",
							"de_overpass",
							"cs_office"
					};
					int numberOfMaps = mapList.length;
					if (args.length > 1 && isNumeric(args[1]) &&
							Integer.parseInt(args[1]) <= mapList.length && Integer.parseInt(args[1]) > 0) {
						numberOfMaps = Integer.parseInt(args[1]);
					} else {
						help("**ошибка в первом аргументе** (использую значение " + numberOfMaps + ")");
					}
					int mapNumber = (int) (Math.random() * numberOfMaps);
					System.out.println(mapNumber);
					channel.sendMessage("Играть будем на карте " + mapList[mapNumber]);
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

	private boolean isSteamID64(String steamID64) {
		return steamID64.length() == 17 &&
				steamID64.matches("\\d+") &&
				Long.parseLong(steamID64) > 76561197960265729L &&
				Long.parseLong(steamID64) < 76561202255233023L;
	}

	private boolean isNumeric(String string) {
		return string.matches("\\d+") && string.length() < 5;
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
		Main.log.sendMessage(author + ", " + error);
		Main.log.sendMessage("\nПоддерживаемые команды: `vac`, `help`, `map`, `server`, `random`, `bind`\n" +
				"(некоторые команды доступны на русском или в сокращённом варианте, например: `вак`, `серв`, `rnd`)"
		);
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
			case "help":
				Main.log.sendMessage("Использование:\t```~help " + command + "```\n" +
						"Пример: ```~help vac```\n");
				break;
			case "vac":
				Main.log.sendMessage("Использование: ```~vac [<количество_дней> [<SteamID64>]]```\n" +
						"Пример: ```~vac 1 76561198095972970```\n" +
						"Допустимые значения дня: от 1 до 9999\n" +
						"Допустимые значения SteamID64: от 76561197960265729 до 76561202255233023");
				break;
			case "random":
				Main.log.sendMessage("Использование:\t```~random [<конечное_значение> [<начальное_значение>]]```\n" +
						"Пример: ```~random 6 1```\n" +
						"Допустимые значения: от 1 до 9999");
				break;
			case "map":
				Main.log.sendMessage("Использование:\t```~map [<количество_карт_в_порядке_желательности>]```\n" +
						"Пример: ```~map 3```\n" +
						"Допустимые значения: от 1 до 8");
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