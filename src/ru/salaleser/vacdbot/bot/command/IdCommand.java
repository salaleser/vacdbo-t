package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class IdCommand extends Command {

	public IdCommand() {
		super("id", 2);
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
	public void handle(IMessage message, String[] args) {
		String discordid = message.getAuthor().getStringID();
		if (args.length != 0) {
			if (Util.isDiscordUser(args[0])) {
				discordid = args[0];
				discordid = discordid.replaceAll("[<@!>]", "");
			}
			else message.getChannel().sendMessage("Неверный ID");
		}
		String username = message.getClient().getUserByID(Long.parseLong(discordid)).getName();
		String steamid = Util.getSteamidByDiscordUser(discordid);
		message.getChannel().sendMessage("SteamID64 " + username + ": " + steamid);
	}

	public void update() {
		//здесь хотел добавить возможность обновлять таблицу профилей из дискорда,
		//но что-то не сильно это и надо как мне показалось чуть позже
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР