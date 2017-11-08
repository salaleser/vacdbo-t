package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IChannel;

class DLog {

	private static IChannel channel = Main.guild.getChannelByID(Main.CHANNEL_LOG);

	static void add(String text) {
		try {
			channel.sendMessage(text);
		} catch (Exception e) {
			DLog.add(e.getMessage());
			e.printStackTrace();
		}
	}
}
