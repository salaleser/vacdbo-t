package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.EventColumns;
import ru.salaleser.vacdbot.bot.Scheduler;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class EventCommand extends Command {

	private static final String EVENTS_CHANNEL_NAME = "events";

	public EventCommand() {
		super("event", UTILITY, "Управляет событиями.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~event <метод> <час> <минута> <задержка_(дней)> <команда> [<аргументы>]`.",
				"`~event` — показывает все события этой гильдии.",
				"`~event add training 17.02.18 14:00 Треня Повторяем кэш, разучиваем сплит А`.",
				"команда может быть: \"add\", \"remove\", \"on\", \"off\", \"toggle\";\n" +
						"тип — имя команды;\n" +
						"дата в формате ДД.ММ.ГГ."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String table = "events";
		if (args.length == 0) {
			String sql = "SELECT * FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' " + "ORDER BY guildid, date, hour, minute";
			String[][] data = DBHelper.executeQuery(sql);
			message.getChannel().sendMessage(Util.makeTable(table, new String[]{"*"}, data));
			Scheduler.refreshTasks();
			return;
		}

		String id = DBHelper.getNewId(table, "id");

		//сджоиванию все аргументы после пятого в аргументы команды:
		String commandArgs = "";
		if (args.length > 5) {
			String[] contentArray = Arrays.copyOfRange(args, 5, args.length);
			commandArgs = String.join(" ", contentArray);
		}

		String[] row = new String[]{id, guild.getStringID(), "19", "00", "0", "", commandArgs, "true"};

		try {
			if (args[1].matches("^\\d{1,2}$") && Integer.parseInt(args[1]) >= 0 && Integer.parseInt(args[1]) <= 24)
				row[EventColumns.Hour] = args[1];
			if (args[2].matches("^\\d{1,2}$") && Integer.parseInt(args[2]) >= 0 && Integer.parseInt(args[2]) < 60)
				row[EventColumns.Minute] = args[2];
			if (args[3].matches("^\\d{1,2}$")) {
				String date = LocalDateTime.now().plusDays(Long.parseLong(args[3])).format(DateTimeFormatter.ofPattern("dd.MM.yy"));
				row[EventColumns.Date] = date;
			}
			row[EventColumns.Command] = args[4];
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.error("Мало аргументов.");
		}

		String selectRowQuery = "SELECT * FROM " + table + " WHERE id = '" + args[1] + "'";
		String[] eventRow = DBHelper.executeQuery(selectRowQuery)[0];

		switch (args[0]) {
			case "add":
			case "create":
			case "make":
			case "+":
				if (DBHelper.insert(table, row)) {
					String sql = "SELECT * FROM " + table + " WHERE id = '" + id + "'";
					eventRow = DBHelper.executeQuery(sql)[0];
					message.reply("вы добавили событие " +
							"ID: " + Util.b(eventRow[EventColumns.ID]) + ", " +
							"время: " + Util.b(eventRow[EventColumns.Hour] + ":" + eventRow[EventColumns.Minute]) + ", " +
							"дата: " + Util.b(row[EventColumns.Date]) + ", " +
							"команда: " + Util.b(eventRow[EventColumns.Command]) + ", " +
							"аргументы: " + Util.b(eventRow[EventColumns.Args]) + ".");
				} else message.reply("произошла ошибка при попытке добавления события.");
				break;
			case "remove":
			case "delete":
			case "del":
			case "-":
				String deleteQuery = "DELETE FROM " + table + " WHERE id = '" + args[1] + "'";
				if (DBHelper.commit(table, deleteQuery, null)) message.reply("вы удалили событие из базы данных.");
				else message.reply("произошла ошибка при попытке удаления события.");
				break;
			case "on":
				eventRow[EventColumns.Enabled] = "true";
				if (DBHelper.update(table, eventRow)) message.reply("вы включили событие.");
				else message.reply("произошла ошибка при попытке включения события.");
				break;
			case "off":
				eventRow[EventColumns.Enabled] = "false";
				if (DBHelper.update(table, eventRow)) message.reply("вы выключили событие.");
				else message.reply("произошла ошибка при попытке выключения события.");
				break;
			case "toggle":
				if (eventRow[EventColumns.Enabled].startsWith("t")) eventRow[EventColumns.Enabled] = "false";
				else eventRow[EventColumns.Enabled] = "true";
				if (DBHelper.update(table, eventRow)) {
					if (message.getAuthor().isBot()) break; //бот выключает задачу после ее выполнения
					if (eventRow[EventColumns.Enabled].equals("true")) message.reply("вы включили событие.");
					else message.reply("вы выключили событие.");
				} else message.reply("произошла ошибка при попытке переключения события.");
				break;
		}
		Scheduler.refreshTasks();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР