package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {

	private final Bot bot;
	HashMap<String, Command> commands;

	public CommandManager(Bot bot) {
		this.bot = bot;
		this.commands = new HashMap<>();
	}

	public void addCommand(Command command) {
		commands.put(command.name, command);
	}

	public Command getCommand(String commandName) {
		return commands.get(commandName);
	}

	public void handle(IMessage message) {
		try {
			String messageContent = message.getContent().substring(1).toLowerCase();
			String[] args = messageContent.split(" ");
			if (args.length == 0) return;
			Command command = getCommand(args[0]);
			command.handle(message, Arrays.copyOfRange(args, 1, args.length));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
