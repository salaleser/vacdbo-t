package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

public class StatusCommand extends Command {

	public StatusCommand() {
		super("status", "устанавливает статус боту (\"Играет в **<ваш_остроумный_текст>**\")");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		Bot.bot.getClient().changePlayingText(args[0]);
//		message.getChannel().sendMessage("MEs").addReaction("<:rasmus:286853270746431488>");
	}
}
