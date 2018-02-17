package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class PlayCommand extends Command {

	public PlayCommand() {
		super("play", new String[]{"p"});
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) throws InterruptedException {
		if (args.length == 0) return;
		Player.join(guild);
		switch (args[0].toLowerCase()) {
			case "сн":
			case "gn":
				Player.queueFile(guild, "music/spyat_ustalye_igrushki.mp3");
				break;
			default:
				Player.queueFile(guild, args[0]);
//				Player.queueUrl(guild, args[0]);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР