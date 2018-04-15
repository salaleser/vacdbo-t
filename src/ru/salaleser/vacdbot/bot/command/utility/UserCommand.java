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
		super("user", UTILITY, "Устанавливает SteamID64, FACEIT ID или пол пользователям.", new String[]{"юзер"});
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

		if (args.length == 0) return;

		String discordid = null;
		String steamid;
		HashMap<String, String> argsMap = getArgs(guild, args);
		if (argsMap.containsKey(DISCORDID)) {
			discordid = argsMap.get(DISCORDID);
			steamid = getSteamID64ByDiscordID(guild.getStringID(), discordid);
		} else if (argsMap.containsKey(STEAMID64)) {
			steamid = argsMap.get(STEAMID64);
			discordid = getDiscordidBySteamid(steamid);
		}
		if (discordid == null) {
			message.reply("невозможно идентифицировать пользователя!");
			return;
		}

		IUser user = guild.getUserByID(Long.parseLong(discordid));
		steamid = DBHelper.executeQuery(getQuery("steamid", discordid))[0][0];
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
			message.reply(replyBuilder + " пользователю " + b(getName(guild, user)) + ".");
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