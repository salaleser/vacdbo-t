import sx.blah.discord.handle.obj.IChannel;

class DLog {

	private static IChannel channel = VACDBOT.guild.getChannelByID(VACDBOT.CHANNEL_LOG);

	static void add(String text) {
		try {
			channel.sendMessage(text);
		} catch (Exception e) {
			DLog.add(e.getMessage());
			e.printStackTrace();
		}
	}
}
