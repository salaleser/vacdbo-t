package ru.salaleser.vacdbot.bot.command.admin;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InviteCommand extends Command {

	public InviteCommand() {
		super("invite", ADMIN, "Приглашает пользователей в голосовой канал.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		final IChannel channel = message.getChannel();
		final IVoiceChannel voice = Bot.getGuilds().get(0).getVoiceChannelsByName("General").get(0); //fixme hardcode
		final IRole officer = Bot.getGuilds().get(0).getRolesByName("Офицеры").get(0); //fixme hardcode

		for (IUser userHere : channel.getUsersHere()) {
			if (userHere.getPresence().getStatus() != StatusType.OFFLINE && !userHere.isBot() &&
					userHere.hasRole(officer) && !voice.getConnectedUsers().contains(userHere)) {
				IMessage inviteMessage = channel.sendMessage(userHere +
						", для вас последнее китайское приглашение в голосовой чат №" +
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("SSS")));
				Util.delay(3000);
				inviteMessage.delete();
				Util.delay(2000);
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР