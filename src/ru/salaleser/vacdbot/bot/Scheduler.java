package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	Scheduler() {
		getTasks();
		Logger.info("Планировщик запущен.");
	}

	private static Timer timer;

	public static String[][] getTasks() {
		timer = new Timer();
		String[][] tasks = DBHelper.executeQuery("SELECT * FROM schedule");
		if (tasks[0][0] == null) return tasks;
		for (String[] task : tasks) {
			String commandName = task[0];
			int hourOfDay = Integer.parseInt(task[1]);
			int minute = Integer.parseInt(task[2]);
			int period = Integer.parseInt(task[3]);
			boolean enabled = Boolean.valueOf(task[4]);
			String guildid = task[5];
			if (enabled) createTask(commandName, new String[]{}, hourOfDay, minute, period, guildid);
		}
		return tasks;
	}

	private static void createTask(String commandName, String[] args, int hourOfDay, int minute, int period, String guildid) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				IGuild guild = Bot.getClient().getGuildByID(Long.parseLong(guildid));
				IMessage message = guild.getDefaultChannel().sendMessage(Util.i("Запланированная задача:"));
				try {
					Bot.getCommandManager().getCommand(commandName).handle(guild, message, args);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					message.delete();
				}
			}
		}, calendar.getTime(), TimeUnit.MINUTES.toMillis(period));
	}

	public static void refreshTasks() {
		timer.cancel();
		getTasks();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР