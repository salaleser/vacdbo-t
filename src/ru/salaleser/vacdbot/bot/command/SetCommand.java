package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;

public class SetCommand extends Command {

	public SetCommand() {
		super("set", "Установка параметров.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		Command command = Bot.getCommandManager().commands.get(args[0]);
		if (command.set(Arrays.copyOfRange(args, 1, args.length))) {
			message.reply("параметр установлен успешно.");
		} else {
			message.reply("параметр не установлен.");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР