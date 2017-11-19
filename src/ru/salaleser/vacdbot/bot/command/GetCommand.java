package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Config;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", "Просмотр параметров.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 2 && args[0].equals("poll")) {
			message.getChannel().sendMessage(processPoll(Arrays.copyOfRange(args, 1, args.length)));
		} else {
			message.getChannel().sendMessage("отказ");
		}
	}

	private String processPoll(String[] args) {
		if (args[0].equals("countdown")) return String.valueOf(Config.getPollCountdown());
		return "отказ";
	}
}
