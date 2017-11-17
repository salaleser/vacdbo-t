package ru.salaleser.vacdbot.command;

import sx.blah.discord.handle.obj.IMessage;

public class ReportCommand extends Command {

	public ReportCommand() {
		super("report", "добавляет в базу данных подозрительного игрока для отслеживания его профиля" +
				"Использование: ```~report <SteamID64> [<описание>]```\n" +
				"Пример: ```~report 76561198095972970 использовал ник Какер, играли на ньюке впятером```");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		message.reply("*функция в разработке*");
	}
}
