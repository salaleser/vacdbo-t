package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
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
		System.out.println(guild.getName());//fixme
		if (guild.getVoiceChannels() == null) return;
		IVoiceChannel mainVoiceChannel = guild.getVoiceChannels().get(0);
		for (IVoiceChannel voiceChannel : guild.getVoiceChannels()) {
			if (voiceChannel.getConnectedUsers().size() > mainVoiceChannel.getConnectedUsers().size()) {
				mainVoiceChannel = voiceChannel;
			}
		}
		if (!mainVoiceChannel.getModifiedPermissions(mainVoiceChannel.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
			Logger.error("I can't join that voice channel!");
		else if (mainVoiceChannel.getUserLimit() != 0 && mainVoiceChannel.getConnectedUsers().size() >= mainVoiceChannel.getUserLimit())
			Logger.error("That room is full!");
		else {
			mainVoiceChannel.join();
			Logger.info("Connected to " + mainVoiceChannel.getGuild().getName() + ":" + mainVoiceChannel.getName() + ".");
		}
	}

	public static void queueUrl(IGuild guild, String url) throws RateLimitException, DiscordException, MissingPermissionsException {
		try {
			URL u = new URL(url);
			join(guild);
			setTrackTitle(getPlayer(guild).queue(u), u.getFile());
		} catch (MalformedURLException e) {
			Logger.error("That URL is invalid!");
		} catch (IOException e) {
			Logger.error("An IO exception occured: " + e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			Logger.error("That type of file is not supported!");
		}
	}

	public static void queueFile(IGuild guild, String file) throws RateLimitException, DiscordException, MissingPermissionsException {
		File f = new File(file);
		if (!f.exists())
			Logger.error("That file doesn't exist!");
		else if (!f.canRead())
			Logger.error("I don't have access to that file!");
		else {
			try {
				join(guild);
				setTrackTitle(getPlayer(guild).queue(f), f.toString());
			} catch (IOException e) {
				Logger.error("An IO exception occured: " + e.getMessage());
			} catch (UnsupportedAudioFileException e) {
				Logger.error("That type of file is not supported!");
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
		Logger.info("Volume is " + (volume * 100) + "%.");
		return volume;
	}

	public static void volume(int percent) throws RateLimitException, DiscordException, MissingPermissionsException {
		volume((float) (percent) / 100);
	}

	private static void volume(Float vol) throws RateLimitException, DiscordException, MissingPermissionsException {
		if (vol > 1.5) vol = 1.5f;
		if (vol < 0) vol = 0f;
//		getPlayer(guild).setVolume(vol);
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