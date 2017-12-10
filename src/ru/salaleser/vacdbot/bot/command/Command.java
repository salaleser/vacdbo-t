package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	public static int count = 0;
	public final String name;
	public final int permissions;
	final String help;

	// TODO: 10.12.2017 создать универсальный способ для всех команд для определения возможных значений переменных

	/**
	 * Команда
	 *
	 * @param name имя команды
	 * @param permissions необходимый priority для использования команды
	 * @param help описание команды
	 */
	Command(String name, int permissions, String help) {
		this.name = name;
		this.help = help;
		count++;
		this.permissions = permissions;
	}

	public abstract void handle(IMessage message, String[] args) throws InterruptedException;
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР