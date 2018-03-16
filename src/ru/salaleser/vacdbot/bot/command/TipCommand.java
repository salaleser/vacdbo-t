package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Random;

public class TipCommand extends Command {

	public TipCommand() {
		super("tip", MISC, "Выдает гениальный совет по игре в ксго.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~tip`.",
				"`~tip` — случайный совет.",
				"`~tip`.",
				"советов пока мало, предлагайте свои если есть идеи."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String[][] tips = DBHelper.executeQuery("SELECT text FROM tips");
		message.getChannel().sendMessage(tips[new Random().nextInt(tips.length)][0]);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР