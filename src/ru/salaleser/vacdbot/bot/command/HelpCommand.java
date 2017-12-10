package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.CommandManager;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;

public class HelpCommand extends Command {
	private final CommandManager commandManager;

	public HelpCommand(CommandManager manager) {
		super("help", 999, "" +
				Util.b("Описание:") + " Показывает как пользоваться командами.\n" +
				Util.b("Использование:") + " `~help [<команда>]`.\n" +
				Util.b("Предустановки:") + " `~help` — покажет все доступные команды.\n" +
				Util.b("Пример:") + " `~help vac`, `~help`.\n" +
				Util.b("Примечание:") + " хелп как хелп, что, хелпа никогда не видели?");

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
			StringBuilder msg = new StringBuilder(Util.b("Поддерживаемые команды: "));
			for (Map.Entry<String, Command> entry : commandManager.commands.entrySet()) {
				msg.append("`").append(entry.getKey()).append("`").append(", ");
			}
			//удаляет лишнюю запятую и пробел в конце:
			msg.delete(msg.length() - 2, msg.length());
			message.getChannel().sendMessage(msg.toString());
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР