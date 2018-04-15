package ru.salaleser.vacdbot.bot.command.steam;

import com.vdurmont.emoji.EmojiManager;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;

import java.util.List;

import static ru.salaleser.vacdbot.Util.*;

public class DiscussCommand extends Command {

	public DiscussCommand() {
		super("discuss", STEAM, "Создает обсуждение.", new String[]{"обсуждение"});
	}

	private IMessage titleMessage;
	private IMessage contentMessage;

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~discuss`.",
				"нет.",
				"`~discuss Сообщение`.",
				"нет."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		titleMessage = message.getChannel().sendMessage("«" + ub(String.join(" ", args)) +
				"», — сказал " + message.getAuthor().mention(true) + "\n");

		delay(100);
		titleMessage.addReaction(EmojiManager.getByUnicode("👍"));
		delay(100);
		titleMessage.addReaction(EmojiManager.getByUnicode("👎"));

		contentMessage = message.getChannel().sendMessage(i("Нажмите на кнопку…"));
	}

	@EventSubscriber
	public void onReactionAdd(ReactionAddEvent event) {
		if (event.getUser().isBot()) return; //добавлена реакция от бота
		if (event.getMessage() != titleMessage) return; //добавлена реакция на другое сообщение

		String titleContent = titleMessage.getContent();
		List<IReaction> reactions = titleMessage.getReactions();
		delay(100);
		titleMessage.delete();
		delay(100);
		titleMessage = event.getChannel().sendMessage(titleContent);
		for (IReaction reaction : reactions) {
			delay(100);
			titleMessage.addReaction(reaction);
		}

		String contentContent = contentMessage.getContent();
		delay(100);
		contentMessage.delete();
		delay(100);
		contentMessage = event.getChannel().sendMessage(contentContent);
		if (!contentMessage.getMentions().contains(event.getUser())) {
			contentContent = "\n\n" + event.getUser().mention(true) +
					" отреагировал " + event.getReaction().getEmoji().getName();
			contentMessage.edit(contentContent);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР