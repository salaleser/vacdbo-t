package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Player {

	private static IVoiceChannel voiceChannel = Bot.voiceChannelGeneral;
	private static IGuild guild = Bot.guildKTO;

	public static void join() throws RateLimitException, DiscordException, MissingPermissionsException {
		if (!voiceChannel.getModifiedPermissions(voiceChannel.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
			Logger.error("I can't join that voice channel!");
		else if (voiceChannel.getUserLimit() != 0 && voiceChannel.getConnectedUsers().size() >= voiceChannel.getUserLimit())
			Logger.error("That room is full!");
		else {
			voiceChannel.join();
			Logger.info("Connected to " + voiceChannel.getName() + ".");
		}
	}

	public static void queueUrl(String url) throws RateLimitException, DiscordException, MissingPermissionsException {
		try {
			URL u = new URL(url);
			join();
			setTrackTitle(getPlayer(guild).queue(u), u.getFile());
		} catch (MalformedURLException e) {
			Logger.error("That URL is invalid!");
		} catch (IOException e) {
			Logger.error("An IO exception occured: " + e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			Logger.error("That type of file is not supported!");
		}
	}

	public static void queueFile(String file) throws RateLimitException, DiscordException, MissingPermissionsException {
		File f = new File(file);
		if (!f.exists())
			Logger.error("That file doesn't exist!");
		else if (!f.canRead())
			Logger.error("I don't have access to that file!");
		else {
			try {
				join();
				setTrackTitle(getPlayer(guild).queue(f), f.toString());
			} catch (IOException e) {
				Logger.error("An IO exception occured: " + e.getMessage());
			} catch (UnsupportedAudioFileException e) {
				Logger.error("That type of file is not supported!");
			}
		}
	}

	public static void stop() {
		getPlayer(guild).clear();
	}

	public static void pause() {
		getPlayer(guild).togglePause();
	}

	public static void skip() {
		getPlayer(guild).skip();
	}

	public static int volume() throws RateLimitException, DiscordException, MissingPermissionsException {
		int volume = (int) (getPlayer(guild).getVolume() * 100);
		Logger.info("Volume is " + (volume * 100) + "%.");
		return volume;
	}

	public static void volume(int percent) throws RateLimitException, DiscordException, MissingPermissionsException {
		volume((float) (percent) / 100);
	}

	public static void volume(Float vol) throws RateLimitException, DiscordException, MissingPermissionsException {
		if (vol > 1.5) vol = 1.5f;
		if (vol < 0) vol = 0f;
		getPlayer(guild).setVolume(vol);
		Logger.info("Set volume to " + (int) (vol * 100) + "%.");
	}

	private static AudioPlayer getPlayer(IGuild guild) {
		return AudioPlayer.getAudioPlayerForGuild(guild);
	}

	public static String getTrackTitle(AudioPlayer.Track track) {
		return track.getMetadata().containsKey("title") ? String.valueOf(track.getMetadata().get("title")) : "Unknown Track";
	}

	private static void setTrackTitle(AudioPlayer.Track track, String title) {
		track.getMetadata().put("title", title);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР