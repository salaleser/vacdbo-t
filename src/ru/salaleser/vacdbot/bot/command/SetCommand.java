package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

public class SetCommand extends Command {

	public SetCommand() {
		super("set", "" +
				Util.b("Описание:") + " Устанавливает параметры командам.\n" +
				Util.b("Использование:") + " `~set <команда> <параметр> <значение>`.\n" +
				Util.b("Предустановки:") + " нет.\n" +
				Util.b("Пример:") + " `~set poll countdown 15`.\n" +
				Util.b("Примечание:") + " используйте с умом.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length != 3) {
			message.reply(" неправильный синтаксис.");
			return;
		}
		String name = args[0];
		String key = args[1];
		String value = args[2];

		// FIXME: 30.11.17 при установке несуществующего параметра возвращает true (есть мнение, что это не баг, а фича)
		if (DBHelper.setSettings(name, key, value)) message.reply(" параметр установлен успешно.");
		else message.reply(" параметр не установлен.");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР