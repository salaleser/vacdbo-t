package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

import static ru.salaleser.vacdbot.Util.*;

public class RolesCommand extends Command {

	public RolesCommand() {
		super("roles", UTILITY, "Показывает роли гильдии.", new String[]{"роли"});
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		StringBuilder builder = new StringBuilder(ub("Роли гильдии «" + guild.getName() + "»:") + "\n");
		for (IRole role : guild.getRoles()) {
			if (role.getPosition() == 0) continue; //пропускаю роль @everyone
			builder.append("\n").append(code(String.valueOf(role.getPosition())))
					.append(" — ").append(b(role.getName())).append(": ");
			List<IUser> users = guild.getUsersByRole(role);
			if (users.isEmpty()) continue;
			for (IUser user : users) builder.append(getName(guild, user)).append(", ");
			builder.replace(builder.length() - 2, builder.length(), "; ");
		}
		message.getChannel().sendMessage(builder.toString());
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР