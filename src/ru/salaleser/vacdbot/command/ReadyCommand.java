package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

public class ReadyCommand extends Command {

	public ReadyCommand() {
		super("ready", "оповещает тиммейтов о готовности к матчу");
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		message.getChannel().sendMessage(message.getGuild().getRolesByName("КТО-С").get(0) + ", " + message.getGuild().getRolesByName("КТО-О").get(0) + ", " + message.getGuild().getRolesByName("КТО-Ж").get(0) + ", " + message.getGuild().getRolesByName("КТО-З").get(0) + ", " + message.getGuild().getRolesByName("КТО-Ф").get(0) + "!\n" + message.getAuthor() + " готов играть в CS:GO");
	}
}
