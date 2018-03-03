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

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) {
			replyNonameCount(message.getChannel());
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
				rolesBuilder.append(", ").append(role.getName()).append(" (").append(role.getStringID())
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
		String table = "users";
		if (DBHelper.isUserExists("discordid", discordid)) {
			String sql = "UPDATE " + table + " SET " + column + " = ? WHERE discordid = ?";
			return DBHelper.commit(table, sql, new String[]{value, discordid});
		} else {
			Logger.error("Пользователя нет в БД.");
			return false;
		}
	}

	private void replyNonameCount(IChannel channel) {
		int discordidCount = Integer.parseInt(DBHelper.executeQuery("SELECT COUNT(discordid) FROM users")[0][0]);
		int steamidCount = Integer.parseInt(DBHelper.executeQuery("SELECT COUNT(steamid) FROM users")[0][0]);
		channel.sendMessage(Util.i("Добавлено " + Util.b("" + Util.refreshUsers()) +
				" пользователей, всего в БД " + Util.b(String.valueOf(discordidCount)) + " пользователей, " +
				"из них с неизвестными SteamID: " + Util.b(String.valueOf(discordidCount - steamidCount)) + "."));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР