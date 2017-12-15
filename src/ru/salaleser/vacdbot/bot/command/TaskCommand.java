package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.Scheduler;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TaskCommand extends Command {

	public TaskCommand() {
		super("task", 2);
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Планирует задачи.",
				"`~task [<метод>]`.",
				"`~task refresh` — обновить список задач;\n" +
						"`~task add <команда> <час> <минута> <период_повторения> — добавить задачу",
				"`~task`.",
				"в разработке."
				)
		);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		String table = "schedule";
		if (args.length == 0) {
			String[] columns = new String[]{"command", "hour", "minute", "period"};
			message.getChannel().sendMessage(Util.makeTable(table, columns, Scheduler.getTasks()));
			return;
		}
		switch (args[0]) {
			case "add":
				if (args.length != 5) {
					Logger.error("Неверное количество аргументов!");
					message.getChannel().sendMessage("Недостаточно аргументов!");
					return;
				}
				String[] task = new String[] {args[1], args[2], args[3], args[4]};
				DBHelper.insert(table, task);
				break;
			case "remove":
			case "delete":
				if (args.length != 2) {
					Logger.error("Неверное количество аргументов!");
					message.getChannel().sendMessage("Неверное количество аргументов!");
					return;
				}
				String deleteQuery = "DELETE FROM " + table + " WHERE command = '" + args[1] + "'";
				if (DBHelper.commit(table, deleteQuery, null)) {
					message.reply(" вы удалили задачу планировщика.");
				} else {
					message.reply(" произошла ошибка при попытке удаления.");
				}
				break;
			case "r":
			case "refresh":
				Scheduler.refreshTasks();
				break;
			default:
				Logger.error("Неверный агрумент!");
				message.getChannel().sendMessage("Неверный агрумент!");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР