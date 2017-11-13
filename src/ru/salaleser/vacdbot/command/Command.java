package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	final String name;
	final String help;

	Command(String name, String help) {
		this.name = name;
		this.help = help;
	}

	public abstract void handle(IMessage message, String[] args) throws Exception;

}
