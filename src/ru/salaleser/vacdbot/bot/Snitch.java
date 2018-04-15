package ru.salaleser.vacdbot.bot;

import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RoleBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static ru.salaleser.vacdbot.Config.*;
import static ru.salaleser.vacdbot.Util.*;
import static ru.salaleser.vacdbot.bot.Bot.*;

class Snitch {

	private IMessage message;

	private void snitch() { //fixme исправить это барахло
		String content = message.getContent().toLowerCase();

		// ПРОВЕРКА НА ОШИБКИ
		HashMap<String, String> dictionary = new HashMap<>();
		dictionary.put("пошол", "пош__ё__л");
		dictionary.put("пришол", "приш__ё__л");
		dictionary.put("подошол", "подош__ё__л");
//		dictionary.put("шол", "ш__ё__л"); FIXME не работает
		dictionary.put("ушол", "уш__ё__л");
		dictionary.put("нашол", "наш__ё__л");
		dictionary.put("прошол", "прош__ё__л");
		dictionary.put("дошол", "дош__ё__л");
		String[] badNounsWordlist = new String[] {
				"школьник",
				"затупок",
				"невежда",
				"школозавр"
		};
		String[] badAdjsWordlist = new String[] {
				"неграмотный",
				"глупенький",
				"тупенький",
				"туповатый"
		};
		for (String word : dictionary.keySet()) {
			if (content.contains(word)) {
				message.reply("Правильно писать не \"" + word + "\", а \"" + dictionary.get(word) + "\", " +
						badAdjsWordlist[ThreadLocalRandom.current().nextInt(badAdjsWordlist.length)] + " " +
						badNounsWordlist[ThreadLocalRandom.current().nextInt(badNounsWordlist.length)] + "!");
			}
		}
	}

	private String[] leadin = new String[]{
			"Если чо, то вот:",
			"Вот такое может пригодиться —",
			"Между делом,",
			"Между прочим,",
			"Между прочим,",
			"Между прочим,",
			"Однако,",
			"Вот это искали?",
			"Вдруг поможет,",
			"Занимательный факт —",
			"Невероятно, но факт —",
			"А",
			"Кстати,",
			"Во чо умею —",
			"Вот такое могу —",
			"А я знаю, что",
			"Информация для размышления —",
			"А Вы знали, что",
			"А Вы знали, что",
			"А Вы знали, что",
			"А Вы знали, что",
			"А Вы знали, что",
			"А Вы знали, что"
	};

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		IGuild guild = event.getGuild();
		IChannel channel = event.getChannel();
		this.message = event.getMessage();
		String content = message.getContent().toLowerCase();

		if (event.getAuthor().isBot()) return; //на ботов не реагировать
		if (event.getMessage().getContent().startsWith(PREFIX)) return; //на команды не реагировать

		String[] args = event.getMessage().getContent().split(" ");
		HashMap<String, String> argsMap = getArgs(event.getGuild(), args);
		if (content.matches(".*<@!?\\d{18}>.*")) argsMap.remove(DISCORDID);
		if (argsMap.containsKey(NUMBER)) argsMap.remove(NUMBER);
		if (argsMap.containsKey(ROLEID)) argsMap.remove(ROLEID);
		StringBuilder argsBuilder = new StringBuilder();
		argsMap.forEach((key, value) -> {
			switch (key) {
				case STEAMID64:
					key = "ссылка на профиль SteamID64 " + value;
					value = "http://steamcommunity.com/profiles/" + value;
					break;
				case DISCORDID:
					IUser user = getClient().getUserByID(Long.parseLong(value));
					key = "имя пользователя с ID " + value;
					value = getName(event.getGuild(), user);
					break;
				case GUILDID:
					key = "название гильдии с ID " + value;
					value = getClient().getGuildByID(Long.parseLong(value)).getName();
					break;
				case ROLEID:
					key = "название роли с ID " + value;
					value = getClient().getRoleByID(Long.parseLong(value)).getName();
					break;
				case TIMESTAMP:
					key = "UNIX-time " + value;
					Timestamp timestamp = new Timestamp(Long.parseLong(value) * 1000L);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM YYYY года в HH:mm:ss");
					String date = timestamp.toLocalDateTime().format(formatter);
					long diff = System.currentTimeMillis() - timestamp.getTime();
					String future = "";
					String past = " назад, ";
					if (diff < 0) {
						future = "через ";
						past = ", ";
						diff = Math.abs(diff);
					}
					if (diff <= TimeUnit.MINUTES.toMillis(1)) {
						long quantity = TimeUnit.MILLISECONDS.toSeconds(diff);
						value = future + quantity + " " + getEnding("секунда", quantity) + past + date;
					} else if (diff <= TimeUnit.HOURS.toMillis(1)) {
						long quantity = TimeUnit.MILLISECONDS.toMinutes(diff);
						value = future + quantity + " " + getEnding("минута", quantity) + past + date;
					} else if (diff <= TimeUnit.DAYS.toMillis(1)) {
						long quantity = TimeUnit.MILLISECONDS.toHours(diff);
						value = future + quantity + " " + getEnding("час", quantity) + past + date;
					} else {
						long quantity = TimeUnit.MILLISECONDS.toDays(diff);
						value = future + quantity + " " + getEnding("день", quantity) + past + date;
					}
					break;
				case COMMANDNAME:
					key = "имя команды \"" + value + "\"";
					value = code(PREFIX + getCommandManager().getCommand(value).name);
					break;
				case USD:
					key = value + " пиндосских денег";
					value = toRubKop(String.valueOf(RUB_USD * Integer.parseInt(value))) +
							" (по курсу " + toRubKop(String.valueOf(RUB_USD)) + ")";
					break;
				case SEX:
					key = "пол " + value;
					switch (value) {
						case "W": value = "женский";
							break;
						case "N": value = "неопределенный (возможно, заднеприводный)";
							break;
						default: value = "мужской";
							break;
					}
					break;
			}
			argsBuilder.append("; ").append(u(key)).append(" = ").append(b(value));
		});
		argsBuilder.delete(0, 1); //удаляю запятую
		if (argsMap.size() != 0) channel.sendMessage(i(leadin[new Random().nextInt(leadin.length)] + argsBuilder));

