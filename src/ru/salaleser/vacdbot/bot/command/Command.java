package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {

	public final static String MISC = "Разное";
	public final static String STEAM = "Steam";
	public final static String SUPPORT = "Полезные";
	public final static String UTILITY = "Служебные";
	public final static String PLAYER = "Аудиоплеер";

	public static int count = 0;
	public final String name;
	public final String category;
	public final String description;
	public final String[] aliases;

	// TODO: 10.12.2017 создать универсальный способ для всех команд для определения возможных значений переменных

	protected Command(String name) {
		count++;
		this.name = name;
		this.category = MISC;
		this.description = "(нет описания)";
		this.aliases = new String[0];
	}

	protected Command(String name, String category) {
		count++;
		this.name = name;
		this.category = category;
		this.description = "(нет описания)";
		this.aliases = new String[0];
	}

	protected Command(String name, String category, String description) {
		count++;
		this.name = name;
		this.category = category;
		this.description = description;
		this.aliases = new String[0];
	}

	protected Command(String name, String category, String description, String[] aliases) {
		count++;
		this.name = name;
		this.category = category;
		this.description = description;
		this.aliases = aliases;
	}

	public void help(IMessage message) {
		message.getChannel().sendMessage(Util.ub("Помощь к команде \"" + name + "\":") + "\n" +
				Util.i("Помощи «нигде нет». Просто слов нет. Найдем слова – сделаем помощь." +
				" Вы держитесь здесь, вам всего доброго, хорошего настроения и здоровья."));
	}

	public abstract void handle(IGuild guild, IMessage message, String[] args);

	protected String buildHelp(String description, String usage, String presets, String example, String note) {
		StringBuilder aliasesBuilder = new StringBuilder();
		String aliasesString;
		if (this.aliases.length > 0) {
			for (String alias : this.aliases) aliasesBuilder.append(", ").append(Util.code(alias));
			aliasesString = aliasesBuilder.substring(1) + ".";
		} else {
			aliasesString = "нет.";
		}
		return Util.ub("Помощь к команде \"" + name + "\":") + "\n" + Util.b("Описание: ") + description + "\n" + Util.b("Использование: ") + usage + "\n" + Util.b("Предустановки: ") + presets + "\n" + Util.b("Псевдонимы: ") + aliasesString + "\n" + Util.b("Пример: ") + example + "\n" + Util.b("Примечание: ") + note;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР