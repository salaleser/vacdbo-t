package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashSet;

public class ReadyCommand extends Command {

	private HashSet<IUser> ready = new HashSet<>();

	public ReadyCommand() {
		super("ready", "**Описание:** Оповещает тиммейтов о готовности к матчу.\n" +
				"**Использование:** `~ready`.\n" +
				"**Предустановки:** `~ready who` — показывает уже готовых тиммейтов.\n" +
				"**Пример:** `~ready`.\n" +
				"**Примечание:** всё и так предельно ясно.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 0) {
			//<@&286563715157852180> = @Офицеры
			message.getChannel().sendMessage("<@&286563715157852180>, " +
					message.getAuthor() + " готов играть в CS:GO!\n" +
					"Уже готовы играть: " + ready);
			ready.add(message.getAuthor());
		}

		if (args.length > 0) {
			switch (args[0]) {
				case "who":
					message.getChannel().sendMessage("Готовы: " + ready);
					break;
				case "clear":
					ready.clear();
					message.getChannel().sendMessage("Список готовых обнулён");
					break;
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР