package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class IdCommand extends Command {

	public IdCommand() {
		super("id");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Возвращает ID.",
				"`~id [<ID_пользователя_Discord>]`.",
				"`~id` — возвращает SteamID64 автора сообщения.",
				"`~id @salaleser`.",
				"нет."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String discordid = message.getAuthor().getStringID();
		switch (args.length) {
			case 1:
				if (Util.isDiscordUser(args[0])) {
					discordid = args[0].replaceAll("[<@!>]", "");
				} else {
					message.getChannel().sendMessage("Неверный ID");
				}
				break;
		}
		String username = message.getClient().getUserByID(Long.parseLong(discordid)).getName();
		String steamid = Util.getSteamidByDiscordid(discordid);
		message.getChannel().sendMessage("SteamID64 " + username + ": " + steamid);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР