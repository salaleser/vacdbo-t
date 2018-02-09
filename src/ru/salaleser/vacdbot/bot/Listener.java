package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.*;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

import static ru.salaleser.vacdbot.bot.Bot.PREFIX;

public class Listener {

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		event.getClient().changePlayingText(Bot.STATUS);
		Bot.setClient(event.getClient());

		StringBuilder guildsBuilder = new StringBuilder();
		for (IGuild guild : Bot.getClient().getGuilds()) {
			Bot.addGuild(guild);
			guildsBuilder.append(", ").append(guild.getName());
		}
		String guilds = guildsBuilder.toString();
		guilds = guilds.substring(2);
		Bot.gui.setConnected(Bot.getClient(), guilds);
		Logger.info("Успешно подключен. Всего серверов — " + Bot.getClient().getGuilds().size() + ": " + guilds);
		new Scheduler();
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {

	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) throws InterruptedException {
		if (event.getAuthor().isBot()) return;
		String guild = "PM"; //чтобы избежать npe надо проверить личное ли сообщение боту пишут:
		if (!event.getChannel().isPrivate()) guild = event.getGuild().getName();
		Logger.onMessage(guild + " / " + event.getChannel().getName() + " / " +
				event.getAuthor().getName() + ": " + event.getMessage().getContent());

		Snitch snitch = new Snitch();

		// Перечень префиксов, которые CommandManager будет обрабатывать (а Snitch -- нет):
		switch (event.getMessage().getContent().substring(0, 1)) {
			case PREFIX:
			case "=":
			case "\"":
				Bot.getCommandManager().handle(event.getGuild(), event.getMessage());
				break;
			default:
				snitch.snitch(event.getMessage());
		}
	}

	@EventSubscriber
	public void onUserSpeaking(UserSpeakingEvent event) {

	}

	@EventSubscriber
	public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) throws InterruptedException {
		if (DBHelper.getValueFromSettings("options", "voice").equals("0") || event.getUser().isBot()) return;
		playSound(event);
	}

	@EventSubscriber
	public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) throws InterruptedException {
		if (DBHelper.getValueFromSettings("options", "voice").equals("0") || event.getUser().isBot()) return;
		playSound(event);
	}

	@EventSubscriber
	public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) throws InterruptedException {
		if (DBHelper.getValueFromSettings("options", "voice").equals("0") || event.getUser().isBot()) return;
		playSound(event);
	}

	private void playSound(UserVoiceChannelEvent event) throws InterruptedException {
		String sound = Util.getSound(event.getUser().getStringID(), event + "sound");
		if (sound != null) Player.queueFile(event.getGuild(), "sounds/" + sound + ".mp3");
		else playTTS(event);
	}
	private void playTTS(UserVoiceChannelEvent event) throws InterruptedException {
		String sound = Util.getSound(event.getUser().getStringID(), event + "tts");
		if (sound != null) Bot.getCommandManager().getCommand("tts").handle(event.getGuild(), null, new String[]{sound});
		else playUsername(event);
	}
	private void playUsername(UserVoiceChannelEvent event) throws InterruptedException {
		String sound;
		String discordid = event.getUser().getStringID();
		if (!discordid.equals("noname")) {
			sound = Bot.getClient().getUserByID(Long.parseLong(discordid)).getNicknameForGuild(event.getGuild());
		} else {
			sound = Util.getSound(discordid, "name");
		}
		String text = "это ";
		switch (event.getClass().getSimpleName()) {
			case "UserVoiceChannelJoinEvent": text = "Пришёл "; break;
			case "UserVoiceChannelMoveEvent": text = "Перешёл "; break;
			case "UserVoiceChannelLeaveEvent": text = "Ушёл "; break;
		}
		if (sound != null) Bot.getCommandManager().getCommand("tts").handle(event.getGuild(), null, new String[]{text, sound});
		else playSound(event);
	}

	//Слушатели аудиоплеера:
	@EventSubscriber
	public void onTrackFinish(TrackFinishEvent event) {
		if (event.getPlayer().getPlaylistSize() == 0) event.getPlayer().getGuild().getConnectedVoiceChannel().leave();
	}

	@EventSubscriber
	public void onTrackStart(TrackStartEvent event) {

	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР