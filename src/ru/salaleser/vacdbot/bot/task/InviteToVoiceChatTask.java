package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.*;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InviteToVoiceChatTask extends TimerTask {

	private static int c = 0;
	private static final IChannel channel = Bot.channelKTOGeneral;
	private static final IVoiceChannel voice = Bot.voiceChannelGeneral;
	private static final IRole officer = Bot.roleOfficers;

	public void run() {
		c++;
		for (IUser userHere : channel.getUsersHere()) {
			if (userHere.getPresence().getStatus() != StatusType.OFFLINE && !userHere.isBot() &&
					userHere.hasRole(officer) && !voice.getConnectedUsers().contains(userHere)) {
				channel.sendMessage(userHere +
						", для вас последнее китайское приглашение в голосовой чат №" + c);
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР