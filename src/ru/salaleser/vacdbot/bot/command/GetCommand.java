package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", "" +
				Util.b("Описание:") + " Возвращает параметры из конфигурационного файла.\n" +
				Util.b("Использование:") + " `~get <аргумент [<аргумент>]>`.\n" +
				Util.b("Предустановки:") + " `~get <команда>` — возвращает параметры команды.\n" +
				Util.b("Пример:") + " `~get poll`.\n" +
				Util.b("Примечание:") + " ничего особенного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) return;
		Command command = Bot.getCommandManager().commands.get(args[0]);
		message.reply(command.get(Arrays.copyOfRange(args, 1, args.length)));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР