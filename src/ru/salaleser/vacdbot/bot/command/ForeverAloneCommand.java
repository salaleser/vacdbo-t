package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ForeverAloneCommand extends Command {

	public ForeverAloneCommand() {
		super("foreveralone");
	}

	private long started;
	private boolean enabled = false;
	private String discordid;
	private final String[] BLAH_BLAH = {
			"да", "да", "да", "да", "да", "да", "да", "да", "да", "да", "нет", "нет", "нет", "нет", "неа", "нее",
			"хорошо", "хорошо", "хорошо", "очень даже неплохо", "неплохо", "потрясающе!", "шикарно", "великолепно",
			"я с тобой согласна", "продолжай", "я слушаю", "верно", "согласна", "вот это да?!", "не уверена",
			"дело говоришь", "ни хуя себе!", "ух ты!", "не может быть!", "вот это ты меня рассмешил"};

	public void handle(IGuild guild, IMessage message, String[] args) throws InterruptedException {
		if (args.length != 2 && !Util.isDiscordUser(args[1])) return;
		switch (args[0]) {
			case "started":
				if (enabled && args[1].equals(discordid)) started = System.nanoTime();
				break;
			case "ended":
				if (!enabled || !args[1].equals(discordid) || System.nanoTime() - started < 500000000L) return;
				String tts = BLAH_BLAH[ThreadLocalRandom.current().nextInt(BLAH_BLAH.length)];
				Bot.exec(guild, "tts", new String[]{tts});
				started = 0;
				break;
			case "on":
				enabled = true;
				discordid = args[1];
				break;
			case "off":
				enabled = false;
				break;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР