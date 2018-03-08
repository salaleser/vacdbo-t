package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageHistory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearCommand extends Command {

	private ArrayList<IMessage> dump = new ArrayList<>();
	private IMessage replyMessage;
	private IMessage errorMessage;
	private IMessage finalMessage;
	private IMessage message;
	private IUser author;

	public ClearCommand() {
		super("clear", UTILITY, "Удаляет сообщения.", new String[]{"c"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~clear [<Discord_ID>] [<количество_соощений>] [<условие>]`.",
				"`~clear` — удаляет 10 сообщений всех пользоваетлей.",
				"`~clear @salaleser 10 ^\\u007e.*`.",
				"условие задаётся регулярным выражением."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		IUser user = null;
		this.message = message;
		this.author = message.getAuthor();
		int limit = Integer.parseInt(DBHelper.getOption(guild.getStringID(), name, "limit"));
		String regexp = null;
		int index = 0;
		if (args.length != 0) {
			if (args[0].equals("yes") || args[0].equals("y")) {
				clear();
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
			replyMessage = message.getChannel().sendMessage(Util.i(builder.toString()));
			dump.add(0, replyMessage);
		} catch (DiscordException e) {
			errorMessage = message.getChannel().sendMessage(Util.i("Ошибка! Количество символов в сообщении превышает " +
					"максимально допустимое (2000 символов)"));
			dump.add(0, errorMessage);
		} finally {
			finalMessage = message.getChannel().sendMessage(Util.i("Хотите удалить все " + (dump.size() - 1) +
					" сообщений? Для подтверждения наберите `~clear yes` (или `~c y`)"));
			Util.delay(100);
			finalMessage.addReaction("✅");
			Util.delay(100);
			finalMessage.addReaction("❌");
			dump.add(0, finalMessage);
		}
	}

	private void clear() {
		message.getClient().changePlayingText("удаляю сообщения");
		IMessage m = message.getChannel().sendMessage(Util.i("Удаляю " + (dump.size() - 2) + " сообщений..."));
		dump.add(m);
		message.delete();
		for (IMessage msg : dump) {
			msg.delete();
			Util.delay(500);
		}
		message.getClient().changePlayingText(Bot.STATUS);
	}

	@EventSubscriber
	public void onReactionAdd(ReactionAddEvent event) {
		String emoji = event.getReaction().getUnicodeEmoji().getUnicode();
		if (emoji.equals("✅") && event.getMessage() == finalMessage && event.getUser() == author) {
			clear();
		}
		if (emoji.equals("❌") && event.getMessage() == finalMessage && event.getUser() == author) {
			if (replyMessage != null) {
				replyMessage.delete();
				Util.delay(500);
			}
			if (errorMessage != null) {
				errorMessage.delete();
				Util.delay(500);
			}
			if (finalMessage != null) {
				finalMessage.delete();
				Util.delay(500);
			}
			message.delete();
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР