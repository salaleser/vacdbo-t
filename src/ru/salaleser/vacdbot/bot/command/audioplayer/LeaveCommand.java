package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class LeaveCommand extends Command {

	public LeaveCommand() {
		super("leave", PLAYER, "Отключает бота из голосового канала.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		guild.getConnectedVoiceChannel().leave();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР