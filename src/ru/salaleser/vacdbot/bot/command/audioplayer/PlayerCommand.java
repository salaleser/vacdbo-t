package ru.salaleser.vacdbot.bot.command.audioplayer;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

public class PlayerCommand extends Command {

	public PlayerCommand() {
		super("player", PLAYER, "Меняет настройки плееру бота.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~player <метод>`.",
				"`~player volume` — показывает текущую громкость.",
				"`~player volume 50`.",
				"в разработке."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) return;
		switch (args[0]) {
			case "volume":
			case "vol":
			case "v":
				if (args.length > 1) {
					if (Util.isNumeric(args[1])) {
						int volume = Integer.parseInt(args[1]);
						Player.volume(guild, volume);
						message.getChannel().sendMessage("Громкость установлена на " + Player.volume(guild) + "%.");
					} else {
						message.reply("не число!");
					}
				} else {
					message.getChannel().sendMessage("Громкость равна " + Player.volume(guild) + "%.");
				}
				break;
		}
	}

	@EventSubscriber
	public void onTrackFinish(TrackFinishEvent event) {
		if (DBHelper.getOption(event.getPlayer().getGuild().getStringID(), "tts", "autoleave").equals("1") &&
				event.getPlayer().getPlaylistSize() == 0) event.getPlayer().getGuild().getConnectedVoiceChannel().leave();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР