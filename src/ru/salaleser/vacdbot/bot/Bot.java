package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.CheckSuspectsTask;
import ru.salaleser.vacdbot.bot.command.*;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

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
	private static final ClientBuilder CLIENT_BUILDER = new ClientBuilder();
	private static final CommandManager COMMAND_MANAGER = new CommandManager();

	public static void main(String[] args) {
		addCommands();
		gui = new Gui();
		Logger.info("Графическая оболочка запущена.");
		Logger.info("Загружаю модули...");
		Logger.info("Всего модулей загружено — " + Command.count);
		boolean isConfig = Config.readConfigFile("/" + "Users/aleksejsalienko/Documents/vacdbo-t/out/artifacts/vacdbo_t_jar/vacdbot.cfg");
		if (!isConfig)
			isConfig = Config.readConfigFile("/" + "Users/salaleser/IdeaProjects/vacdbo-t/out/artifacts/vacdbo_t_jar/vacdbot.cfg");
		if (!isConfig) isConfig = Config.readConfigFile("vacdbot.cfg");
		IDiscordClient client = login(isConfig);
		if (client != null) {
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new AnnotationListener());
		}
		runTask();
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

	private static void runTask() {
		Calendar evening = Calendar.getInstance();
		evening.set(Calendar.HOUR_OF_DAY, 20);
		evening.set(Calendar.MINUTE, 0);
		evening.set(Calendar.SECOND, 0);
		evening.set(Calendar.MILLISECOND, 0);

		Calendar custom = Calendar.getInstance();
		custom.set(Calendar.HOUR_OF_DAY, 4);
		custom.set(Calendar.MINUTE, 59);

		Timer time = new Timer();
		time.schedule(new CheckSuspectsTask(), evening.getTime(), TimeUnit.HOURS.toMillis(4));
		time.schedule(new CheckSuspectsTask(), custom.getTime());
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР