package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

public class NewCommand extends Command {

	public NewCommand() {
		super("report", "Использование: ```~report <SteamID64> [<описание>]```\n" +
				"Пример: ```~report 76561198095972970 использовал ник Какер, играли на ньюке впятером```");
	}

	@Override
	public void handle(IMessage message, String[] args) {

	}
}