package ru.salaleser.vacdbot.bot.task;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.ReportCommand;

import java.util.TimerTask;

public class CheckSuspectsTask extends TimerTask {

	public void run() {
		ReportCommand reportCommand = (ReportCommand) Bot.getCommandManager().getCommand("report");
		reportCommand.checkSuspects();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР