package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageHistory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearCommand extends Command {

	private ArrayList<IMessage> dump = new ArrayList<>();

	public ClearCommand() {
		super("clear", new String[]{"c", "del", "remove"}, 2);
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(
				"Удаляет сообщения.",
				"`~clear [<Discord_ID>] [<количество_соощений>] [<условие>]`.",
				"нет.",
				"`~clear @salaleser 10 \\u007e.*`.",
				"условие задаётся регулярным выражением."
				)
		);
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		IUser user = null;
		int limit = 20;
		limit = Integer.parseInt(DBHelper.getValueFromSettings(name, "limit"));
		String regexp = null;
		int index = 0;
		if (args.length != 0) {
			if (args[0].equals("yes")) {
				message.getClient().changePlayingText("удаляю сообщения");
				IMessage m = message.getChannel().sendMessage(Util.i("Удаляю " + (dump.size() - 2) + " сообщений..."));
				dump.add(m);
				message.delete();
				for (IMessage msg : dump) {
					msg.delete();
					TimeUnit.MILLISECONDS.sleep(500);
				}
				message.getClient().changePlayingText(Bot.status);
				return;
			}
			if (Util.isDiscordUser(args[index])) {
				String userid = args[index].replaceAll("[<@!>]", "");
				user = message.getClient().getUserByID(Long.parseLong(userid));
				index++;
			}
			if (Util.isNumeric(args[index])) {
				limit = Integer.parseInt(args[index]);
				index++;
			}
			if (args.length > index) regexp = args[index];
		}

		MessageHistory history = message.getChannel().getMessageHistory(limit);
		StringBuilder builder = new StringBuilder("По вашему запросу найдены сообщения:\n");
		dump = new ArrayList<>();
		for (IMessage msg : history) {
			if (user == null || msg.getAuthor() == user) {
				if (regexp == null) {
					dump.add(msg);
					builder.append(Util.block(msg.getContent()));
				} else {
					Pattern pattern = Pattern.compile(regexp);
					Matcher matcher = pattern.matcher(msg.getContent());
					if (matcher.matches()) {
						dump.add(msg);
						builder.append(Util.block(msg.getContent()));
					}
				}
			}
		}
		if (dump.isEmpty()) {
			message.getChannel().sendMessage(Util.i("По вашему запросу сообщений не найдено."));
			return;
		}
		try {
			IMessage replyMessage = message.getChannel().sendMessage(Util.i(builder.toString()));
			dump.add(0, replyMessage);
		} catch (DiscordException e) {
			IMessage errorMessage = message.getChannel().sendMessage(Util.i("Ошибка! Количество символов в сообщении превышает " +
					"максимально допустимое (2000 символов)"));
			dump.add(0, errorMessage);
		} finally {
			IMessage finalMessage = message.getChannel().sendMessage("Хотите удалить все " + (dump.size() - 1) +
					" сообщений? Для подтверждения наберите `~clear yes`");
			dump.add(0, finalMessage);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР