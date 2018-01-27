package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.CommandManager;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;

public class HelpCommand extends Command {
	private final CommandManager commandManager;

	public HelpCommand(CommandManager manager) {
		super("help", new String[]{"?"});
		commandManager = manager;
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Показывает как пользоваться командами.",
				"`~help [<команда>]`.",
				"`~help` — покажет все доступные команды.",
				"`~help vac`, `~help`.",
				"хелп как хелп, что, хелпа никогда не видели?"
				)
		);
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length != 0) {
			Command command = commandManager.getCommand(args[0]);
			if (command == null) {// FIXME: 17.11.2017 такой же блок в менеджере команд
				message.reply("команда " + Util.code(args[0]) + " не поддерживается");
				return;
			}
			command.help(message);
		} else {
			int priority = Util.getPriority(message.getAuthor().getStringID());
			StringBuilder msg = new StringBuilder(Util.b("Ваш уровень доступа " + Util.u(String.valueOf(priority)) +
					", доступные команды: "));
			for (Map.Entry<String, Command> entry : commandManager.commands.entrySet()) {
				if (entry.getValue().permissions == 0 || priority <= entry.getValue().permissions) {
					msg.append(Util.code(entry.getKey())).append(", ");
				}
			}
			//удаляет лишнюю запятую и пробел в конце:
			msg.delete(msg.length() - 2, msg.length());
			msg.append("\n").append("Префикс бота — ").append(Util.code(Bot.PREFIX));
			msg.append(". Пример использования: ").append(Util.code("~help")).append(".");
			message.getChannel().sendMessage(msg.toString());
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР