package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", "" +
				Util.b("Описание:") + " Возвращает значения параметров команд.\n" +
				Util.b("Использование:") + " `~get <команда>`.\n" +
				Util.b("Предустановки:") + " нет.\n" +
				Util.b("Пример:") + " `~get poll`.\n" +
				Util.b("Примечание:") + " ничего особенного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			message.reply(" аргументы не заданы!");
			return;
		}
		String table = "settings";
		String name = args[0];
		String key = "%";
		if (args.length == 2) key = args[1];

		String sql = "SELECT * FROM " + table + " WHERE command = '" + name + "' AND key LIKE '" + key + "'";
		ArrayList<String[]> settings = DBHelper.executeQuery(sql);
		StringBuilder settingsBuilder = new StringBuilder();
		if (!Bot.getCommandManager().commands.containsKey(name)) settingsBuilder.append("Команда не поддерживается!");
		else if (settings.isEmpty()) settingsBuilder.append("У команды нет настроек!");
		for (String[] row : settings) {
			settingsBuilder.append("│");
			for (String element : row) settingsBuilder.append(Util.addSpaces(element)).append("│");
			settingsBuilder.append("\n");
		}
		message.getChannel().sendMessage(Util.block(settingsBuilder.toString()));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР