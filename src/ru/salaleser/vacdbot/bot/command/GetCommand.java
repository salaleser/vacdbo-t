package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.Map;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", 2, "" +
				Util.b("Описание:") + " Возвращает значения параметров команд.\n" +
				Util.b("Использование:") + " `~get <команда>`.\n" +
				Util.b("Предустановки:") + " нет.\n" +
				Util.b("Пример:") + " `~get poll`.\n" +
				Util.b("Примечание:") + " ничего особенного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 1 && args[0].equals("permissions")) {
			StringBuilder stringBuilder = new StringBuilder();
			ArrayList<Command> commands = new ArrayList<>();
			for (Map.Entry e : Bot.getCommandManager().commands.entrySet()) commands.add((Command) e.getValue());
			for (Command c : commands) stringBuilder.append(c.name).append(" — ").append(c.permissions).append("\n");
			message.getChannel().sendMessage(Util.block(stringBuilder.toString()));
			return;
		}

		String table = "settings";
		String name = "%";
		String key = "%";
		if (args.length > 0) name = args[0];
		if (args.length == 2) key = args[1];

		String sql = "SELECT * FROM " + table + " WHERE command LIKE '" + name + "' AND key LIKE '" + key + "'";
		String[][] data = DBHelper.executeQuery(sql);

		if (args.length > 0 && !Bot.getCommandManager().commands.containsKey(name)) {
			message.reply(" команда не поддерживается!");
		} else if (data[0][0].isEmpty()) {
			message.reply(" у команды нет настроек!");
		}
		message.getChannel().sendMessage(Util.makeTable(table, new String[] {"*"}, data));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР