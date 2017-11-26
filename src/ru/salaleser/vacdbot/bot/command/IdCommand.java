package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

public class IdCommand extends Command {

	public IdCommand() {
		super("id", "" +
				Util.b("Описание:") + " Возвращает ID.\n" +
				Util.b("Использование:") + " `~id [<ID_пользователя_Discord>]`.\n" +
				Util.b("Предустановки:") + " `~id` — возвращает SteamID64 автора сообщения.\n" +
				Util.b("Пример:") + " `~id @salaleser`.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		String discordid = message.getAuthor().getStringID();
		if (args.length != 0) {
			if (Util.isDiscordUser(args[0])) discordid = args[0];
			else message.getChannel().sendMessage("Неверный ID");
		}
		String steamid = Util.getSteamidByDiscordUser(discordid);
		message.getChannel().sendMessage("SteamID64 " + discordid + ": " + Util.code(steamid));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР