package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
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
		super("random", 999, "" +
				Util.b("Описание:") + " Генерирует случайное число.\n" +
				Util.b("Использование:") + " `~random [<диапазон>]`.\n" +
				Util.b("Предустановки:") + " `~random` - генерация случайного числа от 1 до 6;" +
				"`~random map [<число_карт>]` - выдача случайной карты (можно указать количество карт).\n" +
				Util.b("Пример:") + " `~random 20`, `~random map 4`.\n" +
				Util.b("Примечание:") + " допустимые значения для чисел от 1 до 9999.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		int range = 6;

		if (args.length != 0) {
			//если предустановленный вариант:
			if (args[0].equals("map")) {
				int numberOfMaps = maps.length;
				if (args.length > 1 &&
						Util.isNumeric(args[1]) &&
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
			if (Util.isNumeric(args[0])) {
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
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР