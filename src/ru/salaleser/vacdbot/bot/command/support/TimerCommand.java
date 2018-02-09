package ru.salaleser.vacdbot.bot.command.support;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.concurrent.TimeUnit;

public class TimerCommand extends Command {

	public TimerCommand() {
		super("timer", new String[]{"t"}, 3);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String channelTopic = message.getChannel().getTopic();
		int timer = 5; // TODO: 10.12.2017 брать из базы
		if (args.length == 1) timer = Integer.parseInt(args[0]);
		int finalTimer = timer;
		message.getChannel().sendMessage(Util.i("Таймер установлен на " +
				Util.b(finalTimer + "") + " минут."));
		Thread thread = new Thread(() -> {
			IMessage tMessage = message.getChannel().sendMessage(Util.b("Осталось " +
					Util.u(finalTimer + "") + " минут"));
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
			message.getClient().changePlayingText(Bot.STATUS);

			message.reply("Время истекло!");
		});
		thread.start();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР