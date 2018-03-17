package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserSpeakingEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.concurrent.ThreadLocalRandom;

import static ru.salaleser.vacdbot.bot.Bot.exec;

public class ForeverAloneCommand extends Command {

	public ForeverAloneCommand() {
		super("foreveralone", MISC, "Активирует высокоинтеллектуальный диалог с ботом.");
	}

	private long started;
	private boolean enabled = false;
	private String discordid;
	private final String[] BLAH_BLAH = {
			"да", "да", "да", "да", "да", "да", "да", "да", "да", "да", "нет", "нет", "нет", "нет", "неа", "нее",
			"хорошо", "хорошо", "хорошо", "очень даже неплохо", "неплохо", "потрясающе!", "шикарно", "великолепно",
			"я с тобой согласна", "продолжай", "я слушаю", "верно", "согласна", "вот это да?!", "не уверена",
			"дело говоришь", "ни хуя себе!", "ух ты!", "не может быть!", "вот это ты меня рассмешил"};

	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length != 2 && !Util.isDiscordUser(args[1])) return;
		switch (args[0]) {
			case "started":
				if (enabled && args[1].equals(discordid)) started = System.nanoTime();
				break;
			case "ended":
				if (!enabled || !args[1].equals(discordid) || System.nanoTime() - started < 500000000L) return;
				String tts = BLAH_BLAH[ThreadLocalRandom.current().nextInt(BLAH_BLAH.length)];
				exec(guild, "tts", new String[]{tts});
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

	@EventSubscriber
	public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
		if (DBHelper.getOption(event.getGuild().getStringID(), name, "accessible").equals("0")) return;
		int connectedUsers = 0;
		IUser foreveralone = null;
		for (IUser user : event.getVoiceChannel().getConnectedUsers()) {
			if (!user.isBot()) {
				foreveralone = user;
				connectedUsers++;
			}
		}
		if (connectedUsers == 1) {
			String tts = "Привет " + foreveralone.getDisplayName(event.getGuild()) + ". Меня зовут " +
					Bot.getClient().getOurUser().getDisplayName(event.getGuild()) + ". Я вижу тебе одиноко. Давай хотя бы я с тобой пообщаюсь?";
			exec(event.getGuild(), "tts", new String[]{tts});
			exec(event.getGuild(), "foreveralone", new String[]{"on", foreveralone.getStringID()});
		} else if (connectedUsers > 1) {
			exec(event.getGuild(), "foreveralone", new String[]{"off", foreveralone.getStringID()});
		}
	}

	@EventSubscriber
	public void onUserSpeaking(UserSpeakingEvent event) {
		if (DBHelper.getOption(event.getGuild().getStringID(), name, "accessible").equals("0")) return;
		if (event.getUser().isBot()) return;
		if (event.isSpeaking()) exec(event.getGuild(), "foreveralone", new String[]{"started", discordid});
		else exec(event.getGuild(), "foreveralone", new String[]{"ended", discordid});
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР