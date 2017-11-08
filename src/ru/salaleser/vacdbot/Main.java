package ru.salaleser.vacdbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;

public class Main {
	static IGuild guild;
	static final long CHANNEL_OFFICERS = 347961146059390976L;
	static final long CHANNEL_GENERAL = 347088817729306624L;
	static final long CHANNEL_TEST = 347333162449502208L;
	static final long CHANNEL_LOG = 377431980658393088L;

	private final MessageManager messageManager;

	private Main() throws Exception {
		Config config = new Config();
		IDiscordClient client = new ClientBuilder().withToken(config.getToken()).build();
		guild = client.getGuildByID(223560049937743872L);
		messageManager = new MessageManager();

		client.login();
		client.getDispatcher().registerListener(this);
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {

	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith("~")) {
			messageManager.handleMessage(event.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}
}
