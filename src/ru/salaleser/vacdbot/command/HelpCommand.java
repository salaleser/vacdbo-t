package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;

public class HelpCommand extends Command {
	private final CommandManager commandManager;

	public HelpCommand(CommandManager commandManager) {
		super("help", "`~help` - список команд; `!help <команда>` - описание");
		this.commandManager = commandManager;
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		if (args.length != 0) {
			Command command = commandManager.getCommand(args[0]);
			if (command == null) {
				message.reply("нет такой команды");
				return;
			}
			if (command.help != null) message.reply(command.help);
			else message.reply("У команды " + command.name + " нет описания");
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
