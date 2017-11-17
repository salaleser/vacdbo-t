package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.CommandManager;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;

public class HelpCommand extends Command {
	private final CommandManager commandManager;

	public HelpCommand(CommandManager manager) {
		super("help", "**Описание:** Показывает как пользоваться командами.\n" +
				"**Использование:** `~help [<команда>]`.\n" +
				"**Предустановки:** `~help` — покажет все доступные команды.\n" +
				"**Пример:** `~help vac`, `~help`.\n" +
				"**Примечание:** хелп как хелп, что, хелпа никогда не видели?");

		commandManager = manager;
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length != 0) {
			Command command = commandManager.getCommand(args[0]);
			if (command == null) {// FIXME: 17.11.2017 такой же блок в менеджере команд
				message.reply("команда `" + args[0] + "` не поддерживается");
				return;
			}
			if (command.help != null) message.getChannel().sendMessage(command.help);
			else message.reply("у команды " + command.name + " нет описания");
		} else {
			StringBuilder msg = new StringBuilder("**Поддерживаемые команды:** ");
			for (Map.Entry<String, Command> entry : commandManager.commands.entrySet()) {
				msg.append("`").append(entry.getKey()).append("`").append(", ");
			}
			//удаляет лишнюю запятую и пробел в конце:
			msg.delete(msg.length() - 2, msg.length());
			message.getChannel().sendMessage(msg.toString());
		}
	}
}
