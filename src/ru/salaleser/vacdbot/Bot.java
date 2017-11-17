package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.command.*;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

public class Bot {

	public static IChannel log;
	public static IChannel test;
	public static IChannel general;
	public static String status = "твои нервы!";
	public static IUser bot;
	public static IGuild guild;
	public static IRole KTO;
	public static IVoiceChannel voice;

	public static Gui gui;
	private static final Config CONFIG = new Config();
	private static final ClientBuilder CLIENT_BUILDER = new ClientBuilder();
	private static final CommandManager COMMAND_MANAGER = new CommandManager();

	public static void main(String[] args) {
		addCommands();
		gui = new Gui();
		IDiscordClient client;
		if (Config.readConfigFile("/Users/aleksejsalienko/Documents/vacdbo-t/out/artifacts/vacdbo_t_jar/")) client = login();
		else return;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new AnnotationListener());
	}

	public static IDiscordClient login() {
		CLIENT_BUILDER.withToken(Config.getToken());
		try {
			return CLIENT_BUILDER.login();
		} catch (DiscordException e) {
			e.printStackTrace();
			Bot.gui.addText(e.getErrorMessage());
			return null;
		}
	}

	public static CommandManager getCommandManager() {
		return COMMAND_MANAGER;
	}

	private static void addCommands() {
		COMMAND_MANAGER.addCommand(new ConsoleCommand());
		COMMAND_MANAGER.addCommand(new GoogleCommand());
		COMMAND_MANAGER.addCommand(new HelpCommand(COMMAND_MANAGER));
		COMMAND_MANAGER.addCommand(new PollCommand());
		COMMAND_MANAGER.addCommand(new RandomCommand());
		COMMAND_MANAGER.addCommand(new ReadyCommand());
		COMMAND_MANAGER.addCommand(new RepCommand());
		COMMAND_MANAGER.addCommand(new ReportCommand());
		COMMAND_MANAGER.addCommand(new ServerCommand());
		COMMAND_MANAGER.addCommand(new StatusCommand());
		COMMAND_MANAGER.addCommand(new TipCommand());
		COMMAND_MANAGER.addCommand(new VacCommand());
		COMMAND_MANAGER.addCommand(new SmokeCommand());
		COMMAND_MANAGER.addCommand(new QuitCommand());
	}
}