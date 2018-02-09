package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Доморощенный логгер, зато мой
 * TODO изучить уже наконец SLF4J
 *
 * Рассылает сообщения во все каналы с именем "log" всех гильдий,
 * а также в GUI и консоль
 */
public class Logger {

	private static Gui gui = Bot.gui;
	private final static String LOG_CHANNEL_NAME = "log";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm:ss.SSS");

	public static void onMessage(String message) {
		gui.addText(LocalDateTime.now().format(formatter) + " [MESSAGE] " + message, Color.BLUE);
	}

	public static void info(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [INFO] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [INFO] " + message, Color.BLACK);
		for (IGuild guild : Bot.getGuilds()) {
			for (IChannel channel : guild.getChannelsByName(LOG_CHANNEL_NAME)) {
				channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [INFO] " + message));
			}
		}
	}

	public static void debug(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [DEBUG] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [DEBUG] " + message, Color.ORANGE);
		for (IGuild guild : Bot.getGuilds()) {
			for (IChannel channel : guild.getChannelsByName(LOG_CHANNEL_NAME)) {
				channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [DEBUG] " + message));
			}
		}
	}

	public static void error(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [ERROR] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [ERROR] " + message, Color.RED);
		for (IGuild guild : Bot.getGuilds()) {
			for (IChannel channel : guild.getChannelsByName(LOG_CHANNEL_NAME)) {
				channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [ERROR] " + message));
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР