package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class Snitch {

	public void snitch(IMessage message) {
		String content = message.getContent();
		IChannel channel = message.getChannel();

		// ПРОВЕРКА НА STEAMID64 И ПРЕДОСТАВЛЕНИЕ ССЫЛКИ НА ЭТОТ АККАУНТ
		if (Utilities.isSteamID64(content)) {
			channel.sendMessage("http://steamcommunity.com/profiles/" + content);
		}

		// ПРОВЕРКА НА СОДЕРЖАНИЕ СЛОВА ИЗ СООБЩЕНИЯ В КИДЛИСТЕ
		String[] kidWordlist = new String[] {
				"лол",
				"кек",
		};
		for (String word : kidWordlist) {
			if (content.equals(word)) message.addReaction("🎒");
		}


	}
}
