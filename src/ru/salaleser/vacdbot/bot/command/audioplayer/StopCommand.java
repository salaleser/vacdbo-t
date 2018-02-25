package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop", PLAYER, "Останавливает трек и очищает очередь воспроизведения.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		Player.stop(guild);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР