package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

import java.util.concurrent.ThreadLocalRandom;

public class TipCommand extends Command {

	private static final String[] TIPS = {
			"Не стреляй на ходу с калаша",
			"Не покупай петуха"};

	public TipCommand() {
		super("tip");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		message.getChannel().sendMessage(TIPS[ThreadLocalRandom.current().nextInt(TIPS.length)]);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР