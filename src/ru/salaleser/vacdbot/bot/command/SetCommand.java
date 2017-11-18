package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.bot.Config;
import sx.blah.discord.handle.obj.IMessage;

public class SetCommand extends Command {

	public SetCommand() {
		super("set", "Установка параметров.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 3 && args[0].equals("poll") && args[1].equals("countdown")) {
			Config.setPollCountdown(args[2]);
		}
		else message.getChannel().sendMessage("неверный запрос");
	}
}
