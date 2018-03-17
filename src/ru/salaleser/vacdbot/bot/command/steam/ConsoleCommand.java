package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.*;

import static ru.salaleser.vacdbot.Util.*;

public class ConsoleCommand extends Command {

	public ConsoleCommand() {
		super("console", STEAM, "Показывает полезные консольные команды.", new String[]{"binds", "консоль"});
	}

	public enum Columns {
		category, caption, description, content
	}

	private IMessage resultMessage;
	private IUser author;
	private String table = "csgo_binds";
	private String[][] categories;
	private IMessage bindsMessage;

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~console`.",
				"нет.",
				"`~console`.",
				"в разработке."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		this.author = message.getAuthor();
		IChannel channel = message.getChannel();
		String queryCategories = "SELECT " + Columns.category.name() + " FROM " + table + " GROUP BY " + Columns.category.name();
		categories = DBHelper.executeQuery(queryCategories);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < categories.length; i++) {
			builder.append(EmojisNumbers.values()[i].emoji().getUnicode()).append(" — ").append(b(categories[i][0])).append("\n");
		}
		resultMessage = channel.sendMessage(builder.toString());
		for (int i = 0; i < categories.length; i++) {
			delay(100);
			resultMessage.addReaction(EmojisNumbers.values()[i].emoji());
		}
		bindsMessage = channel.sendMessage(i("Нажмите на кнопку чтобы выбрать категорию…"));
	}

	private String[][] getBindsByCategory(String category) {
		String queryData = "SELECT * FROM " + table + " WHERE " + Columns.category.name() + " = '" + category + "'";
		return DBHelper.executeQuery(queryData);
	}

	@EventSubscriber
	public void onReactionAdd(ReactionAddEvent event) {
		if (event.getUser().isBot()) return; //добавлена реакция от бота
		if (event.getMessage() != resultMessage) return; //добавлена реакция на другое сообщение
		if (event.getUser() != author) return; //добавлена реакция не от автора исходного запроса
		if (!event.getReaction().getUsers().contains(Bot.getClient().getOurUser())) return; //не та реакция

		String category = null;
		String emoji = event.getReaction().getEmoji().getName();
		for (EmojisNumbers numbers : EmojisNumbers.values()) {
			if (emoji.equals(numbers.emoji().getUnicode())) {
				category = categories[numbers.ordinal()][0];
			}
		}

		String[][] data = getBindsByCategory(category);
		StringBuilder bindsBuilder = new StringBuilder(ub(category) + "\n\n");
		for (String[] row : data) {
			bindsBuilder
					.append(b(row[Columns.caption.ordinal()])).append(" — ")
					.append(row[Columns.description.ordinal()]).append(":\n")
					.append(block(row[Columns.content.ordinal()])).append("\n");
		}
		bindsMessage.edit(bindsBuilder.toString());

		for (IReaction reaction : resultMessage.getReactions()) {
			if (reaction.getUserReacted(event.getUser())) {
				if (reaction.getEmoji().getName().equals(emoji)) continue;
				resultMessage.removeReaction(event.getUser(), reaction);
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР