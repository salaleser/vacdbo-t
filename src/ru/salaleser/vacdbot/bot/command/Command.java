package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	public static int count = 0;
	public final String name;
	final String help;

	Command(String name, String help) {
		this.name = name;
		this.help = help;
		count++;
	}

	public abstract void handle(IMessage message, String[] args) throws InterruptedException;
}