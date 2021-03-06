package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;

public class TrainingCommand extends Command {

	public TrainingCommand() {
		super("training", MISC, "Оповещает указанные роли и пользователей.");
	}

	public void handle(IGuild guild, IMessage message, String[] args) {
		ArrayList<IRole> roleList = new ArrayList<>();
		ArrayList<IUser> userList = new ArrayList<>();
		StringBuilder contentBuilder = new StringBuilder();

		//распихиваю роли из аргументов в лист:
		if (args.length == 0) {
			roleList.add(guild.getEveryoneRole());
		} else {
			for (String arg : args) {
				if (Util.isDiscordRole(arg, guild)) {
					long roleId = Long.parseLong(arg.replaceAll("[<@&>]", ""));
					roleList.add(guild.getRoleByID(roleId));
				} else if (Util.isDiscordUser(arg)) {
					long userId = Long.parseLong(arg.replaceAll("[<@!>]", ""));
					userList.add(guild.getUserByID(userId));
				} else { //в остальных случаях это слово для описания
					contentBuilder.append(" ").append(arg);
				}
			}
		}

		//создаю общий список пользователей указанных ролей:
		for (IRole role : roleList) userList.addAll(guild.getUsersByRole(role));

		//отправляю личные сообщения указанным пользователям:
		int counter = 0;
		for (IUser user : userList) {
			try {
				String ip = DBHelper.getOption(guild.getStringID(), "server", "ip");
				user.getOrCreatePMChannel().sendMessage(Util.b(Util.getTimeOfDay() + " " +
						message.getAuthor() + " приглашает Вас на тренировку.") + "\nОписание:" + contentBuilder +
						"\n\nТренировочный сервер: steam://" + ip + "//\n```connect " + ip + "; password 2002```");
				counter++;
			} catch (DiscordException e) {
				Logger.error(e.getErrorMessage(), guild);
			}
		}
		Logger.info("Оповещение отправлено личным сообщением " + counter + " пользователям, " +
				"не доставлено " + (userList.size() - counter) + " сообщений.", guild);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР