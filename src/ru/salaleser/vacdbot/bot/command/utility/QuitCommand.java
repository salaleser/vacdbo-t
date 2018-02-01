package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IMessage;

public class QuitCommand extends Command {

	public QuitCommand() {
		super("quit", new String[]{"exit"}, 1);
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Выключает бота.",
				"`~quit`.",
				"нет.",
				"`~quit`.",
				"команда для отладки, не используйте её."
				)
		);
	}

	@Override
	public void handle(IMessage message, String[] args) {
		System.exit(0);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР