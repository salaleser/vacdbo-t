package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", "" +
				Util.b("Описание:") + " Возвращает параметры из конфигурационного файла.\n" +
				Util.b("Использование:") + " `~get <аргумент [<аргумент>]>`.\n" +
				Util.b("Предустановки:") +
				" `steamid` — возвращает известные SteamID64;\n" +
				"`discordid` — возвращает известные Discord ID.\n" +
				Util.b("Пример:") + " `~get poll countdown`.\n" +
				Util.b("Примечание:") + " ничего особенного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length == 2 && args[0].equals("poll")) {
			message.getChannel().sendMessage(processPoll(Arrays.copyOfRange(args, 1, args.length)));
		} else if (args[0].equals("steamid")) {
			message.getChannel().sendMessage(String.valueOf(Util.mapSteamidDiscordid));
		} else if (args[0].equals("discordid")) {
			message.getChannel().sendMessage(String.valueOf(Util.mapDiscordidSteamid));
		} else {
			message.getChannel().sendMessage("отказ");
		}
	}

	private String processPoll(String[] args) {
		if (args[0].equals("countdown")) return String.valueOf(Config.getPollCountdown());
		return "отказ";
	}
}
