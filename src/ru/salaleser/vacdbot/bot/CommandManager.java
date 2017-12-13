package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

	public HashMap<String, Command> commands; // fixme это не должно быть паблик!

	CommandManager() {
		this.commands = new HashMap<>();
	}

	void addCommand(Command command) {
		commands.put(command.name, command);
		for (String alias : command.aliases) commands.put(alias, command);
	}

	public Command getCommand(String commandsKey) {
		return commands.get(commandsKey);
	}

	public void handle(IMessage message) {
		String content = message.getContent();
		//если нет команды, покажу хелп хотя бы:
		if (content.equals("~")) content = "~help";
		//если есть, то распихать аргументы по ячейкам массива:
		content = content.substring(1);
		String[] args = content.split(" ");
		Command command = getCommand(args[0].toLowerCase());
		if (command == null) { // FIXME: 17.11.2017 сделать исключение так как код повторяется. такой же блок в хелпе
			message.reply("команда `" + args[0] + "` не поддерживается");
			return;
		}
		Logger.info("Получил команду " + command.name + ".");

		int priority = Util.getPriority(message.getAuthor().getStringID());
		if (command.permissions != 0 && priority > command.permissions) {
			message.reply("Вы не обладаете достаточными правами для использования " + command.name + "!");
			return;
		}
		//передаю управление дальше по команде:
		try {
			command.handle(message, Arrays.copyOfRange(args, 1, args.length));
		} catch (DiscordException e) {
			Logger.error(e.getMessage());
		} catch (RateLimitException e) {
			Logger.error("RateLimitException отловлен!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР