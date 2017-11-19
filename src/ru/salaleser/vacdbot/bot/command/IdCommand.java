package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

import java.util.concurrent.ThreadLocalRandom;

public class IdCommand extends Command {

	public IdCommand() {
		super("id", "Возвращает ID");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			message.getChannel().sendMessage(Utilities.getSteamidByDiscordUser(message.getAuthor()));
			return;
		}
		if (args.length == 1 && Utilities.isSteamID64(args[0])) {
			message.getChannel().sendMessage(Utilities.getDiscordDisplayNameBySteamid(args[0], message.getGuild()));
		} else if (args[0].startsWith("<@!")) {
			long discordid = Long.parseLong(args[0].substring(3, args[0].length() - 1));
			System.out.println(discordid);
			message.getChannel().sendMessage(Utilities.getSteamidByDiscordUser(message.getGuild().getUserByID(discordid)));
		}
	}
}