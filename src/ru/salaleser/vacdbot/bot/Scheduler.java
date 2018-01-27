package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	Scheduler() {
		getTasks();
	}

	private static Timer timer;
	private static IChannel channel = Bot.channelKTOGeneral;

	public static String[][] getTasks() {
		timer = new Timer();
		String sql = "SELECT * FROM schedule";
		String[][] tasks = DBHelper.executeQuery(sql);
		for (String[] task : tasks) {
			String commandName = task[0];
			int hourOfDay = Integer.parseInt(task[1]);
			int minute = Integer.parseInt(task[2]);
			int period = Integer.parseInt(task[3]);
			boolean enabled = Boolean.valueOf(task[4]);
			if (enabled) createTask(commandName, new String[]{}, hourOfDay, minute, period);
		}
		return tasks;
	}

	private static void createTask(String commandName, String[] args, int hourOfDay, int minute, int period) {
		Command command = Bot.getCommandManager().getCommand(commandName);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				IMessage message = channel.sendMessage(Util.i("Запланированная задача:"));
				try {
					command.handle(message, args);
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