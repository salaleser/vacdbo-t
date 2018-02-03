package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class InviteCommand extends Command {

	public InviteCommand() {
		super("invite", 1);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		final IChannel channel = message.getChannel();
		final IVoiceChannel voice = Bot.voiceChannelGeneral;
		final IRole officer = Bot.roleOfficers;

		for (IUser userHere : channel.getUsersHere()) {
			if (userHere.getPresence().getStatus() != StatusType.OFFLINE && !userHere.isBot() &&
					userHere.hasRole(officer) && !voice.getConnectedUsers().contains(userHere)) {
				IMessage inviteMessage = channel.sendMessage(userHere +
						", для вас последнее китайское приглашение в голосовой чат №" +
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("SSS")));
				TimeUnit.SECONDS.sleep(3);
				inviteMessage.delete();
				TimeUnit.SECONDS.sleep(2);
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР