package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.TimerTask;

public class InviteToVoiceChatTask extends TimerTask {

	private static int c = 0;
	private static final IChannel channel = Bot.channelKTOGeneral;
	private static final IVoiceChannel voice = Bot.voiceChannelGeneral;

	public void run() {
		c++;

		channel.sendMessage("Последнее китайское предупреждение номер " + c);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР