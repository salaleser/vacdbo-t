package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

public class Snitch {

	public void snitch(IMessage message) {
		String content = message.getContent();
		if (Utilities.isSteamID64(content)) {
			message.getChannel().sendMessage("http://steamcommunity.com/profiles/" + content);
		}
	}
}
