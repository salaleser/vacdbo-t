package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.HashMap;

import static ru.salaleser.vacdbot.Config.*;
import static ru.salaleser.vacdbot.Util.*;

public class UserCommand extends Command {

	public UserCommand() {
		super("user", UTILITY, "Устанавливает SteamID64, FACEIT ID или пол пользователям.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~user [<@User>|<Discord_ID>|<SteamID64>|<CommunityURL>] [<SteamID64>|<CommunityURL>|faceit:<FACEIT_ID>|<пол>]`.",
				"`~user` — показывает всех пользователей гильдии.",
				"`~user @salaleser 76561198095972970`, `~user @volevju M volevju`.",
				"чтобы указать профиль фейсита, учитывайте необходимость вначале приписать \"faceit:\" " +
						i("(тем кто шарит — регексп должен матчиться по такому паттерну: `^faceit:\\w{4,16}$`)") + "."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		this.guild = guild;
		IChannel channel = message.getChannel();
		String table = "users";
		refreshUsers(guild);

		int usersCounter = 0;
		int unknownSteamidsCounter = 0;
		if (args.length == 0) {
			StringBuilder userBuilder = new StringBuilder(ub("Пользователи гильдии " + guild.getName() + ":"));
			for (IUser user : guild.getUsers()) {
				if (user == null) continue; //если пользователя нет в гильдии, то не показывать его
				if (user.isBot()) continue; //если бот, то тоже не показывать
				usersCounter++;
				String steamid = getSteamID64ByDiscordID(guild.getStringID(), user.getStringID());
				if (steamid == null) unknownSteamidsCounter++;
				userBuilder
						.append("\n")
						.append(b(getName(guild, user)))
						.append(" ")
						.append(code(user.getStringID()))
						.append("|")
						.append(code(steamid))
						.append(" — ")
						.append(b("" + getRank(guild, user)));
				if (usersCounter % 20 == 0) {
					channel.sendMessage(userBuilder.toString());
					userBuilder = new StringBuilder();
				}
			}
			userBuilder
					.append("\n\n")
					.append("Всего пользователей Вашей гильдии: ")
					.append(b(usersCounter + ""))
					.append(". Из них с неизвестными SteamID: ")
					.append(b(unknownSteamidsCounter + ""))
					.append(".");
			channel.sendMessage(userBuilder.toString());
			return;
		}

		HashMap<String, String> argsMap = getArgs(guild, args);
		String discordid = argsMap.get(DISCORDID);
		if (discordid == null) {
			message.reply("невозможно идентифицировать пользователя!");
			return;
		}

		IUser user = guild.getUserByID(Long.parseLong(discordid));
		String steamid = DBHelper.executeQuery(getQuery("steamid", discordid))[0][0];
		if (argsMap.containsKey(STEAMID64)) steamid = argsMap.get(STEAMID64);
		String faceit = DBHelper.executeQuery(getQuery("faceit", discordid))[0][0];
		if (argsMap.containsKey(FACEITID)) faceit = argsMap.get(FACEITID);
		String sex = DBHelper.executeQuery(getQuery("sex", discordid))[0][0];
		if (argsMap.containsKey(SEX)) sex = argsMap.get(SEX);

		if (args.length == 1) {
			ArrayList<IRole> roles = new ArrayList<>(user.getRolesForGuild(guild));
			StringBuilder rolesBuilder = new StringBuilder();
			for (IRole role : roles) {
				if (role.getPosition() == 0) continue; //пропустить роль @everyone
				rolesBuilder.append(", ").append(b(role.getName())).append(" (").append(code(role.getStringID()))
						.append(" — ").append(getRoleRank(role.getStringID())).append(")");
			}
			rolesBuilder = rolesBuilder.delete(0, 2);
			channel.sendMessage(
					ub(getName(guild, user)) + "\n" +
							"Discord ID: " + code(discordid) + "\n" +
							"SteamID64: " + code(steamid) + "\n" +
							"FACEIT ID: " + b(faceit) + "\n" +
							"Роли: " + rolesBuilder + "\n" +
							"Ранг: " + b(getRank(guild, user) + "") + "\n"
			);
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
		if (argsMap.containsKey(STEAMID64)) queryBuilder.append("steamid").append(" = ?, ");
		if (argsMap.containsKey(SEX)) queryBuilder.append("sex").append(" = ?, ");
		if (argsMap.containsKey(FACEITID)) queryBuilder.append("faceit").append(" = ?, ");
		queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1); //удаляю последнюю запятую
		queryBuilder.append("WHERE guildid = '").append(guild.getStringID()).append("' ")
				.append("AND discordid = '").append(discordid).append("'");
		argsMap.remove(DISCORDID); //удалю чтобы он не попал в массив в параметре метода commit()
		if (DBHelper.commit(table, queryBuilder.toString(), argsMap.values().toArray(new String[0]))) {
			StringBuilder replyBuilder = new StringBuilder();
			if (argsMap.containsKey(STEAMID64)) replyBuilder.append(", SteamID64 ").append(b(steamid));
			if (argsMap.containsKey(FACEITID)) replyBuilder.append(", FACEIT ID ").append(b(faceit));
			if (argsMap.containsKey(SEX)) {
				switch (sex) {
					case "W": replyBuilder.append(", пол ").append(b("женский")); break;
					case "N": replyBuilder.append(", пол ").append(b("неопределенный")); break;
					default: replyBuilder.append(", пол ").append(b("мужской")); break;
				}
			}
			replyBuilder.delete(0, 2);
			replyBuilder.append(" успешно добавлен");
			if (argsMap.size() > 1) replyBuilder.append("ы");
			message.reply(replyBuilder + " пользователю " + b(user.getName()) + ".");
		} else {
			message.reply("параметр не установлен.");
		}
	}

	private String getQuery(String column, String discordid) {
		return "SELECT " + column + " FROM users " +
				"WHERE guildid = '" + guild.getStringID() + "' AND discordid = '" + discordid + "'";
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР