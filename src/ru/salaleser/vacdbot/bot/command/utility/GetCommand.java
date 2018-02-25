package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
		if (args.length == 1 && args[0].equals("permissions")) {
			StringBuilder stringBuilder = new StringBuilder();
			ArrayList<Command> commands = new ArrayList<>();
			for (Map.Entry e : Bot.getCommandManager().commands.entrySet()) commands.add((Command) e.getValue());
			for (Command c : commands) stringBuilder.append(c.name).append(" — ").append(Arrays.toString(c.aliases)).append("\n");
			message.getChannel().sendMessage(Util.block(stringBuilder.toString()));
			return;
		}

		String table = "settings";
		String command = "%";
		String key = "%";
		if (args.length > 0) command = args[0];
		if (args.length == 2) key = args[1];

		String sqlDefaults = "SELECT * FROM " + table + " WHERE guildid = 'default' " +
				"AND command LIKE '" + command + "' AND key LIKE '" + key + "' " +
				"ORDER BY guildid, command, key, value";
		String[][] dataDefaults = DBHelper.executeQuery(sqlDefaults);
		String sql = "SELECT * FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' " +
				"AND command LIKE '" + command + "' AND key LIKE '" + key + "' " +
				"ORDER BY guildid, command, key, value";
		String[][] data = DBHelper.executeQuery(sql);


		if (args.length > 0 && !Bot.getCommandManager().commands.containsKey(command)) {
			message.reply(" команда не поддерживается!");
		} else if (dataDefaults[0][0] == null) {
			message.reply(" у команды нет настроек!");
			return;
		}
		message.getChannel().sendMessage(Util.makeTable(table, new String[] {"*"}, data));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР