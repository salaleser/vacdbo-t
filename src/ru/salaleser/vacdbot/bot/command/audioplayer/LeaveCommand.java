package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IMessage;

public class LeaveCommand extends Command {

	public LeaveCommand() {
		super("leave", 3);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		Bot.guildKTO.getConnectedVoiceChannel().leave();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР