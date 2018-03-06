package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;

public class UserCommand extends Command {

	public UserCommand() {
		super("user", UTILITY, "Устанавливает SteamID64 или имя пользователю.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~user <пользователь_Discord> [<SteamID64> | <имя>]`.",
				"`~user` — сканирует пользователей гильдии, добавляет новых пользователей в базу данных и" +
						"выводит в чат информацию по заполнению БД.",
				"`~user @salaleser 76561198095972970`, `~user @volevju Женя`.",
				"нет."
				)
		);
	}

	private String table = "users";

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String[][] discordids = DBHelper.executeQuery("SELECT discordid FROM " + table);
		String[][] steamids = DBHelper.executeQuery("SELECT steamid FROM " + table);
		Logger.info("Добавлено " + Util.refreshUsers() + " пользователей, всего в БД " + discordids.length +
				" пользователей, из них с неизвестными SteamID: " + (discordids.length - steamids.length) + ".");

		int usersCounter = 0;
		int unknownSteamidsCounter = 0;
		if (args.length == 0) {
			StringBuilder userBuilder = new StringBuilder(Util.ub("Пользователи гильдии " + guild.getName() + ":"));
			for (String[] row : discordids) {
				IUser user = guild.getUserByID(Long.parseLong(row[0]));
				if (user == null) continue; //если пользователя нет в гильдии, то не показывать его
				if (user.isBot()) continue; //если бот, то тоже не показывать
				usersCounter++;
				String steamid = Util.getSteamidByDiscordid(row[0]);
				if (steamid == null) unknownSteamidsCounter++;
				userBuilder
						.append("\n")
						.append(Util.b(user.getName()))
						.append(" (")
						.append(user.getNicknameForGuild(guild))
						.append(") ")
						.append(Util.code(row[0]))
						.append("|")
						.append(Util.code(steamid))
						.append(" — ")
						.append(Util.b("" + Util.getRank(guild, user)));
				if (usersCounter % 20 == 0) {
					message.getChannel().sendMessage(userBuilder.toString());
					userBuilder = new StringBuilder();
				}
			}
			userBuilder
					.append("\n\n")
					.append("Всего пользователей Вашей гильдии: ")
					.append(Util.b(usersCounter + ""))
					.append(". Из них с неизвестными SteamID: ")
					.append(Util.b(unknownSteamidsCounter + ""))
					.append(".");
			message.getChannel().sendMessage(userBuilder.toString());
			return;
		}

		String arg1 = args[0];
		IUser user = message.getAuthor();
		String discordid = user.getStringID();
		String steamid = Util.getSteamidByDiscordid(discordid);
		if (Util.isCommunityID(arg1)) arg1 = Util.getSteamidByCommunityid(arg1);
		if (Util.isSteamID64(arg1)) {
			steamid = arg1;
			discordid = Util.getDiscordidBySteamid(steamid);
		} else if (Util.isDiscordUser(arg1)) {
			discordid = arg1.replaceAll("[<@!>]", "");
			steamid = Util.getSteamidByDiscordid(discordid);
		}
		user = Bot.getClient().getUserByID(Long.parseLong(discordid));

		if (args.length == 1) {
			ArrayList<IRole> roles = new ArrayList<>(user.getRolesForGuild(guild));
			StringBuilder rolesBuilder = new StringBuilder();
			for (IRole role : roles) {
				if (role.getPosition() == 0) continue; //пропустить роль @everyone
				rolesBuilder.append(", ").append(Util.b(role.getName())).append(" (").append(Util.code(role.getStringID()))
						.append(" — ").append(Util.getRoleRank(role.getStringID())).append(")");
			}
			rolesBuilder = rolesBuilder.delete(0, 2);
			message.getChannel().sendMessage(
					Util.ub(user.getName()) + " " + Util.i("(" + user.getDisplayName(guild) + ")") + "\n" +
							"Discord ID: " + Util.code(discordid) + "\n" +
							"SteamID64: " + Util.code(steamid) + "\n" +
							"Роли: " + rolesBuilder + "\n" +
							"Ранг: " + Util.b(Util.getRank(guild, user) + "") + "\n"
			);
			return;
		}

		String newSteamid = "";
		if (Util.isCommunityID(args[1])) newSteamid = Util.getSteamidByCommunityid(args[1]);
		if (Util.isSteamID64(newSteamid)) {
			if (set(discordid, "steamid", newSteamid)) {
				message.getChannel().sendMessage("SteamID " + Util.b(newSteamid) +
						" успешно ассоциирован с пользователем " + Util.b(user.getName()) + ".");
			}
		} else if (set(discordid, "name", args[1])) {
			message.getChannel().sendMessage("Имя " + Util.b(args[1]) +
					" успешно добавлено пользователю " + Util.b(user.getName()) + ".");
		}
	}

	private boolean set(String discordid, String column, String value) {
		if (DBHelper.isUserExists("discordid", discordid)) {
			String query = "UPDATE " + table + " SET " + column + " = ? WHERE discordid = ?";
			return DBHelper.commit(table, query, new String[]{value, discordid});
		} else {
			Logger.error("Пользователя нет в БД.");
			return false;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР