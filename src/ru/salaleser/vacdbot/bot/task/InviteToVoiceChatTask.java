package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.impl.obj.Presence;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InviteToVoiceChatTask extends TimerTask {

	private static int c = 0;
	private static final IChannel channel = Bot.channelKTOGeneral;
	private static final IVoiceChannel voice = Bot.voiceChannelGeneral;

	public void run() {
		c++;
		for (IUser userHere : channel.getUsersHere()) {
			if (userHere.getPresence().getStatus() != StatusType.OFFLINE || !userHere.isBot() ||
					userHere.hasRole(Bot.roleOfficers) || !voice.getConnectedUsers().contains(userHere)) {
				channel.sendMessage(userHere +
						", для вас последнее китайское приглашение в голосовой чат номер " + c);
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (c > 3) userHere.moveToVoiceChannel(voice);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР