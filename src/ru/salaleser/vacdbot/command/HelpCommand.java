package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends Command {
	private final CommandManager commandManager;

	public HelpCommand(CommandManager manager) {
		super("help", "`~help` - список команд; `!help <команда>` - описание");

		commandManager = manager;
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length != 0) {
			Command command = commandManager.getCommand(args[0]);
			if (command == null) {
				message.reply("команда `" + message.getContent() + "` не поддерживается");
				return;
			}
			if (command.help != null) message.getChannel().sendMessage(command.help);
			else message.reply("у команды " + command.name + " нет описания");
		} else {
			StringBuilder msg = new StringBuilder("Поддерживаемые команды: ");
			for (Map.Entry<String, Command> entry : commandManager.commands.entrySet()) {
				msg.append("`").append(entry.getKey()).append("`").append(", ");
			}
			msg.delete(msg.length() - 2, msg.length());
			msg.append("\nИспользование: ```~help [<команда>]```");
			message.getChannel().sendMessage(msg.toString());
		}
	}
}
