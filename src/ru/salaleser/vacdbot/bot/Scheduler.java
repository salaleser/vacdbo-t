package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	private static Timer timer;

	Scheduler() {
		Logger.info("Планировщик запущен.");
		refreshTasks();
	}

	public static void refreshTasks() {
		timer = new Timer();
		Logger.info("Задач добавлено: " + getTasks() + ".");
	}

	private static int getTasks() {
		int counter = 0;
		String[][] tasks = DBHelper.executeQuery("SELECT * FROM events");
		if (tasks[0][0] == null) return counter;
		int nowYear = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy")));
		int nowMonth = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM")));
		int nowDay = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd")));
		int nowHour = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")));
		int nowMinute = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm")));
		for (String[] task : tasks) {
			String id = task[EventColumns.ID];
			String guildid = task[EventColumns.GuildID];
			int hour = Integer.parseInt(task[EventColumns.Hour]);
			int minute = Integer.parseInt(task[EventColumns.Minute]);
			String[] date = task[EventColumns.Date].split("[.]");
			String command = task[EventColumns.Command];
			String[] args = new String[]{};
			if (!task[EventColumns.Args].isEmpty()) args = task[EventColumns.Args].split(" ");
			int period = 1; //пока не использую todo

			boolean enabled = false;
			if (task[EventColumns.Enabled].startsWith("t")) enabled = true;

			if (enabled &&
					Integer.parseInt(date[2]) >= nowYear &&
					Integer.parseInt(date[1]) >= nowMonth &&
					Integer.parseInt(date[0]) >= nowDay &&
					hour >= nowHour && minute >= nowMinute) {
				createTask(id, guildid, hour, minute, command, args, period);
				counter++;
			}
		}
		return counter;
	}

	private static void createTask(String id, String guildid, int hour, int minute, String command, String[] args, int period) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				IGuild guild = Bot.getClient().getGuildByID(Long.parseLong(guildid));
				IMessage message = guild.getDefaultChannel().sendMessage(Util.i("Запланированная задача:"));
				try {
					Bot.getCommandManager().getCommand(command).handle(guild, message, args);
					Bot.getCommandManager().getCommand("event").handle(guild, message, new String[]{"toggle", id});
				} finally {
					message.delete();
				}
			}
		}, calendar.getTime(), TimeUnit.DAYS.toMillis(period));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР