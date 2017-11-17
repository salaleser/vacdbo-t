package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

public class ReadyCommand extends Command {

	public ReadyCommand() {
		super("ready", "**Описание:** Оповещает тиммейтов о готовности к матчу.\n" +
				"**Использование:** `~ready`.\n" +
				"**Предустановки:** нет.\n" +
				"**Пример:** `~ready`.\n" +
				"**Примечание:** всё и так предельно ясно.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		message.getChannel().sendMessage(message.getGuild().getRolesByName("КТО-С").get(0) + ", " +
				message.getGuild().getRolesByName("КТО-О").get(0) + ", " +
				message.getGuild().getRolesByName("КТО-Ж").get(0) + ", " +
				message.getGuild().getRolesByName("КТО-З").get(0) + ", " +
				message.getGuild().getRolesByName("КТО-Ф").get(0) + "!\n" +
				message.getAuthor() + " готов играть в CS:GO");
	}
}
