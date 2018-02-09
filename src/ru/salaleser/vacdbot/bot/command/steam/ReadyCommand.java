package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ReadyCommand extends Command {

	private IChannel channel;
	private IUser author;
	private String table = "ready";
	private String id;
	private StringBuilder description;
	private String date;
	private String time;
	private StringBuilder readyBuilder;
	private StringBuilder notReadyBuilder;

	public ReadyCommand() {
		super("ready", new String[]{"r"}, 3);
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Оповещает тиммейтов о готовности к игре или занятости.",
				"`~ready [<примечание>]`.",
				"`~ready` — оповещает обладателей указанной роли о готовности к игре;\n" +
						"`~ready remove` — удаляет из базы данных на сегодня;\n" +
						"`~ready not` — заносит в список занятых.",
				"`~ready сегодня играю до трех ночи`, `~ready две катки на трейне заверните`.",
				"нет."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		channel = message.getChannel();
		author = message.getAuthor();
		id = author.getStringID();
		description = new StringBuilder();
		date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YY"));
		time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

		//читаю пользователей из таблицы ready в строку:
		String sql = "SELECT date, time, id, description, isready FROM ready WHERE date = '" + date + "'";
		String[][] ready = DBHelper.executeQuery(sql);
		readyBuilder = new StringBuilder();
		notReadyBuilder = new StringBuilder();
		for (String[] row : ready) {
			IUser user = message.getClient().getUserByID(Long.parseLong(row[2]));
			if (row[4].equals("t")) {
				readyBuilder.append(row[0]).append(" ").append(row[1]).append(" — ").append(user.getName()).append(":");
				if (row[3].isEmpty()) {
					//лишнее условие для девочек: fixme hardcode
					if (user.getStringID().equals("278897176271126528")) readyBuilder.append(" (сегодня я свободна)\n");
					else readyBuilder.append(" (сегодня я свободен)\n");
				}
				else readyBuilder.append(row[3]).append("\n");
			} else {
				notReadyBuilder.append(row[0]).append(" ").append(row[1]).append(" — ").append(user.getName()).append(":");
				if (row[3].isEmpty()) {
					//лишнее условие для девочек: fixme hardcode
					if (user.getStringID().equals("278897176271126528")) notReadyBuilder.append(" (сегодня я занята)\n");
					else notReadyBuilder.append(" (сегодня я занят)\n");
				}
				else notReadyBuilder.append(row[3]).append("\n");
			}
		}
		if (readyBuilder.length() == 0) readyBuilder.append("(пока никто не готов)\n");
		if (notReadyBuilder.length() != 0) readyBuilder.append("\n").append(Util.ui("Сегодня играть не будут:")).append("\n");

		if (args.length == 0) {
			add();
		} else {
			switch (args[0]) {
				case "remove":
				case "delete":
					if (DBHelper.isAlreadyExistToday(table, "id", id, date)) {
						String deleteQuery = "DELETE FROM ready WHERE date = '" + date + "' AND id = '" + id + "'";
						if (DBHelper.commit(table, deleteQuery, null)) {
							message.reply(" вы удалили себя из базы данных.");
						} else {
							message.reply(" произошла ошибка при попытке удаления.");
						}
					} else {
						message.reply(" вы ещё не добавляли себя в базу данных сегодня.");
					}
					break;
				case "not":
					if (DBHelper.isAlreadyExistToday(table, "id", id, date)) {
						alreadyExist();
						break;
					}
					String[] args2 = Arrays.copyOfRange(args, 1, args.length);
					for (String arg : args2) description.append(" ").append(arg);
					String[] notReadyCols = new String[] {id, date, time, description.toString(), "false"};
					DBHelper.insert(table, notReadyCols);
					message.getChannel().sendMessage("<@&286563715157852180>, " +
							message.getAuthor() + " сегодня занят.");
					break;
				default:
					for (String arg : args) description.append(" ").append(arg);
					add();
			}
		}
	}

	private void alreadyExist() {
		channel.sendMessage("Вы уже добавлены в базу данных сегодня.");
		channel.sendMessage(Util.ui("Готовы играть:") + "\n" +
				readyBuilder.toString() + notReadyBuilder.toString());
	}

	private void add() {
		if (DBHelper.isAlreadyExistToday(table, "id", id, date)) {
			alreadyExist();
			return;
		}
		String[] readyCols = new String[] {id, date, time, description.toString(), "true"};
		DBHelper.insert(table, readyCols);
		//<@&286563715157852180> = @Офицеры fixme hardcode
		channel.sendMessage("<@&286563715157852180>, " +
				author + " готов играть!\n" + "Уже готовы играть:\n" + readyBuilder);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР