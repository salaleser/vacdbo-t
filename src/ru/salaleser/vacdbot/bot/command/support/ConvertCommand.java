package ru.salaleser.vacdbot.bot.command.support;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ConvertCommand extends Command {

	public ConvertCommand() {
		super("convert", SUPPORT, "Конвертирует всё во всё из всего.", new String[]{"con", "$"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~convert <аргумент>`.",
				"нет.",
				"`~convert 1519767814`; `$76561198095972970`.",
				"Поддерживаемые операции: @Роль дискорда в StringID дискорда; " +
						"SteamID64 в URL Steam-профиля; UNIX-время в дату и время.\n" +
						"Можно использовать знак `$` для активации команды."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String argument = "";
		if (args.length != 0) argument = args[0];
		String converted;
		String text;
		if (Util.isDiscordRole(argument, guild)) {
			converted = argument.replaceAll("[<@&>]", "");
			text = "ID роли " + guild.getRoleByID(Long.parseLong(argument)).getName() + " " + Util.b(converted);
		} else if (Util.isSteamID64(argument)) {
			converted = "http://steamcommunity.com/profiles/" + argument;
			text = "Профиль пользователя с таким SteamID64 вы можете найти по адресу " + converted;
		} else if (Util.isTimestamp(argument)) {
			Timestamp timestamp = new Timestamp(Long.parseLong(argument) * 1000L);
			converted = timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss"));
			text = "Timestamp " + argument + " соответствует " + Util.b(converted);
		} else {
			converted = Util.block("  ___\n{~._.~}\n ( Y ) \n()~*~()\n(_)-(_)");
			text = "Не удалось ничего подобрать :( Конвертирую ничего в милоту...\n" + converted;
		}
		message.getChannel().sendMessage(text);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР