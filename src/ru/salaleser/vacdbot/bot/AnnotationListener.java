package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserSpeakingEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

public class AnnotationListener {

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		event.getClient().changePlayingText(Bot.status);
		Bot.guildKTO = event.getClient().getGuildByID(223560049937743872L);
		Bot.channelKTOLog = event.getClient().getChannelByID(377431980658393088L);
		Bot.channelKTOTest = event.getClient().getChannelByID(347333162449502208L);
		Bot.channelKTOGeneral = event.getClient().getChannelByID(347088817729306624L);
		Bot.channelKTOOfficers = event.getClient().getChannelByID(347088817729306624L);
		Bot.roleOfficers = event.getClient().getRoleByID(286563715157852180L);
		Bot.voiceChannelGeneral = event.getClient().getVoiceChannelByID(399878431287803905L);

		StringBuilder guildsBuilder = new StringBuilder();
		for (IGuild guild : event.getClient().getGuilds()) guildsBuilder.append(", ").append(guild.getName());
		String guilds = guildsBuilder.toString();
		guilds = guilds.substring(2);
		Bot.gui.setConnected(event.getClient(), guilds);
		Logger.info("Успешно подключен. Всего серверов — " + event.getClient().getGuilds().size() + ": " + guilds);
		new Scheduler();
		Logger.info("Планировщик запущен.");
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {
		if (event.getUser().hasRole(Bot.roleOfficers)) {
			Bot.channelKTOGeneral.sendMessage(event.getUser() + ", <:alo:346605809532141570>");
			event.getUser().moveToVoiceChannel(Bot.voiceChannelGeneral);
		}
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) throws InterruptedException {
		String guild = "PM";
		if (!event.getChannel().isPrivate()) guild = event.getGuild().getName();
		Logger.onMessage(guild + " / " + event.getChannel().getName() + " / " +
				event.getAuthor().getName() + ": " + event.getMessage().getContent());
		Snitch snitch = new Snitch();
		if (event.getMessage().getContent().startsWith("~")) {
			Bot.getCommandManager().handle(event.getMessage());
		} else if (event.getMessage().getContent().startsWith("=")) {
			//Этот код для альтернативного вызова команды "Calc",
			String messageContent = event.getMessage().getContent().substring(1);
			String[] args = messageContent.split(" ");
			Bot.getCommandManager().getCommand("calc").handle(event.getMessage(), args);
		} else {
			snitch.snitch(event.getMessage());
		}
	}

	@EventSubscriber
	public void onUserSpeaking(UserSpeakingEvent event) {

	}

	@EventSubscriber
	public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
		String enabled = DBHelper.getValueFromSettings("options", "voice");
		if (enabled.equals("1")) Player.queueFile("sounds/" + Util.getSound(event.getUser(), "joinsound") + ".mp3");
	}

	@EventSubscriber
	public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) {
		String enabled = DBHelper.getValueFromSettings("options", "voice");
		if (enabled.equals("1")) Player.queueFile("sounds/" + Util.getSound(event.getUser(), "leavesound") + ".mp3");
	}

	@EventSubscriber
	public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) {
		String enabled = DBHelper.getValueFromSettings("options", "voice");
		if (enabled.equals("1")) Player.queueFile("sounds/" + Util.getSound(event.getUser(), "leavesound") + ".mp3");
	}

	//Слушатели аудиоплеера:
	@EventSubscriber
	public void onTrackFinish(TrackFinishEvent event) {
		if (event.getPlayer().getPlaylistSize() == 0) Bot.guildKTO.getConnectedVoiceChannel().leave();
	}

	@EventSubscriber
	public void onTrackStart(TrackStartEvent event) {

	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР