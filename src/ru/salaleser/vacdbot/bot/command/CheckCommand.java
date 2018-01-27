package ru.salaleser.vacdbot.bot.command;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Parser7DTDServer;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;

public class CheckCommand extends Command {

	public CheckCommand() {
		super("check");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		Parser7DTDServer parser = new Parser7DTDServer();
		Document document = null;
		try {
			document = Jsoup.connect("https://7dtd.zone/qserver.php").get();
		} catch (IOException e) {
			Logger.error(Util.i("Нет соединения с интернетом"));
		}
		if (document == null) {
			Logger.error(Util.i("Пустой ответ от сервера"));
			return;
		}
		message.getChannel().sendMessage("Игроков на сервере 7DTD.ZONE=RU#2: " +
				Util.b(parser.parse(document.text())));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР