package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class ReadyCommand extends Command {

	public ReadyCommand() {
		super("ready", "" + Util.b("Описание:") + " Оповещает тиммейтов о готовности к игре " +
						"или занятости.\n" +
				Util.b("Использование:") + " `~ready [<примечание>]`.\n" +
				Util.b("Предустановки:") + " `~ready` — показывает уже готовых тиммейтов;\n" +
						"`~ready remove` — удаляет из базы данных на сегодня;" +
						"`~ready not` — заносит в список занятых.\n" +
				Util.b("Пример:") + " `~ready сегодня играю до трех ночи`, " +
						"`~ready две катки на трейне заверните`.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		String table = "ready";
		String id = message.getAuthor().getStringID();
		StringBuilder description = new StringBuilder();
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YY"));
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

		//читаю пользователей из таблицы ready в строку:
		// FIXME: 30.11.2017 фильтровать по дате
		String sql = "SELECT date, time, id, description, isready FROM ready WHERE date = '" + date + "'";
		ArrayList<String[]> ready = DBHelper.executeQuery(sql);
		StringBuilder readyBuilder = new StringBuilder();
		StringBuilder notreadyBuilder = new StringBuilder();
		for (String[] row : ready) {
			String username = Util.b(message.getClient().getUserByID(Long.parseLong(row[2])).getName());
			if (row[4].equals("1")) {
				readyBuilder.append(row[0]).append(" ").append(row[1]).append(" — ").append(username).append(":");
				if (row[3].isEmpty()) readyBuilder.append(" (сегодня я свободен)\n");
				else readyBuilder.append(row[3]).append("\n");
			} else {
				notreadyBuilder.append(row[0]).append(" ").append(row[1]).append(" — ").append(username).append(":");
				if (row[3].isEmpty()) notreadyBuilder.append(" (сегодня я занят)\n");
				else notreadyBuilder.append(row[3]).append("\n");
			}
		}
		if (readyBuilder.length() == 0) readyBuilder.append("(пока никто не готов)\n");
		if (notreadyBuilder.length() != 0) readyBuilder.append(Util.ui("Сегодня играть не будут:")).append("\n");

		if (args.length == 0) {
			message.getChannel().sendMessage(Util.ui("Готовы играть:") + "\n" + readyBuilder.toString() +
					notreadyBuilder.toString());
		} else {
			switch (args[0]) {
				case "remove":
				case "delete":
					if (DBHelper.isAlreadyExistsToday(table, "id", id, date)) {
						String deleteQuery = "DELETE FROM ready WHERE date = '" + date + "' AND id = '" + id + "'";
						if (DBHelper.delete(deleteQuery)) message.reply(" вы удалили себя из базы данных.");
						else message.reply(" произошла ошибка при попытке удаления.");
					} else {
						message.reply(" вы ещё не добавляли себя в базу данных сегодня.");
					}
					break;
				case "not":
					if (DBHelper.isAlreadyExistsToday(table, "id", id, date)) {
						// FIXME: 30.11.2017 изменить строку в базе данных, isready=false
						message.getChannel().sendMessage("Пользователь уже был добавлен в базу данных сегодня.");
						break;
					}
					String[] args2 = Arrays.copyOfRange(args, 1, args.length);
					for (String arg : args2) description.append(" ").append(arg);
					DBHelper.insert(table, id, date, time, description.toString(), "0");
					message.getChannel().sendMessage("<@&286563715157852180>, " +
							message.getAuthor() + " сегодня занят.");
					break;
				case "all":
					ArrayList<String[]> all = DBHelper.executeQuery("SELECT * FROM ready");
					StringBuilder builder = new StringBuilder();
					for (String[] row : all) {
						builder.append("│");
						for (String element : row) builder.append(Util.addSpaces(element)).append("│");
						builder.append("\n");
					}
					message.getChannel().sendMessage(Util.block(builder.toString()));
					break;
				default:
					if (DBHelper.isAlreadyExistsToday(table, "id", id, date)) {
						message.getChannel().sendMessage("Пользователь уже был добавлен в базу данных сегодня.");
						break;
					}
					for (String arg : args) description.append(" ").append(arg);
					DBHelper.insert(table, id, date, time, description.toString(), "1");
					//<@&286563715157852180> = @Офицеры fixme hardcode
					message.getChannel().sendMessage("<@&286563715157852180>, " +
							message.getAuthor() + " готов играть!\n" + "Уже готовы играть:\n" + readyBuilder);
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР