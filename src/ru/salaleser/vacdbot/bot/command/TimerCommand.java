package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.util.concurrent.TimeUnit;

public class TimerCommand extends Command {

	public TimerCommand() {
		super("timer", "" +
				Util.b("Описание:") + " По истечении времени проигрывает звук.\n" +
				Util.b("Использование:") + " `~timer [<время_в_минутах>]`.\n" +
				Util.b("Предустановки:") + " `~timer` — устанавливает таймер на 5 минут.\n" +
				Util.b("Пример:") + " `~timer 20`.\n" +
				Util.b("Примечание:") + " таймер как таймер.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		String channelTopic = message.getChannel().getTopic();
		int timer = 5;
		if (args.length == 1) timer = Integer.parseInt(args[0]);
		int finalTimer = timer;
		message.getChannel().sendMessage(Util.i("Таймер установлен на " +
				Util.b(finalTimer + "") + " минут."));
		Thread thread = new Thread(() -> {
			IMessage tMessage = message.getChannel().sendMessage(Util.b("Осталось " + Util.u(finalTimer + "") + " минут"));
			for (int i = finalTimer; i > 0; i--) {
				message.getChannel().changeTopic("таймер: " + i + " мин");
				message.getClient().changePlayingText("таймер: " + i + " мин");
				tMessage.edit(Util.b("Осталось " + Util.u(i + "") + " минут"));
				try {
					TimeUnit.MINUTES.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			message.getChannel().changeTopic(channelTopic);
			message.getClient().changePlayingText(Bot.status);
			message.reply("Время истекло!");
		});
		thread.start();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР