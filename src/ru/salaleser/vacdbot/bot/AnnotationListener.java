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

import static ru.salaleser.vacdbot.bot.Bot.PREFIX;

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
		String guild = "PM"; //чтобы избежать npe надо проверить личное ли сообщение боту пишут:
		if (!event.getChannel().isPrivate()) guild = event.getGuild().getName();
		Logger.onMessage(guild + " / " + event.getChannel().getName() + " / " +
				event.getAuthor().getName() + ": " + event.getMessage().getContent());

		Snitch snitch = new Snitch();
		switch (event.getMessage().getContent().substring(0, 1)) {
			case PREFIX:
			case "=":
			case "\"":
				Bot.getCommandManager().handle(event.getMessage());
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
		if (event.getUser().isBot()) return;
		playsound(event.getUser().getStringID(), "join");
	}

	@EventSubscriber
	public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) throws InterruptedException {
		if (event.getUser().isBot()) return;
		playsound(event.getUser().getStringID(), "move");
	}

	@EventSubscriber
	public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) throws InterruptedException {
		if (event.getUser().isBot()) return;
		playsound(event.getUser().getStringID(), "leave");
	}

	private void playsound(String discordid, String event) throws InterruptedException {
		if (DBHelper.getValueFromSettings("options", "voice").equals("0")) return;
		String sound = Util.getSound(discordid, event + "sound");
		System.out.println(sound);
		try {
			if (!sound.isEmpty()) Player.queueFile("sounds/" + sound + ".mp3");
		} catch (NullPointerException e) {
			System.out.println("поймал нпе на саунде");
			sound = Util.getSound(discordid, event + "tts");
			System.out.println(sound);
			try {
				if (!sound.isEmpty()) {
					Bot.getCommandManager().getCommand("tts").handle(null, new String[]{sound});
				}
			} catch (NullPointerException e2) {
				System.out.println("поймал нпе на ттс");
				sound = Util.getSound(discordid, "name");
				System.out.println(sound);
				String text = "Ошибка! ";
				switch (event) {
					case "join":
						text = "Пришёл ";
						break;
					case "move":
						text = "Перешёл ";
						break;
					case "leave":
						text = "Ушёл ";
						break;
				}
				System.out.println(text);
				try {
					if (!sound.isEmpty()) {
						Bot.getCommandManager().getCommand("tts").handle(null, new String[]{text, sound});
					}
				} catch (NullPointerException e3) {
					System.out.println("поймал нпе на имени");
					playsound("noname", event);
				}
			}
		}
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