		if (content.matches(".*привет.*") || content.contains("\uD83D\uDC4B")) {
			delay(100);
			message.addReaction(EmojiManager.getForAlias("wave"));
		} else if (content.matches(".*кто\\s.*") ||
				content.matches(".{4,}\\?$") ||
				content.matches("^го\\s.*") ||
				content.matches(".*будешь.*|.*будет.*") ||
				content.matches(".*кс\\s?го.*|.*cs[\\s:]?go.*") ||
				content.matches(".*пубг.*|.*pubg.*|.*пупок.*|.*пупчик.*|.*пабчик.*")) {
			delay(100);
			message.addReaction(EmojiManager.getByUnicode("➕"));
			delay(100);
			message.addReaction(EmojiManager.getByUnicode("➖"));
		}

		// ПРОВЕРКА НА ШКОЛЬНИКА
		for (IUser user : guild.getUsers()) {
			if (user.isBot()) continue;
			if (!user.getPresence().getPlayingText().isPresent()) continue;
			String playing = user.getPresence().getPlayingText().get();
			if (playing.matches(".*minecraft.*") ||
					playing.matches(".*warface.*") ||
					playing.matches(".*Garry’s\\sMod.*") ||
					playing.matches(".*World\\sof\\sTanks.*")) {
				setShkolnikRole(guild, user);
			}
		}

		try {
			File file = new File("txt/shkolnik_list.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = reader.readLine();
			while (line != null) {
				if (content.matches(line)) {
					delay(100);
					message.addReaction(EmojiManager.getByUnicode("🎒"));
					message.getChannel().sendMessage("школьник detected");
					exec(message.getGuild(), "tts", new String[]{"внимание, в канале " +
							message.getChannel().getName() + " зафиксирован школьник!"});
					setShkolnikRole(guild, event.getAuthor());
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		snitch();
	}

	private void setShkolnikRole(IGuild guild, IUser user) {
		String rolename = "школьник";
		IRole shkolnikRole = guild.getRolesByName(rolename).isEmpty() ? null : guild.getRolesByName(rolename).get(0);
		if (shkolnikRole == null) shkolnikRole = new RoleBuilder(guild).withName(rolename)
				.withColor(new Color(0x7B917B)).setMentionable(false).setHoist(true).build();
		if (!user.hasRole(shkolnikRole)) user.addRole(shkolnikRole);

		rolename = "Школьник недели";
		IRole shkolnikWeekRole = guild.getRolesByName(rolename).isEmpty() ? null : guild.getRolesByName(rolename).get(0);
		if (shkolnikWeekRole == null) shkolnikWeekRole = new RoleBuilder(guild).withName(rolename)
				.withColor(new Color(0x639132)).setMentionable(false).setHoist(true).build();
		for (IUser u : guild.getUsersByRole(shkolnikWeekRole)) u.removeRole(shkolnikWeekRole);
		user.addRole(shkolnikWeekRole);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР