package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Config;
import sx.blah.discord.handle.obj.IMessage;

public class ServerCommand extends Command {
	private String trainingServerAddress = Config.getTrainingServerAddress();

	public ServerCommand() {
		super("server", "даёт ссылку на сервер");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		message.getChannel().sendMessage("Тренировочный сервер: " +
				"steam://" + trainingServerAddress + "//\n" +
				"```connect " + trainingServerAddress + "; password 2002```" +
				"```connect " + trainingServerAddress + "; password 2002; rcon_password ```");
	}
}
