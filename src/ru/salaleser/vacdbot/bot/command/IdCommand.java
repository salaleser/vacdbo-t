package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

public class IdCommand extends Command {

	public IdCommand() {
		super("id", "" +
				Util.b("Описание:") + " Возвращает ID.\n" +
				Util.b("Использование:") + " `~id [<ID_пользователя_Discord || SteamID64>]`.\n" +
				Util.b("Предустановки:") + " `~id` — возвращает SteamID64 автора сообщения.\n" +
				Util.b("Пример:") + " `~id @salaleser`, `~id 76561198095972970`.\n" +
				Util.b("Примечание:") + " можно указать SteamID64 или Discord ID.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			message.getChannel().sendMessage(Util.getSteamidByDiscordUser(message.getAuthor().getStringID()));
			return;
		}
		if (Util.isSteamID64(args[0])) {
			message.getChannel().sendMessage("Discord ID " + args[0] + ": " +
					Util.getDiscordUserBySteamid(args[0]));
			return;
		}
		if (Util.isDiscordUser(args[0])) {
			message.getChannel().sendMessage("SteamID64 " + args[0] +": " +
					Util.getSteamidByDiscordUser(args[0]));
			return;
		}
		message.getChannel().sendMessage("Неверный ID");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР