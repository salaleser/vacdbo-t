package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class SelectCommand extends Command {

	public SelectCommand() {
		super("select", 2);
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"SELECT в БД бота.",
				"не надо ее использовать.",
				"не скажу.",
				"не покажу.",
				"добавлена для отладки."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		for (int i = 0; i < args.length; i++) args[i] = args[i].toLowerCase();
		//придётся костыльным методом вычленять название таблицы в угоду удобству:
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("from")) {
				String table = args[i + 1];
				StringBuilder sqlBuilder = new StringBuilder("select");
				for (String arg : args) {
					sqlBuilder.append(" ").append(arg);
				}
				//удаляю пробелы после запятых:
				String sqlWithoutSpaces = sqlBuilder.toString().replaceAll(", ",",");
				String[] sql = sqlWithoutSpaces.split(" ");
				for (int j = 0; j < sql.length; j++) {
					if (sql[j].equals("select")) {
						String[] columns = sql[j + 1].split(",");
						String[][] data = DBHelper.executeQuery(sqlBuilder.toString());
						message.getChannel().sendMessage(Util.makeTable(table, columns, data));
						return;
					}
				}
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР