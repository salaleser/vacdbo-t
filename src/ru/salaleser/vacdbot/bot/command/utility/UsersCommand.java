package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import static ru.salaleser.vacdbot.Util.*;

public class UsersCommand extends Command {

	public UsersCommand() {
		super("users", UTILITY, "Показывает всех пользователей гильдии.", new String[]{"юзеры"});
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		int usersCounter = 0;
		int unknownSteamidsCounter = 0;
		StringBuilder builder = new StringBuilder(ub("Пользователи гильдии «" + guild.getName() + "»:") + "\n");
		for (IUser user : guild.getUsers()) {
			if (user == null) continue; //если пользователя нет в гильдии, то не показывать его
			if (user.isBot()) continue; //если бот, то тоже не показывать
			usersCounter++;
			String steamid = getSteamID64ByDiscordID(guild.getStringID(), user.getStringID());
			if (steamid == null) unknownSteamidsCounter++;
			builder.append("\n").append(b(getName(guild, user))).append(" ").append(code(user.getStringID())).append("|").append(code(steamid)).append(" — ").append(b("" + getRank(guild, user)));
			if (usersCounter % 20 == 0) {
				message.getChannel().sendMessage(builder.toString());
				builder = new StringBuilder();
			}
		}
		builder.append("\n\n").append("Всего пользователей Вашей гильдии: ").append(b(usersCounter + "")).append(". Из них с неизвестными SteamID: ").append(b(unknownSteamidsCounter + "")).append(".");
		message.getChannel().sendMessage(builder.toString());
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР