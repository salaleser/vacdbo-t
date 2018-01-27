package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Player;
import sx.blah.discord.handle.obj.IMessage;

public class PlayCommand extends Command {

	public PlayCommand() {
		super("play", new String[]{"p"});
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		if (args.length == 0) return;
		Player.join();
		switch (args[0].toLowerCase()) {
			case "сн":
			case "gn":
				message.getChannel().sendMessage("Здесь будет " +
						"проигрываться музыка из телепередачи \"Спокойной ночи, малыши!\"");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР