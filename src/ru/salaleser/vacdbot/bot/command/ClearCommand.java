package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

import java.util.concurrent.TimeUnit;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", "" +
				Util.b("Описание:") + " Удаляет сообщения ботов.\n" +
				Util.b("Использование:") + " `~clear <количество_соощений>`.\n" +
				Util.b("Предустановки:") + " `~clear`.\n" +
				Util.b("Пример:") + " `~clear`.\n" +
				Util.b("Примечание:") + " ");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length != 2) {
			message.reply(" неправильный синтаксис!");
			return;
		}
		if (!Util.isDiscordUser(args[0])) {
			message.reply(" не имя пользователя!");
			return;
		}
		if (!Util.isNumeric(args[1])) {
			message.reply(" не число!");
			return;
		}
		String userid = args[0].replaceAll("[<@!>]", "");
		IUser user = message.getClient().getUserByID(Long.parseLong(userid));
		MessageHistory history = message.getChannel().getMessageHistory(Integer.parseInt(args[1]) + 1);
		for (IMessage msg : history) {
			if (msg.getAuthor() == user) {
				msg.delete();
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР