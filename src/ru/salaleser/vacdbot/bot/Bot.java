package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.bot.command.*;
import ru.salaleser.vacdbot.bot.command.audioplayer.*;
import ru.salaleser.vacdbot.bot.command.steam.*;
import ru.salaleser.vacdbot.bot.command.support.*;
import ru.salaleser.vacdbot.bot.command.utility.*;
import ru.salaleser.vacdbot.gui.Gui;
import ru.salaleser.vacdbot.gui.Log;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;

public class Bot {

	private static ArrayList<IGuild> guilds = new ArrayList<>();
	public static ArrayList<IGuild> getGuilds() {
		return guilds;
	}
	public static void setGuilds(ArrayList<IGuild> guilds) {
		Bot.guilds = guilds;
	}
	public static void addGuild(IGuild guild) {
		Bot.guilds.add(guild);
	}

	private static IDiscordClient client;
	public static void setClient(IDiscordClient client) {
		Bot.client = client;
	}
	public static IDiscordClient getClient() {
		return client;
	}

	public static final String STATUS = "твои нервы!";
	public static final String PREFIX = "~";

	public static Gui gui;
	public static Log log;
	private static final Config CONFIG = new Config();
	private static final String CFG = "vacdbot.cfg";
	private static final ClientBuilder CLIENT_BUILDER = new ClientBuilder();
	private static final CommandManager COMMAND_MANAGER = new CommandManager();

	public static void main(String[] args) {
		System.out.println("Загружаю модули...");
		addCommands();
		System.out.println("Всего модулей загружено — " + Command.count);

		System.out.println("Загружаю графическую оболочку...");
		gui = new Gui();
		log = new Log();
		Logger.info("Графическая оболочка запущена.");

		boolean isConfig = Config.readConfigFile(CFG);
		if (!isConfig) isConfig = Config.readConfigFile("/Users/salaleser/IdeaProjects/" +
				"vacdbo-t/out/artifacts/vacdbo_t_jar/" + CFG);
		IDiscordClient client = login(isConfig);
		if (client != null) {
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new Listener());
		}
		new DBHelper();
	}

	private static IDiscordClient login(boolean login) {
		try {
			CLIENT_BUILDER.withToken(Config.getToken());
			if (login) {
				Bot.gui.setConnecting();
				return CLIENT_BUILDER.login();
			} else {
				return CLIENT_BUILDER.build();
			}
		} catch (DiscordException e) {
			e.printStackTrace();
			Logger.error(e.getErrorMessage());
			return null;
		} catch (NullPointerException e) {
			Logger.error("Конфигурационный файл не загружен!");
			return null;
		}
	}

	public static void relogin() {
		login(true);
	}

	public static CommandManager getCommandManager() {
		return COMMAND_MANAGER;
	}

	private static void addCommands() { // TODO: 09.02.2018 скан на новые модули и подключаемые пользовательские модули
		COMMAND_MANAGER.addCommand(new ConsoleCommand());
		COMMAND_MANAGER.addCommand(new FindCommand());
		COMMAND_MANAGER.addCommand(new HelpCommand());
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
		COMMAND_MANAGER.addCommand(new SetCommand());
		COMMAND_MANAGER.addCommand(new GetCommand());
		COMMAND_MANAGER.addCommand(new CalcCommand());
		COMMAND_MANAGER.addCommand(new IdCommand());
		COMMAND_MANAGER.addCommand(new TransCommand());
		COMMAND_MANAGER.addCommand(new CostCommand());
		COMMAND_MANAGER.addCommand(new TimerCommand());
		COMMAND_MANAGER.addCommand(new ClearCommand());
		COMMAND_MANAGER.addCommand(new ScanCommand());
		COMMAND_MANAGER.addCommand(new SelectCommand());
		COMMAND_MANAGER.addCommand(new CheckCommand());
		COMMAND_MANAGER.addCommand(new InviteCommand());
		COMMAND_MANAGER.addCommand(new TaskCommand());
		COMMAND_MANAGER.addCommand(new TestCommand());
		COMMAND_MANAGER.addCommand(new PlayerCommand());
		COMMAND_MANAGER.addCommand(new PlayCommand());
		COMMAND_MANAGER.addCommand(new StopCommand());
		COMMAND_MANAGER.addCommand(new SkipCommand());
		COMMAND_MANAGER.addCommand(new PauseCommand());
		COMMAND_MANAGER.addCommand(new LeaveCommand());
		COMMAND_MANAGER.addCommand(new TTSCommand());
		COMMAND_MANAGER.addCommand(new UserCommand());
		COMMAND_MANAGER.addCommand(new EventCommand());
		COMMAND_MANAGER.addCommand(new ForeverAloneCommand());
	}

	public static void exec(IGuild guild, String commandName, String[] args) throws InterruptedException {
		getCommandManager().getCommand(commandName).handle(guild, null, args);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР