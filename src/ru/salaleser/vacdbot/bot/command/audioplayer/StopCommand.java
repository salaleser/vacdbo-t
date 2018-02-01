package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IMessage;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop", 3);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		Player.stop();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР