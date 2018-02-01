package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	public static int count = 0;
	public final String name;
	public final String[] aliases;
	public final int permissions;

	// TODO: 10.12.2017 создать универсальный способ для всех команд для определения возможных значений переменных

	protected Command(String name) {
		count++;
		this.name = name;
		this.aliases = new String[0];
		this.permissions = 0;
	}

	protected Command(String name, String[] aliases) {
		count++;
		this.name = name;
		this.aliases = aliases;
		this.permissions = 0;
	}

	protected Command(String name, int permissions) {
		count++;
		this.name = name;
		this.aliases = new String[0];
		this.permissions = permissions;
	}

	/**
	 * Команда
	 *
	 * @param name имя команды
	 * @param aliases псевдонимы
	 * @param permissions необходимый priority для использования команды
	 */
	protected Command(String name, String[] aliases, int permissions) {
		count++;
		this.name = name;
		this.aliases = aliases;
		this.permissions = permissions;
	}

	protected void help(IMessage message) {
		message.getChannel().sendMessage(Util.ub("Помощь к команде \"" + name + "\":") + "\n" +
				Util.i("Помощи «нигде нет». Просто слов нет. Найдем слова – сделаем помощь." +
				" Вы держитесь здесь, вам всего доброго, хорошего настроения и здоровья."));
	}

	public abstract void handle(IMessage message, String[] args) throws InterruptedException;

	protected String buildHelp(String description, String usage, String presets, String example, String note) {
		StringBuilder aliasesBuilder = new StringBuilder();
		String aliasesString;
		if (this.aliases.length > 0) {
			for (String alias : this.aliases) aliasesBuilder.append(", ").append(Util.code(alias));
			aliasesString = aliasesBuilder.substring(1) + ".";
		} else {
			aliasesString = "нет.";
		}
		return Util.ub("Помощь к команде \"" + name + "\":") + "\n" +
				Util.b("Описание: ") + description + "\n" +
				Util.b("Использование: ") + usage + "\n" +
				Util.b("Предустановки: ") + presets + "\n" +
				Util.b("Псевдонимы: ") + aliasesString + "\n" +
				Util.b("Пример: ") + example + "\n" +
				Util.b("Примечание: ") + note;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР