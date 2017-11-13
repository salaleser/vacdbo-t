package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

public class RandomCommand extends Command {

	public RandomCommand() {
		super("random", "Использование: ```~random [<конечное_значение> [<начальное_значение>]]```\n" + "Пример: ```~random 6 1```\n" + "Допустимые значения: от 1 до 9999");
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		int range = 6;
		if (args.length != 0) {
			if (Utilities.isNumeric(args[0])) {
				range = Integer.parseInt(args[0]);
			} else {
				message.reply("**неверный аргумент** (использую значение 6)");
			}
		} else {
			message.reply("**не указаны аргументы** (рандомлю от 1 до 6):");
		}
		int random = (int) (Math.random() * range) + 1;
		message.getChannel().sendMessage("Случайное число от 1 до " + range + ": `" + random + "`");
	}
}
