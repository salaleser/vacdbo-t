package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;

import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {

	public PollCommand() {
		super("poll", "создаёт голосование\n" +
				"Использование: ```~poll <??> [<??>]```\n" +
				"Пример: ```~poll ?? ??```");
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		if (args.length == 0) {
			message.reply("*нет аргументов*");
		} else {
			String question = args[0];

			IMessage questionMessage = message.getChannel().sendMessage("_*" + question + "*_");
			TimeUnit.MILLISECONDS.sleep(250);
			questionMessage.addReaction("👍");
			TimeUnit.MILLISECONDS.sleep(250);
			questionMessage.addReaction("👎");
		}
	}
}
