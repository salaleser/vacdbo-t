package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class PlayerCommand extends Command {

	public PlayerCommand() {
		super("player", PLAYER, "Настраивает бота.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~" + name + " <метод>`.",
				"`~" + name + " volume` — показывает текущую громкость.",
				"`~" + name + " volume 50`.",
				"в разработке."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) return;
		switch (args[0]) {
			case "volume":
			case "vol":
			case "v":
				if (args.length > 1) {
					if (Util.isNumeric(args[1])) {
						int volume = Integer.parseInt(args[1]);
						Player.volume(guild, volume);
						message.getChannel().sendMessage("Громкость установлена на " + Player.volume(guild) + "%.");
					} else {
						Logger.error("Не число!");
					}
				} else {
					message.getChannel().sendMessage("Громкость равна " + Player.volume(guild) + "%.");
				}
				break;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР