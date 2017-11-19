package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class IdCommand extends Command {

	public IdCommand() {
		super("id", "**Описание:** Возвращает ID.\n" +
				"**Использование:** `~id [<ID_пользователя_Discord>]`.\n" +
				"**Предустановки:** `~id` — возвращает SteamID64 автора сообщения.\n" +
				"**Пример:** `~id @salaleser`.\n" +
				"**Примечание:** ничего примечательного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			message.getChannel().sendMessage(Utilities
					.getSteamidByDiscordUser(message.getAuthor().getStringID()));
			return;
		}
		if (args.length == 1 && Utilities.isSteamID64(args[0])) {
			message.getChannel().sendMessage(Utilities
					.getDiscordNameBySteamid(args[0]));
		} else if (args.length == 1 && args[0].startsWith("<@")) {
			message.getChannel().sendMessage(Utilities
					.getSteamidByDiscordUser(args[0].replaceAll("[<@!>]", "")));
		} else if (args.length == 1 && args[0].length() == 18) {
			message.getChannel().sendMessage(Utilities
					.getSteamidByDiscordUser(args[0]));
		} else {
			message.getChannel().sendMessage("Неверный ID");
		}
	}
}