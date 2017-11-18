package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	public final String name;
	final String help;

	Command(String name, String help) {
		this.name = name;
		this.help = help;
	}

	public abstract void handle(IMessage message, String[] args) throws InterruptedException;
}
