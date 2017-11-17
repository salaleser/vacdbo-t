package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Random;

public class RandomCommand extends Command {

	private String[] maps = {// FIXME: 16.11.2017 сделать один вариант, повторение кода!
			"de_train",
			"de_nuke",
			"de_dust2",
			"de_cache",
			"de_mirage",
			"de_inferno",
			"de_cobblestone",
			"de_overpass",
			"cs_office",
			"cs_agency"
	};

	public RandomCommand() {
		super("random", "**Описание:** Генерирует случайное число.\n" +
						"**Использование:** `~random [<диапазон>]`.\n" +
						"**Предустановки:** `~random` - генерация случайного числа от 1 до 6;" +
						"`~random map [<число_карт>]` - выдача случайной карты (можно указать количество карт).\n" +
						"**Пример:** `~random 20`, `~random map 4`.\n" +
						"**Примечание:** допустимые значения для чисел от 1 до 9999.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		int range = 6;

		if (args.length != 0) {
			//если предустановленный вариант:
			if (args[0].equals("map")) {
				int numberOfMaps = maps.length;
				if (args.length > 1 &&
						Utilities.isNumeric(args[1]) &&
						Integer.parseInt(args[1]) <= maps.length &&
						Integer.parseInt(args[1]) > 0) {
					numberOfMaps = Integer.parseInt(args[1]);
				} else {
					message.reply("\n**ошибка в первом аргументе** (использую значение " + numberOfMaps + ")");
				}
				int mapNumber = (int) (Math.random() * numberOfMaps);
				message.getChannel().sendMessage("Играть будем на карте " + maps[mapNumber]);
			}
			//если первый аргумент оказался подходящим числом:
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

	private String getRandomMap() {
		int random = new Random().nextInt(maps.length);
		return maps[random];
	}
}