package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.command.CommandManager;
import ru.salaleser.vacdbot.command.HelpCommand;
import ru.salaleser.vacdbot.command.MapCommand;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class Bot {
	static long CHANNEL_OFFICERS = 347961146059390976L;
	static long CHANNEL_GENERAL = 347088817729306624L;
	static long CHANNEL_TEST = 347333162449502208L;
	public static IChannel log;

	private final IDiscordClient client;
	private final MessageManager messageManager;
	private final CommandManager commandManager;

	private Bot() throws Exception {
		Config config = new Config();
		messageManager = new MessageManager();
		commandManager = new CommandManager(this);
		commandManager.addCommand(new HelpCommand(commandManager));
		commandManager.addCommand(new MapCommand());
//		commandManager.addCommand(new RandomCommand());

		client = new ClientBuilder().withToken(config.getToken()).build();

		client.login();
		client.getDispatcher().registerListener(this);
	}

	@EventSubscriber
	public void onReady(ReadyEvent event) throws RateLimitException, DiscordException {
		client.changePlayingText("KTO=ЛЕЩ");
		log = client.getChannelByID(377431980658393088L);
		log.sendMessage("**___Я тут___**");
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {

	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith("~")) {
			messageManager.handleMessage(event.getMessage());
		}

		if (event.getMessage().getContent().startsWith("~"))
			commandManager.handle(event.getMessage());
	}

	public static void main(String[] args) throws Exception {
		new Bot();
	}
}
