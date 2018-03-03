package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", UTILITY, "Возвращает значения параметров команд своей гильдии.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~get <команда>`.",
				"нет.",
				"`~get poll`.",
				"ничего особенного."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) {
			message.reply("мало аргументов.");
			return;
		}
		String table = "settings";
		String command = args[0];
		String key = "%";
		if (args.length == 2) key = args[1];

		if (!Bot.getCommandManager().commands.containsKey(command)) {
			message.reply("команда не поддерживается!");
			return;
		}

		String query = "SELECT * FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' " +
				"AND command LIKE '" + command + "' AND key LIKE '" + key + "' " +
				"UNION SELECT * FROM " + table + " WHERE guildid IS NULL " +
				"AND command LIKE '" + command + "' AND key LIKE '" + key + "'";
		String[][] data = DBHelper.executeQuery(query);
		if (data[0] == null) message.reply("у команды нет настроек!");
		else message.getChannel().sendMessage(Util.makeTable(table, new String[] {"*"}, data));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР