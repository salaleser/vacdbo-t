package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;

public class QuitCommand extends Command {

	public QuitCommand() {
		super("quit", "**Описание:** Выключает бота.\n" +
				"**Использование:** `~quit`.\n" +
				"**Предустановки:** нет.\n" +
				"**Пример:** `~quit`.\n" +
				"**Примечание:** команда для отладки, не используйте её.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		System.exit(0);
	}
}