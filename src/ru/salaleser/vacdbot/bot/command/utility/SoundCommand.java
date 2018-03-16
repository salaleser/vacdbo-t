package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Player;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.HashMap;

import static ru.salaleser.vacdbot.Config.DISCORDID;
import static ru.salaleser.vacdbot.Util.*;
import static ru.salaleser.vacdbot.bot.Bot.SALALESER;
import static ru.salaleser.vacdbot.bot.Bot.exec;

public class SoundCommand extends Command {

	public SoundCommand() {
		super("sound", UTILITY, "Озвучивает подключение|отключение пользователя в голосовой канал.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~sound [<@User>|<Discord_ID>|<SteamID64>|<CommunityURL>] [join|leave|move] <текст_ту_спич>`.",
				"`~sound [<@User>|<Discord_ID>|<SteamID64>|<CommunityURL>]` — показывает звуки  пользователя;\n" +
						"`~sound [<@User>|<Discord_ID>|<SteamID64>|<CommunityURL>] [join|leave|move]` — удаляет текст.",
				"`~sound @salaleser leave Вышел суперчел`, `~sound @pchelka join Залетела Пчёлка`.\n" +
						b("Подробное описание:") + " при наступлении одного из событий: подключение или отключение " +
						"от голосового канала или переключение в другой голосовой канал ставит в очередь на воспроизведение " +
						"аудиофайл, если файл с этим событием не ассоциирован, то синтезирует текст, если и текст не найден, " +
						"то синтезирует стандартный текст вида \"Пришёл|Ушёл|Перешёл <отображаемое_имя>\".",
				"приоритет: аудиофайл -> пользовательский текст -> стандартный текст.\n" +
						"Чтобы установить свой аудиофайл (эмпэтришку) обратитесь ко мне (Лёха <@!" + SALALESER + ">)."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String table = "sounds";

		if (args.length == 0) {
			message.reply("слишком мало аргументов!");
			return;
		}

		HashMap<String, String> argsMap = getArgs(guild, args);
		String discordid = argsMap.get(DISCORDID);
		if (discordid == null) {
			message.reply("невозможно идентифицировать пользователя!");
			return;
		}
		IUser user = guild.getUserByID(Long.parseLong(discordid));
		//дело было ночью и я устал, поэтому не смог понять почему DBHelper.executeQuery("SELECT * FROM " + table + " WHERE guildid = '" +
		//guild.getStringID() + "' AND discordid = '" + discordid + "'") возвращает null fixme 10.03.18 4:44
		if (args.length == 1) {
			message.getChannel().sendMessage(
					ub(user.getName()) + " " + i("(" + user.getDisplayName(guild) + ")") + "\n" +
							"Текст подключения к голосовому каналу: " +
							b(DBHelper.executeQuery("SELECT join_tts FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0]) + "\n" +
							"Текст отключения от голосового канала: " +
							b(DBHelper.executeQuery("SELECT leave_tts FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0]) + "\n" +
							"Текст переключения в другой голосовой канал: " +
							b(DBHelper.executeQuery("SELECT move_tts FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0]) + "\n" +
							"Аудиофайл подключения к голосовому каналу: " +
							b(DBHelper.executeQuery("SELECT join_file FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0]) + "\n" +
							"Аудиофайл отключения от голосового канала: " +
							b(DBHelper.executeQuery("SELECT leave_file FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0]) + "\n" +
							"Аудиофайл переключения в другой голосовой канал: " +
							b(DBHelper.executeQuery("SELECT move_file FROM " + table + " WHERE guildid = '" +
									guild.getStringID() + "' AND discordid = '" + discordid + "'")[0][0])
			);
			return;
		}

		String tts = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if (tts.equals("")) tts = null;
		if (!args[1].matches("^join$|^leave$|^move$")) {
				message.reply("второй аргумент должен быть \"join\", \"leave\" или \"move\"!");
				return;
		}

		String query = "SELECT discordid FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' AND discordid = '" + discordid + "'";
		if (DBHelper.executeQuery(query)[0][0] == null) {
			DBHelper.insert(table, new String[]{guild.getStringID(), discordid});
		}
		String updateQuery = "UPDATE " + table + " SET " + args[1] + "_tts = ? " +
				"WHERE guildid = '" + guild.getStringID() + "' AND discordid = '" + discordid + "'";
		if (DBHelper.commit(table, updateQuery, new String[]{tts})) {
			if (tts == null) message.reply("текст успешно удалён у пользователя " + b(user.getName()) + ".");
			else message.reply("текст " + b(tts) + " успешно установлен пользователю " + b(user.getName()) + ".");
			return;
		}
		message.reply("звук не установлен.");
	}

	@EventSubscriber
	public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
		if (!event.getUser().isBot()) playSound(event);
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
		if (DBHelper.getOption(event.getGuild().getStringID(), name, "level").equals("0")) return;
		String eventName = event.getClass().getSimpleName();
		String eventString = eventName.replace("UserVoiceChannel", "");
		eventString = eventString.replace("Event", "");
		String sound = getSound(event.getUser().getStringID(), eventString + "_file");
		if (sound == null) playTTS(event);
		else Player.queueFile(event.getGuild(), "sounds/" + sound + ".mp3");
	}

	private void playTTS(UserVoiceChannelEvent event) {
		String eventName = event.getClass().getSimpleName();
		String eventString = eventName.replace("UserVoiceChannel", "");
		eventString = eventString.replace("Event", "");
		String sound = getSound(event.getUser().getStringID(), eventString + "_tts");
		if (sound == null) playUsername(event);
		else exec(event.getGuild(), "tts", new String[]{sound});
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
		exec(event.getGuild(), "tts", new String[]{text, sound});
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР