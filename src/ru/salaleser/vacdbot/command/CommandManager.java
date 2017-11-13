package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {

	HashMap<String, Command> commands;

	public CommandManager() {
		this.commands = new HashMap<>();
	}

	public void addCommand(Command command) {
		commands.put(command.name, command);
	}

	Command getCommand(String commandsKey) {
		return commands.get(commandsKey);
	}

	public void handle(IMessage message) {
		try {
			String messageContent = message.getContent().substring(1);
			String[] args = messageContent.split(" ");
			Command command = getCommand(args[0].toLowerCase());
			command.handle(message, Arrays.copyOfRange(args, 1, args.length));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
