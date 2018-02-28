package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class RolesCommand extends Command {

	public RolesCommand() {
		super("roles", UTILITY, "Устанавливает параметры ролям.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) {
			Logger.info("Ролей изменено: " + Util.updateRoles(null));
			String table = "roles";
			String query = "SELECT * FROM " + table + " WHERE guildid = '" + guild.getStringID() + "'";
			String[][] data = DBHelper.executeQuery(query);
			message.getChannel().sendMessage(Util.makeTable(table, new String[]{"*"}, data));
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР