package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ru.salaleser.vacdbot.Config.*;
import static ru.salaleser.vacdbot.Util.*;

public class VacCommand extends Command {

	private final HttpClient httpClient = new HttpClient();

	public VacCommand() {
		super("vac", STEAM, "Показывает забаненных друзей пользователя за указанный период времени.", new String[]{"вак"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~vac [<@User>|<Discord_ID>|<SteamID64>|<CommunityURL>] [<количество_дней>]`.",
				"`~vac` — ….",
				"`~vac 76561198095972970`, `~vac 30 @salaleser`.",
				"день может быть от 1 до 9999."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		int days = 5;
		this.guild = guild;
		IChannel channel = message.getChannel();
		IUser user = message.getAuthor();
		String discordid = user.getStringID();
		String steamid = getSteamID64ByDiscordID(guild.getStringID(), discordid);
		String username = getName(guild, user);
		HashMap<String, String> map = getArgs(guild, args);
		if (map.containsKey(NUMBER)) days = Integer.parseInt(map.get(NUMBER));
		if (map.containsKey(STEAMID64)) {
			steamid = map.get(STEAMID64);
			discordid = getDiscordidBySteamid(steamid);
			if (discordid != null) {
				user = guild.getUserByID(Long.parseLong(discordid));
				if (user != null) {
					username = getName(guild, user);
				} else {
					username = "SteamID64 " + steamid;
				}
			}
		} else if (map.containsKey(DISCORDID)) { // FIXME: 16.03.2018
			discordid = map.get(DISCORDID);
			user = guild.getUserByID(Long.parseLong(discordid));
			if (user != null) {
				steamid = getSteamID64ByDiscordID(guild.getStringID(), discordid);
				username = getName(guild, user);
				if (steamid == null) {
					message.reply(i("SteamID64 не ассоциирован с пользователем " + username + ". " +
							"Взаимодействие со Steam API невозможно!"));
					return;
				}
			} else {
				message.reply(i("невозможно идентифицировать пользователя!"));
				return;
			}
		}
		
		channel.sendMessage("Проверяю друзей " + username + "…");

		StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/ISteamUser/GetFriendList/" +
				"v0001/?key=" + Config.getSteamWebApiKey() + "&steamid=" + steamid + "&relationship=friend");
		if (jsonFriends == null) {
			Logger.error("Время ожидания вышло! Повторите запрос...", guild);
			return;
		}

		ParserFriends parserFriends = new ParserFriends();
		ArrayList<StringBuilder> hundredsOfSteamIDs = parserFriends.parse(jsonFriends);

		channel.sendMessage("Всего друзей: " +
				b(hundredsOfSteamIDs.get(hundredsOfSteamIDs.size() - 1).toString()) + "\nПроверяю друзей на баны…");
		ParserFriendsBans parserBans = new ParserFriendsBans();
		HashMap<String, Integer> cheaters = new HashMap<>();
		//не забыть исключить из парсинга последний элемент массива (количество друзей)
		for (int i = 0; i < hundredsOfSteamIDs.size() - 1; i++) {
			StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/ISteamUser/GetPlayerBans/" +
					"v1/?key=" + Config.getSteamWebApiKey() + "&steamids=" + hundredsOfSteamIDs.get(i));
			if (jsonBans == null) {
				channel.sendMessage(i("Ошибка HTTP-соединения на " + ++i + "-ой итерации! Повторяю запрос..."));
				i--;
			} else {
				cheaters = parserBans.parse(jsonBans);
			}
		}

		int lastOnesNumber = 0;
		StringBuilder bMessage = new StringBuilder("Профили друзей " + username + ", получивших бан за последние " +
				days + getEnding("день", days));
		for (Map.Entry<String, Integer> lastOne : cheaters.entrySet()) {
			if (lastOne.getValue() < days) {
				lastOnesNumber++;
				bMessage.append("\n").append("http://steamcommunity.com/profiles/").append(lastOne.getKey());
			}
		}

		if (lastOnesNumber > 0) {
			channel.sendMessage(String.valueOf(bMessage)); //fixme hardcode повтор кода
		} else {
			channel.sendMessage(b("Забаненных друзей " + username + " за последние " +
					days + getEnding("день", days)+ " нет. Пока нет..."));
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР