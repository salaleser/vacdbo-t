package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

public class PlayerCommand extends Command {

	public PlayerCommand() {
		super("player", 2);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		if (args.length == 0) return;
		switch (args[0]) {
			case "volume":
			case "vol":
			case "v":
				if (args.length > 1) {
					if (Util.isNumeric(args[1])) {
						int v = Integer.parseInt(args[1]);
						Player.volume(v);
						message.getChannel().sendMessage("Громкость установлена на " + Player.volume() + "%.");
					} else {
						Logger.error("Не число!");
					}
				} else {
					message.getChannel().sendMessage("Громкость равна " + Player.volume() + "%.");
				}
				break;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР