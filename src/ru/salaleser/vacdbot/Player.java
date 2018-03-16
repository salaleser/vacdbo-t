package ru.salaleser.vacdbot;

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

	public static void join(IGuild guild) throws RateLimitException, DiscordException, MissingPermissionsException {
		if (guild.getVoiceChannels() == null) return;
		IVoiceChannel mainVoiceChannel = guild.getVoiceChannels().get(0);
		for (IVoiceChannel voiceChannel : guild.getVoiceChannels()) {
			if (voiceChannel.getConnectedUsers().size() > mainVoiceChannel.getConnectedUsers().size()) {
				mainVoiceChannel = voiceChannel;
			}
		}
		if (mainVoiceChannel.isConnected()) return;
		if (!mainVoiceChannel.getModifiedPermissions(mainVoiceChannel.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
			Logger.error("I can't join that voice channel!", guild);
		else if (mainVoiceChannel.getUserLimit() != 0 && mainVoiceChannel.getConnectedUsers().size() >= mainVoiceChannel.getUserLimit())
			Logger.error("That room is full!", guild);
		else {
			mainVoiceChannel.join();
			Logger.info("Connected to " + mainVoiceChannel.getGuild().getName() + ":" + mainVoiceChannel.getName() + ".", guild);
		}
	}

	public static void queueUrl(IGuild guild, String url) throws RateLimitException, DiscordException, MissingPermissionsException {
		try {
			URL u = new URL(url);
			join(guild);
			setTrackTitle(getPlayer(guild).queue(u), u.getFile());
		} catch (MalformedURLException e) {
			Logger.error("That URL is invalid!", guild);
		} catch (IOException e) {
			Logger.error("An IO exception occured: " + e.getMessage(), guild);
		} catch (UnsupportedAudioFileException e) {
			Logger.error("That type of file is not supported!", guild);
		}
	}

	public static void queueFile(IGuild guild, String pathname) throws RateLimitException, DiscordException, MissingPermissionsException {
		File file = new File(pathname);
		if (!file.exists())
			Logger.error("That file doesn't exist!", guild);
		else if (!file.canRead())
			Logger.error("I don't have access to that file!", guild);
		else {
			try {
				join(guild);
				setTrackTitle(getPlayer(guild).queue(file), file.toString());
			} catch (IOException e) {
				Logger.error("An IO exception occured: " + e.getMessage(), guild);
			} catch (UnsupportedAudioFileException e) {
				Logger.error("That type of file is not supported!", guild);
			}
		}
	}

	public static void stop(IGuild guild) {
		getPlayer(guild).clear();
	}

	public static void pause(IGuild guild) {
		getPlayer(guild).togglePause();
	}

	public static void skip(IGuild guild) {
		getPlayer(guild).skip();
	}

	public static int volume(IGuild guild) throws RateLimitException, DiscordException, MissingPermissionsException {
		int volume = (int) (getPlayer(guild).getVolume() * 100);
		Logger.info("Volume is " + (volume * 100) + "%.", guild);
		return volume;
	}

	public static void volume(IGuild guild, int percent) throws RateLimitException, DiscordException, MissingPermissionsException {
		float volume = (float) (percent) / 100;
		if (volume > 1.5) volume = 1.5f;
		if (volume < 0) volume = 0f;
		getPlayer(guild).setVolume(volume);
		Logger.info("Set volume to " + (int) (volume * 100) + "%.", guild);
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