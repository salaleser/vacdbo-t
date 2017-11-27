package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.bot.command.*;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

public class Bot {

	public static IGuild guildKTO;
	public static IChannel channelKTOLog; // FIXME: 21.11.2017 убрать хардкод
	public static IChannel channelKTOTest;
	public static IChannel channelKTOGeneral;
	public static IVoiceChannel voiceChannelGeneral;
	public static IRole roleOfficers;
	public static String status = "твои нервы!";

	public static Gui gui;
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
		Logger.info("Графическая оболочка запущена.");

		boolean isConfig = Config.readConfigFile(CFG);
		if (!isConfig) isConfig = Config.readConfigFile("/Users/salaleser/IdeaProjects/" +
				"vacdbo-t/out/artifacts/vacdbo_t_jar/" + CFG);
		IDiscordClient client = login(isConfig);
		if (client != null) {
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new AnnotationListener());
		}

		new Scheduler();
		Logger.info("Планировщик запущен.");
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

	private static void addCommands() {
		COMMAND_MANAGER.addCommand(new ConsoleCommand());
		COMMAND_MANAGER.addCommand(new FindCommand());
		COMMAND_MANAGER.addCommand(new HelpCommand(COMMAND_MANAGER));
		COMMAND_MANAGER.addCommand(new PollCommand());
		COMMAND_MANAGER.addCommand(new RandomCommand());
		COMMAND_MANAGER.addCommand(new ReadyCommand());
		COMMAND_MANAGER.addCommand(new RepCommand());
		COMMAND_MANAGER.addCommand(new ReportCommand());
		COMMAND_MANAGER.addCommand(new ServerCommand());
		COMMAND_MANAGER.addCommand(new StatusCommand());
		//		COMMAND_MANAGER.addCommand(new TipCommand());
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
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР