package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.bot.task.CheckSuspectsTask;
import ru.salaleser.vacdbot.bot.task.InviteToVoiceChatTask;
import ru.salaleser.vacdbot.bot.task.SDTDFreeSlotCheckerTask;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

class Scheduler {

	Scheduler() {
		runTasks();
	}

	private static void runTasks() {
		Calendar everyday20 = Calendar.getInstance();
		everyday20.set(Calendar.HOUR_OF_DAY, 20);
		everyday20.set(Calendar.MINUTE, 0);
		everyday20.set(Calendar.SECOND, 0);
		everyday20.set(Calendar.MILLISECOND, 0);

		Calendar everyday18 = Calendar.getInstance();
		everyday18.set(Calendar.HOUR_OF_DAY, 18);
		everyday18.set(Calendar.MINUTE, 0);
		everyday18.set(Calendar.SECOND, 0);
		everyday18.set(Calendar.MILLISECOND, 0);

		Timer time = new Timer();
		CheckSuspectsTask checkSuspects = new CheckSuspectsTask();
		InviteToVoiceChatTask inviteToVoiceChat = new InviteToVoiceChatTask();
		SDTDFreeSlotCheckerTask sdtdFreeSlotCheckerTask = new SDTDFreeSlotCheckerTask();

		time.schedule(checkSuspects, everyday20.getTime(), TimeUnit.HOURS.toMillis(8));
		time.schedule(inviteToVoiceChat, everyday18.getTime(), TimeUnit.MINUTES.toMillis(20));
//		time.schedule(sdtdFreeSlotCheckerTask, 5000, TimeUnit.HOURS.toMillis(10));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР