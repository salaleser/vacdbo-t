package ru.salaleser.vacdbot;

import ru.salaleser.vacdbot.bot.Bot;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Доморощенный логгер, зато мой
 * TODO изучить уже наконец SLF4J
 */
public class Logger {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm:ss.SSS");

	public static void info(String message) {
		System.out.println(LocalDateTime.now().format(formatter) + " [INFO] " + message);
		Bot.gui.addText(LocalDateTime.now().format(formatter) + " [INFO] " + message, Color.BLACK);
	}

	public static void error(String message) {
		System.err.println(LocalDateTime.now().format(formatter) + " [ERROR] " + message);
		Bot.gui.addText(LocalDateTime.now().format(formatter) + " [ERROR] " + message, Color.RED);
	}

	public static void onMessage(String message) {
		Bot.gui.addText(LocalDateTime.now().format(formatter) + " [MESSAGE] " + message, Color.BLUE);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР