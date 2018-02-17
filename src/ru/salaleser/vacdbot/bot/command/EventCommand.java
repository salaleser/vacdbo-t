package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EventCommand extends Command {

	private static final String EVENTS_CHANNEL_NAME = "events";

	public EventCommand() {
		super("event");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Управляет событиями.",
				"`~event <команда> <тип> <дата> <время> [<заголовок> <содержание>]`.",
				"`~event` — показывает все события этой гильдии.",
				"`~event add training 17.02.18 14:00 Треня Повторяем кэш, разучиваем сплит А`.",
				"команда может быть \"add\" или \"remove\";\n" +
						"тип может быть \"training\", \"game\" или \"tournament\";\n" +
						"дата в формате ДД.ММ.ГГ, а время — ЧЧ:мм;" +
						"заголовок и содержание можно не указывать.\n" +
						"Команда в разработке."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) throws InterruptedException {
		if (args.length == 0) return;
		String table = "events";
		String id = DBHelper.getNewId(table, "id");

		//сджоиванию все аргументы после пятого в описание:
		String content = "Нет описания.";
		if (args.length > 5) {
			String[] contentArray = Arrays.copyOfRange(args, 5, args.length);
			content = String.join(" ", contentArray);
		}

		//наполняю строку таблицы БД перед вставкой:
		String[] row = new String[]{id, guild.getStringID(), "event",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YY")), "18:00", "", "", content};

		if (args.length > 1) row[2] = args[1]; //тип события
		if (args.length > 2 && args[2].matches("[0-3][0-9][.][01][0-9][.][01][0-9]")) row[3] = args[2]; //дата
		else message.reply("неправильный формат даты.");
		if (args.length > 3 && args[3].matches("[0-2][0-9][:][01][0-9]")) row[4] = args[3]; //время
		else message.reply("неправильный формат времени.");
		if (args.length > 4) row[5] = args[4];

		switch (args[0]) {
			case "add":
			case "create":
			case "make":
			case "+":
				DBHelper.insert(table, row);
				break;
			case "remove":
			case "delete":
			case "del":
			case "-":
				String deleteQuery = "DELETE FROM " + table + " WHERE id = '" + args[1] + "'";
				if (DBHelper.commit(table, deleteQuery, null)) {
					message.reply(" вы удалили событие из базы данных.");
				} else {
					message.reply(" произошла ошибка при попытке удаления.");
				}
				break;
			case "all":
				String sql = "SELECT * FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' " +
						"ORDER BY guildid, date, time";
				String[][] data = DBHelper.executeQuery(sql);
				message.getChannel().sendMessage(Util.makeTable(table, new String[] {"*"}, data));
				break;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР