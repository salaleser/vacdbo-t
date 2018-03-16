package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;

public class Listener {

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		event.getClient().changePlayingText(Bot.STATUS);
		Bot.setClient(event.getClient());

		Logger.info("Изменения в разрешениях команд: " + Util.fillCommandsAccessible());
		Logger.info("Изменения в уровнях доступа команд: " + Util.fillCommandsLevel());

		new Scheduler();

		StringBuilder guildsBuilder = new StringBuilder();
		for (IGuild guild : Bot.getClient().getGuilds()) {
			Bot.addGuild(guild);
			guildsBuilder.append(", ").append(guild.getName());
		}
		String guilds = guildsBuilder.toString();
		guilds = guilds.substring(2);
		Bot.gui.setConnected(Bot.getClient(), guilds);
		Logger.info("Успешно подключен. Всего серверов — " + Bot.getClient().getGuilds().size() + ": " + guilds);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР