package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class UserCommand extends Command {

	public UserCommand() {
		super("user");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Устанавливает значения в таблицу \"users\".",
				"`~user <пользователь_Discord> <SteamID64> | <имя>`.",
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
			int discordidCount = Integer.parseInt(DBHelper.executeQuery("SELECT COUNT(discordid) FROM users")[0][0]);
			int steamidCount = Integer.parseInt(DBHelper.executeQuery("SELECT COUNT(steamid) FROM users")[0][0]);
			int nonameCount = discordidCount - steamidCount;
			message.getChannel().sendMessage(Util.i("Добавлено " + Util.b("" + Util.refreshUsers()) +
					" пользователей, всего в БД " + Util.b(String.valueOf(discordidCount)) + " пользователей, " +
					"из них с неизвестными SteamID: " + Util.b(String.valueOf(nonameCount)) + "."));
			return;
		}
		if (args.length != 2) return;
		String discordid = args[0].replaceAll("[<@!>]", "");
		String username = Bot.getClient().getUserByID(Long.parseLong(discordid)).getName();
		if (Util.isDiscordUser(args[0])) {
			if (Util.isSteamID64(args[1])) {
				if (set(discordid, "steamid", args[1])) {
					message.getChannel().sendMessage("SteamID " + Util.b(args[1]) +
							" успешно ассоциирован с пользователем " + Util.b(username) + ".");
				}
			} else if (Util.isNumeric(args[1])) {
				if (set(discordid, "priority", args[1])) {
					message.getChannel().sendMessage("Приоритет " + Util.b(args[1]) +
							" успешно установлен пользователю " + Util.b(username) + ".");
				}
			} else {
				if (set(discordid, "name", args[1])) {
					message.getChannel().sendMessage("Имя " + Util.b(args[1]) +
							" успешно добавлено пользователю " + Util.b(username) + ".");
				}
			}
		} else {
			message.getChannel().sendMessage("Неверный айди дискорда!");
		}
	}

	private boolean set(String discordid, String column, String value) {
		String table = "users";
		if (DBHelper.isExists(table, "discordid", discordid)) {
			String sql = "UPDATE " + table + " SET " + column + " = ? WHERE discordid = ?";
			return DBHelper.commit(table, sql, new String[]{value, discordid});
		} else {
			Logger.error("Пользователя нет в БД.");
			return false;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР