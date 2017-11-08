package ru.salaleser.vacdbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

public class Main {
	static long CHANNEL_OFFICERS = 347961146059390976L;
	static long CHANNEL_GENERAL = 347088817729306624L;
	static long CHANNEL_TEST = 347333162449502208L;
	static IChannel channelLog;

	private final IDiscordClient client;
	private final MessageManager messageManager;

	private Main() throws Exception {
		Config config = new Config();
		new Log();
		messageManager = new MessageManager();
		client = new ClientBuilder().withToken(config.getToken()).build();

		client.login();
		client.getDispatcher().registerListener(this);
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {

	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		channelLog = client.getChannelByID(377431980658393088L);
		if (event.getMessage().getContent().startsWith("~")) {
			messageManager.handleMessage(event.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}
}
