package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;
import java.util.HashMap;

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

	public void handle(IGuild guild, IMessage message) {
		String content = message.getContent();
		//проверю на особые алиасы:
		switch (content.substring(0, 1)) {
			case "=":
				content = "calc " + content.substring(1);
				break;
			case "\"":
				content = "tts " + content.substring(1);
				break;
			default:
				content = content.substring(1);
		}
		//распихать аргументы по ячейкам массива:
		String[] args = content.split(" ");
		Command command = getCommand(args[0].toLowerCase());
		if (command == null) { // FIXME: 17.11.2017 сделать исключение так как код повторяется. такой же блок в хелпе
			message.reply("команда " + Util.code(args[0]) + " не поддерживается");
			return;
		}
		Logger.info("Получил команду " + command.name + ".");

		//Проверка на право использования команды:
		int priority = Util.getPriority(message.getAuthor().getStringID());
		if (command.permissions != 0 && priority > command.permissions) {
			message.reply("Вы не обладаете достаточными правами " +
					"для использования команды " + Util.code(command.name) + "!");
			return;
		}
		//передаю управление дальше по команде:
		try {
			System.out.println(Arrays.toString(Arrays.copyOfRange(args, 1, args.length)));
			command.handle(guild, message, Arrays.copyOfRange(args, 1, args.length));
		} catch (DiscordException e) {
			Logger.error(e.getMessage());
		} catch (RateLimitException e) {
			Logger.error("RateLimitException!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР