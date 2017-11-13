package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Bot;
import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

public class MapCommand extends Command {

	private static final String[] MAPS = {
			"de_train",
			"de_nuke",
			"de_dust2",
			"de_cache",
			"de_mirage",
			"de_inferno",
			"de_cobblestone",
			"de_overpass",
			"cs_office"
	};

	public MapCommand() {
		super("map", "Использование: ```~map [<количество_карт_в_порядке_желательности>]```\n" + "Пример: ```~map 3```\n" + "Допустимые значения: от 1 до 8");
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		int numberOfMaps = MAPS.length;
		if (args.length > 1 &&
				Utilities.isNumeric(args[1]) &&
				Integer.parseInt(args[1]) <= MAPS.length &&
				Integer.parseInt(args[1]) > 0) {
			numberOfMaps = Integer.parseInt(args[1]);
		} else {
			message.reply(help + "\n**ошибка в первом аргументе** (использую значение " + numberOfMaps + ")");
		}
		int mapNumber = (int) (Math.random() * numberOfMaps);
		message.getChannel().sendMessage("Играть будем на карте " + MAPS[mapNumber]);
	}
}
