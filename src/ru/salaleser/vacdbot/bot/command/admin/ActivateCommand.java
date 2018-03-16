package ru.salaleser.vacdbot.bot.command.admin;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

import static ru.salaleser.vacdbot.Config.COMMANDNAME;
import static ru.salaleser.vacdbot.Config.GUILDID;
import static ru.salaleser.vacdbot.Util.*;
import static ru.salaleser.vacdbot.bot.Bot.SALALESER;

public class ActivateCommand extends Command {

	public ActivateCommand() {
		super("activate", ADMIN, "Разрешает или запрещает команду.");
	}


	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~activate <имя_гильдии> <имя_команды> [-].",
				"`~activate <имя_гильдии> <имя_команды>` — включает команду.",
				"`~activate ` .",
				"пока только я могу включать и выключать команды, так что обращайтесь ко мне (Лёха <@!" + SALALESER + ">)."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String table = "settings";

		HashMap<String, String> argsMap = getArgs(guild, args);
		String guildid = argsMap.get(GUILDID);
		String commandname = argsMap.get(COMMANDNAME);
		if (commandname == null) {
			message.reply("не обнаружен обязательный аргумент <имя_команды>!");
			return;
		}

		String accessible = "1";
		String access = "разрешена";
		if (args.length > 2 && args[2].equals("-")) {
			accessible = "0";
			access = "запрещена";
		}
		String updateQuery = "UPDATE " + table + " SET value = ? " +
				"WHERE guildid = '" + guildid + "' AND command = '" + commandname + "' AND key = 'accessible'";
		if (DBHelper.commit(table, updateQuery, new String[]{accessible})) {
			message.reply("команда " + code(commandname) + " успешно " + access + " для гильдии " +
					b(Bot.getClient().getGuildByID(Long.parseLong(guildid)).getName()) + ".");
		} else {
			message.reply("ошибка! Изменений в разрешениях команды нет.");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР