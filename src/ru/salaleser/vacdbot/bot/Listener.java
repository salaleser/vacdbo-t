package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.*;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
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
	public void onMessage(MessageReceivedEvent event) {
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
			case "$":
			case "\"":
				Bot.getCommandManager().handle(event.getGuild(), event.getMessage());
				break;
			default:
				snitch.snitch(event.getMessage());
		}
	}

	@EventSubscriber
	public void onUserSpeaking(UserSpeakingEvent event) {
		if (event.getUser().isBot()) return;
		String id = event.getUser().getStringID();
		if (event.isSpeaking()) Bot.exec(event.getGuild(), "foreveralone", new String[]{"started", id});
		else Bot.exec(event.getGuild(), "foreveralone", new String[]{"ended", id});
	}

	@EventSubscriber
	public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
		if (event.getUser().isBot()) return;
		// TODO: 17.02.2018 перенести в соответствующие классы
		int connectedUsers = 0;
		for (IUser user : event.getVoiceChannel().getConnectedUsers()) if (!user.isBot()) connectedUsers++;
		if (connectedUsers == 1) {
			String tts = "Привет " + event.getUser().getDisplayName(event.getGuild()) + ". " +
					"Меня зовут Виталина. Я вижу тебе одиноко. Давай хотя бы я с тобой пообщаюсь?";
			Bot.exec(event.getGuild(), "tts", new String[]{tts});
			Bot.exec(event.getGuild(), "foreveralone", new String[]{"on", event.getUser().getStringID()});
		} else if (connectedUsers > 1) {
			Bot.exec(event.getGuild(), "foreveralone", new String[]{"off", event.getUser().getStringID()});
		}
		if (connectedUsers > 1) playSound(event);
	}

	@EventSubscriber
	public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) {
		if (!event.getUser().isBot()) playSound(event);
	}

	@EventSubscriber
	public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) {
		if (!event.getUser().isBot()) playSound(event);
	}

	private void playSound(UserVoiceChannelEvent event) {
		String eventName = event.getClass().getSimpleName();
		String eventString = eventName.replace("UserVoiceChannel", "");
		eventString = eventString.replace("Event", "");
		String sound = Util.getSound(event.getUser().getStringID(), eventString + "sound");
		if (sound == null) playTTS(event);
		else Player.queueFile(event.getGuild(), "sounds/" + sound + ".mp3");
	}
	private void playTTS(UserVoiceChannelEvent event) {
		String eventName = event.getClass().getSimpleName();
		String eventString = eventName.replace("UserVoiceChannel", "");
		eventString = eventString.replace("Event", "");
		String sound = Util.getSound(event.getUser().getStringID(), eventString + "tts");
		if (sound == null) playUsername(event);
		else Bot.getCommandManager().getCommand("tts").handle(event.getGuild(), null, new String[]{sound});
	}
	private void playUsername(UserVoiceChannelEvent event) {
		String sound = event.getUser().getDisplayName(event.getGuild());
		String text = "это ";
		//проверка на окончание для баб:
		String sql = "SELECT sex FROM users WHERE discordid = '" + event.getUser().getStringID() + "'";
		String sex = DBHelper.executeQuery(sql)[0][0];
		String ending = "ёл"; //по-умолчанию мужской род
		//если пол не указан:
		if (sex == null) sex = "M";
		//меняю окончание в зависимости от пола:
		if (sex.equals("W") || sex.equals("F")) ending = "ла";
		else if (sex.equals("N")) ending = "ло";
		switch (event.getClass().getSimpleName()) {
			case "UserVoiceChannelLeaveEvent": text = "Уш" + ending; break;
			case "UserVoiceChannelJoinEvent": text = "Приш" + ending; break;
			case "UserVoiceChannelMoveEvent": text = "Переш" + ending; break;
		}
		if (sound == null) sound = "незнакомец";
		Bot.getCommandManager().getCommand("tts").handle(event.getGuild(), null, new String[]{text, sound});
	}

	//Слушатели аудиоплеера:
	@EventSubscriber
	public void onTrackFinish(TrackFinishEvent event) {
		if (DBHelper.getOption(event.getPlayer().getGuild().getStringID(), "tts", "autoleave").equals("1") &&
				event.getPlayer().getPlaylistSize() == 0) event.getPlayer().getGuild().getConnectedVoiceChannel().leave();
	}

	@EventSubscriber
	public void onTrackStart(TrackStartEvent event) {

	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР