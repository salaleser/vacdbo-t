package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Gamer;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.bot.command.*;
import ru.salaleser.vacdbot.bot.command.admin.*;
import ru.salaleser.vacdbot.bot.command.audioplayer.*;
import ru.salaleser.vacdbot.bot.command.steam.*;
import ru.salaleser.vacdbot.bot.command.support.*;
import ru.salaleser.vacdbot.bot.command.utility.*;
import ru.salaleser.vacdbot.gui.Gui;
import ru.salaleser.vacdbot.gui.Log;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;
import java.util.HashMap;

public class Bot {

	private static ArrayList<IGuild> guilds = new ArrayList<>();
	public static ArrayList<IGuild> getGuilds() {
		return guilds;
	}
	public static void addGuild(IGuild guild) {
		guilds.add(guild);
	}

	private static HashMap<String, Gamer> gamers = new HashMap<>();
	public static HashMap<String, Gamer> getGamers() {
		return gamers;
	}
	public static void addGamer(String discordid, Gamer gamer) {
		gamers.put(discordid, gamer);
	}

	private static IDiscordClient client;
	public static void setClient(IDiscordClient client) {
		Bot.client = client;
	}
	public static IDiscordClient getClient() {
		return client;
	}

	public static final String SALALESER = "223559816239513601";
	public static final String STATUS = "твои нервы!";
	public static final String PREFIX = "~";
	public static final int RUB_USD = 5712; // FIXME: 12.03.2018 парсить с сайта

	public static Gui gui;
	public static Log log;

	private static final Config CONFIG = new Config();
	private static final String CFG = "vacdbot.cfg";
	private static final ClientBuilder CLIENT_BUILDER = new ClientBuilder();
	private static final CommandManager COMMAND_MANAGER = new CommandManager();

	private static ClearCommand clearCommand = new ClearCommand();
	private static ForeverAloneCommand foreverAloneCommand= new ForeverAloneCommand();
	private static SoundCommand soundCommand= new SoundCommand();
	private static ConsoleCommand consoleCommand= new ConsoleCommand();

	public static void main(String[] args) {
		addCommands();

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
			dispatcher.registerListener(COMMAND_MANAGER);
			dispatcher.registerListener(new Listener());
			dispatcher.registerListener(new Snitch());
			dispatcher.registerListener(clearCommand);
			dispatcher.registerListener(foreverAloneCommand);
			dispatcher.registerListener(soundCommand);
			dispatcher.registerListener(consoleCommand);
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

	/**
	 * Регистрация команд
	 */
	private static void addCommands() { // TODO: 09.02.2018 скан на новые модули и подключаемые пользовательские модули
		System.out.println("Загружаю модули...");
		COMMAND_MANAGER.addCommand(consoleCommand);
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
		COMMAND_MANAGER.addCommand(new TransCommand());
		COMMAND_MANAGER.addCommand(new CostCommand());
		COMMAND_MANAGER.addCommand(new TimerCommand());
		COMMAND_MANAGER.addCommand(clearCommand);
		COMMAND_MANAGER.addCommand(new ScanCommand());
		COMMAND_MANAGER.addCommand(new SelectCommand());
		COMMAND_MANAGER.addCommand(new CheckCommand());
		COMMAND_MANAGER.addCommand(new InviteCommand());
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
		COMMAND_MANAGER.addCommand(foreverAloneCommand);
		COMMAND_MANAGER.addCommand(new TrainingCommand());
		COMMAND_MANAGER.addCommand(new ConvertCommand());
		COMMAND_MANAGER.addCommand(new RoleCommand());
		COMMAND_MANAGER.addCommand(soundCommand);
		COMMAND_MANAGER.addCommand(new ActivateCommand());
		System.out.println("Всего модулей загружено — " + Command.count);
	}

	/**
	 * Обертка для метода handle() команды
	 *
	 * @param guild гильдия
	 * @param commandName имя команды
	 * @param args аргументы
	 */
	public static void exec(IGuild guild, String commandName, String[] args) {
		getCommandManager().getCommand(commandName).handle(guild, null, args);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР