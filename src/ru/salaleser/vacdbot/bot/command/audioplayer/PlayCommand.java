package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IMessage;

public class PlayCommand extends Command {

	public PlayCommand() {
		super("play", new String[]{"p"}, 3);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		if (args.length == 0) return;
		Player.join();
		switch (args[0].toLowerCase()) {
			case "сн":
			case "gn":
				Player.queueFile("music/spyat_ustalye_igrushki.mp3");
				break;
			default:
				Player.queueUrl(args[0]);
				Bot.channelKTOTest.sendMessage("{0}{1}\\n#Test.");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР