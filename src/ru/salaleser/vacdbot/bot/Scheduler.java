package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.bot.task.CheckSuspectsTask;
import ru.salaleser.vacdbot.bot.task.InviteToVoiceChatTask;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	public Scheduler() {
		runTasks();
	}

	private static void runTasks() {
		Calendar evening = Calendar.getInstance();
		evening.set(Calendar.HOUR_OF_DAY, 20);
		evening.set(Calendar.MINUTE, 0);
		evening.set(Calendar.SECOND, 0);
		evening.set(Calendar.MILLISECOND, 0);

		Calendar custom = Calendar.getInstance();
		custom.set(Calendar.HOUR_OF_DAY, 4);
		custom.set(Calendar.MINUTE, 59);


		Timer time = new Timer();
		CheckSuspectsTask checkSuspectsTask = new CheckSuspectsTask();
		InviteToVoiceChatTask inviteToVoiceChatTask = new InviteToVoiceChatTask();

		//каждые два часа проверяю подозреваемых:
		time.schedule(checkSuspectsTask, 10000, TimeUnit.HOURS.toMillis(1));
		time.schedule(inviteToVoiceChatTask, 30000, TimeUnit.SECONDS.toMillis(15));
	}
}
