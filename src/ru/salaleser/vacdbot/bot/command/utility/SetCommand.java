package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class SetCommand extends Command {

	public SetCommand() {
		super("set");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Устанавливает параметры командам.",
				"`~set <команда> <параметр> <значение>`.",
				"нет.",
				"`~set poll countdown 15`.",
				"используйте с умом, ибо можно прострелить себе ногу случайно этой командой."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		// FIXME: 30.11.17 при установке несуществующего параметра возвращает true (есть мнение, что это не баг, а фича)
		String table = "settings";
		if (args.length != 3) {
			message.reply(" неправильный синтаксис.");
			return;
		}

		//частный случай для DBHelper.update(), пока так удобней:
		String sql = "UPDATE " + table + " SET value = ? WHERE command = ? AND key = ?";
		String[] newArgs = new String[] {args[2], args[0], args[1]};

		if (DBHelper.commit(table, sql, newArgs)) message.reply(" параметр установлен успешно.");
		else message.reply(" параметр не установлен.");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР