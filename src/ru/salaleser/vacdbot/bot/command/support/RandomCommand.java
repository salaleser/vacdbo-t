package ru.salaleser.vacdbot.bot.command.support;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Random;

public class RandomCommand extends Command {

	private String[] maps = {// FIXME: 16.11.2017 сделать один вариант, повторение кода!
			"de_nuke",
			"de_train",
			"de_cache",
			"de_inferno",
			"de_dust2",
			"de_mirage",
			"de_overpass",
			"de_cobblestone"
	};

	public RandomCommand() {
		super("random", SUPPORT, "Генерирует случайное число.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~random [<диапазон>]`.",
				"`~random` - генерация случайного числа от 1 до 6;\n" +
						"`~random map [<число_карт>]` - выдача случайной карты (можно указать количество карт).",
				"`~random 20`, `~random map 4`.",
				"допустимые значения для чисел от 2 до 999 999 999."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		switch (args.length) {
			case 0:
				message.getChannel().sendMessage(getRandom("6"));
				break;
			case 1:
				if (args[0].equals("map")) message.getChannel().sendMessage(getMap("666"));
				else message.getChannel().sendMessage(getRandom(args[0]));
				break;
			case 2:
				if (args[0].equals("map")) message.getChannel().sendMessage(getMap(args[1]));
				break;
		}
	}

	private String getRandom(String stringRange) {
		int range;
		if (Util.isNumeric(stringRange)) range = Integer.parseInt(stringRange);
		else return Util.b("Неверный аргумент");
		int random = (int) (Math.random() * range) + 1;
		return Util.i("Случайное число от 1 до " + range + ": `" + random + "`");
	}

	private String getMap(String stringRange) {
		int range;
		if (Util.isNumeric(stringRange)) {
			range = Integer.parseInt(stringRange);
			if (range < 2) range = 2;
			else if (range > maps.length) range = maps.length;
		} else {
			return Util.b("Неверный аргумент");
		}
		return Util.i("Играть будем на карте " + Util.b(maps[new Random().nextInt(range)]));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР