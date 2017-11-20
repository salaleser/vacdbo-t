package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Snitch {

	public void snitch(IMessage message) {
		String content = message.getContent().toLowerCase();
		IChannel channel = message.getChannel();

		// ПРОВЕРКА НА STEAMID64 И ПРЕДОСТАВЛЕНИЕ ССЫЛКИ НА ЭТОТ АККАУНТ
		if (Util.isSteamID64(content)) {
			channel.sendMessage("http://steamcommunity.com/profiles/" + content);
		}

		// ПРОВЕРКА НА СОДЕРЖАНИЕ СЛОВА ИЗ СООБЩЕНИЯ В КИДЛИСТЕ
		String[] kidWordlist = new String[] {
				"лол",
				"кек",
				"рофл",
				"эщкере",
		};
		for (String word : kidWordlist) {
			if (content.contains(word)) {
				message.addReaction("🎒");
			}
		}

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
				message.reply("Правильно писать не \"" + word +
						"\", а \"" + dictionary.get(word) + "\", " +
						badAdjsWordlist[ThreadLocalRandom.current()
								.nextInt(badAdjsWordlist.length)] + " " +
						badNounsWordlist[ThreadLocalRandom.current()
								.nextInt(badNounsWordlist.length)] + "!");
			}
		}
	}
}
