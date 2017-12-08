package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InviteToVoiceChatTask extends TimerTask {

	private static final IChannel channel = Bot.channelKTOGeneral;
	private static final IVoiceChannel voice = Bot.voiceChannelGeneral;
	private static final IRole officer = Bot.roleOfficers;

	public void run() {
		for (IUser userHere : channel.getUsersHere()) {
			if (userHere.getPresence().getStatus() != StatusType.OFFLINE && !userHere.isBot() &&
					userHere.hasRole(officer) && !voice.getConnectedUsers().contains(userHere)) {
				IMessage message = channel.sendMessage(userHere +
						", для вас последнее китайское приглашение в голосовой чат №" +
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("SSS")));
				try {
					TimeUnit.SECONDS.sleep(3);
					message.delete();
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР