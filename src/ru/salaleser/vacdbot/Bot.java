package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.command.*;
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
	private final CommandManager commandManager;

	private Bot() throws Exception {
		new Config();
		commandManager = new CommandManager();
		commandManager.addCommand(new HelpCommand(commandManager));
		commandManager.addCommand(new ConsoleCommand());
		commandManager.addCommand(new MapCommand());
		commandManager.addCommand(new PollCommand());
		commandManager.addCommand(new RandomCommand());
		commandManager.addCommand(new ReadyCommand());
		commandManager.addCommand(new RepCommand());
		commandManager.addCommand(new ReportCommand());
		commandManager.addCommand(new ServerCommand());
		commandManager.addCommand(new TipCommand());
		commandManager.addCommand(new VacCommand());

		client = new ClientBuilder().withToken(Config.getToken()).build();

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
		if (event.getMessage().getContent().equals("~")) return;
		if (event.getMessage().getContent().startsWith("~")) {
			commandManager.handle(event.getMessage());
		} else {
			if (event.getMessage().getMentions().size() != 0) { // Содержит ли текущее сообщение упоминание
				for (int i = 0; i < event.getMessage().getMentions().size(); i++) { // Перебираю упомянутых пользователей
					// УПОМИНАНИЕ БОТА
					if (event.getMessage().getMentions().get(i).getName().equals("VACDBO-T")) {
						event.getMessage().getChannel().sendMessage(event.getMessage().getAuthor() + ", я выявляю недавно получивших VAC-бан друзей" + "(команда \"~vac\"");
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Bot();
	}
}
