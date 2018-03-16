package ru.salaleser.vacdbot.bot.command.support;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class ConvertCommand extends Command {

	public ConvertCommand() {
		super("convert", SUPPORT, "Конвертирует всё во всё из всего.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~convert <аргумент>`.",
				"нет.",
				"`~convert 1519767814`; `$76561198095972970`.",
				"Поддерживаемые операции: @Роль дискорда в StringID дискорда; " +
						"SteamID64 в URL Steam-профиля; UNIX-время в дату и время."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) {
			message.reply("пока конвертер работает принудительно, например если в тексте будет обнаружен SteamID64, " +
					"то бот любезно предоставит ссылку на этот профиль.");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР