package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		IChannel channel = message.getChannel();
		String steamid = Util.getSteamidByDiscordUser(message.getAuthor().getStringID());
		int days = 1;

		if (args.length > 0) {
			if (Util.isNumeric(args[0])) {
				days = Integer.parseInt(args[0]);
				if (args.length > 1) {
					if (Util.isSteamID64(args[1])) {
						steamid = args[1];
					} else {
						message.reply(Util.b("ошибка в профиле") + "(проверяю баны друзей salaleser'а)");
					}
				} else {
					message.reply(Util.b("профиль не задан") + " (проверяю баны друзей salaleser'а)");
				}
			} else {
				message.reply(Util.b("ошибка в количестве дней") + " (проверяю баны за сегодня)");
			}
		} else {
			message.reply(Util.b("аргументы не заданы") + " (проверяю баны друзей salaleser'а за сегодня)");
		}

		String name = message.getAuthor().getName();
		message.getChannel().sendMessage("Проверяю " +
				"друзей " + name + "...");
		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/" +
				"ISteamUser/GetFriendList/v0001/" + "?key=" + Config.getSteamWebApiKey() + "&steamid=" +
				steamid + "&relationship=friend");
		if (jsonFriends == null) {
			message.getChannel().sendMessage("*Время ожидания вышло! Повторите запрос...*");
			return;
		}
		ParserFriends parserFriends = new ParserFriends();
		ArrayList<StringBuilder> hundredsOfSteamIDs = parserFriends.parse(jsonFriends);

		message.getChannel().sendMessage("Всего друзей: " +
				Util.b(hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1).toString()) +
				"\nПроверяю друзей на баны...");
		ParserFriendsBans parserBans = new ParserFriendsBans();
		HashMap<String, Integer> cheaters = new HashMap<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
					"ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" +
					hundredsOfSteamIDs.get(i));
			if (jsonBans == null) {
				message.getChannel().sendMessage("*Ошибка HTTP-соединения на *" + i +
						"*-ой итерации! Повторяю запрос...*");
				i--;
			} else {
				cheaters = parserBans.parse(jsonBans);
			}
		}

		int lastOnesNumber = 0;
		StringBuilder bMessage = new StringBuilder("Профили друзей " + name + ", получивших бан за последние " +
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
			channel.sendMessage(Util.b("Забаненных друзей " + name + " за последние " + days + " д" + Util.ending(days) + " нет. Пока нет..."));
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР