package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.handle.obj.IChannel;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Доморощенный логгер, зато мой
 * TODO изучить уже наконец SLF4J
 */
public class Logger {

	private static Gui gui = Bot.gui;
	private static IChannel channel = null;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm:ss.SSS");

	public static void onMessage(String message) {
		gui.addText(LocalDateTime.now().format(formatter) + " [MESSAGE] " + message, Color.BLUE);
	}

	public static void info(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [INFO] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [INFO] " + message, Color.BLACK);
		if (channel != null) {
			channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [INFO] " + message));
		} else {
			channel = Bot.channelKTOLog;
		}
	}

	public static void debug(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [DEBUG] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [DEBUG] " + message, Color.ORANGE);
		if (channel != null) {
			channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [DEBUG] " + message));
		} else {
			channel = Bot.channelKTOLog;
		}
	}

	public static void error(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [ERROR] " + message);
		gui.addText(LocalDateTime.now().format(formatter) + " [ERROR] " + message, Color.RED);
		if (channel != null) {
			channel.sendMessage(Util.block(LocalDateTime.now().format(formatter) + " [ERROR] " + message));
		} else {
			channel = Bot.channelKTOLog;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР