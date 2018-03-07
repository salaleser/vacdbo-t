package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;

public class RoleCommand extends Command {

	public RoleCommand() {
		super("role", UTILITY, "Устанавливает параметры ролям.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String table = "roles";
		Logger.info("Ролей изменено: " + Util.updateRoles(null));
		if (args.length == 0) {
			String[][] data = DBHelper.executeQuery("SELECT * FROM " + table);
			StringBuilder rolesBuilder = new StringBuilder(Util.ub("Роли гильдии " + guild.getName() + ":"));
			for (String[] row : data) {
				if (Util.isDiscordRole(row[0], guild)) {
					IRole role = guild.getRoleByID(Long.parseLong(row[0]));
					if (role.getPosition() == 0) continue; //пропускаю роль @everyone
					rolesBuilder
							.append("\n")
							.append(role.getName())
							.append(" (")
							.append(Util.code(row[0]))
							.append(") — ")
							.append(Util.b(Util.getRoleRank(row[0])));
				}
			}
			message.getChannel().sendMessage(rolesBuilder.toString());
			return;
		}

		if (!Util.isDiscordRole(args[0], guild)) {
			message.reply("такой роли нет в Вашей гильдии.");
			return;
		}
		String roleid = args[0].replaceAll("[<@&>]", "");

		if (args.length < 2) {
			message.reply("должно быть не менее двух аргументов!");
			return;
		}

		if (!Util.isNumeric(args[1])) {
			message.reply("ранг должен быть числом.");
			return;
		}
		String rank = args[1];

		if (Integer.parseInt(Util.getRoleRank(roleid)) < Util.getRank(guild, message.getAuthor())) {
			message.reply("Вы не можете менять ранг старшей роли.");
			return;
		}

		if (Integer.parseInt(rank) < Util.getRank(guild, message.getAuthor())) {
			message.reply("Вы не можете установить ранг выше своего.");
			return;
		}

		//получаю нужную строку:
		String[] row = DBHelper.executeQuery("SELECT * FROM roles WHERE roleid = '" + roleid + "'")[0];

		//подменяю ячейку:
		row[1] = rank;

		//подменяю строку в таблице:
		if (DBHelper.update(table, row)) message.reply("ранг " + rank + " для роли " +
				guild.getRoleByID(Long.parseLong(roleid)).getName() + " успешно установлен.");
		else message.reply("ранг не установлен!");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР