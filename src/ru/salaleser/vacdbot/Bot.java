package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.command.*;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class Bot {

	public static IChannel log;
	public static String status = "твои нервы!";
	public static IUser bot;
	public static IGuild guild;
	public static final Gui gui = new Gui();

	private static IDiscordClient client;
	private static CommandManager commandManager = new CommandManager();

	public static void main(String[] args) {
		addCommands();
		new Config();
		client = createClient(Config.readConfigFile("/Users/salaleser/IdeaProjects/vacdbo-t/out/artifacts/vacdbo_t_jar/"));
		EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
		dispatcher.registerListener(new AnnotationListener()); // Registers the @EventSubscriber example class from above
	}

	private static IDiscordClient createClient(boolean login) {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(Config.getToken()); // Adds the login info to the builder
		try {
			if (login) {
				return clientBuilder.login(); // Creates the client instance and logs the client in
			} else {
				return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
			}
		} catch (DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			Bot.gui.addText(e.getErrorMessage());
			return null;
		}
	}

	public static void retry() {
		client.login();
	}

	static CommandManager getCommandManager() {
		return commandManager;
	}

	private static void addCommands() {
		commandManager.addCommand(new ConsoleCommand());
		commandManager.addCommand(new GoogleCommand());
		commandManager.addCommand(new HelpCommand(commandManager));
		commandManager.addCommand(new PollCommand());
		commandManager.addCommand(new RandomCommand());
		commandManager.addCommand(new ReadyCommand());
		commandManager.addCommand(new RepCommand());
		commandManager.addCommand(new ReportCommand());
		commandManager.addCommand(new ServerCommand());
		commandManager.addCommand(new StatusCommand());
		commandManager.addCommand(new TipCommand());
		commandManager.addCommand(new VacCommand());
	}
}