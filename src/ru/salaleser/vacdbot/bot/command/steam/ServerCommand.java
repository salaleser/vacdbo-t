package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class ServerCommand extends Command {

	public ServerCommand() {
		super("server", new String[]{"сервер"});
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String trainingServerAddress = DBHelper.getOption(guild.getStringID(), "server", "ip");
		message.getChannel().sendMessage("" +
				"Тренировочный сервер: " + "steam://" + trainingServerAddress + "//\n" +
				"```connect " + trainingServerAddress + "; password 2002```" +
				"```connect " + trainingServerAddress + "; password 2002; rcon_password ```");

		message.getChannel().sendMessage("\nsteam://open/servers");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР