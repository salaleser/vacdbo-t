package ru.salaleser.vacdbot.bot.command;

import sx.blah.discord.handle.obj.IMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StatusCommand extends Command {

	public StatusCommand() {
		super("status", 3, "**Описание:** Устанавливает боту статус.\n" +
				"**Использование:** `~status [<ваш_остроумный_статус>]`.\n" +
				"**Предустановки:** `~status` — гениальный статус для бота и в чат с сайта statusi.com.ru;\n" +
				"`~status bot` — немного информации о боте;\n" +
				"`~status КТО` — бонус.\n" +
				"**Пример:** `~status Понедельник - это не день недели, а состояние души....`.\n");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		//заполняю массив строками из файла
		ArrayList<String> list = new ArrayList<>();
		try {
			File file = new File("txt/statusi.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = reader.readLine();
			while (line != null) {
				list.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (args.length == 0) {
			message.getClient().changePlayingText(getRandomLessThen32(list));
			message.getChannel().sendMessage(getRandom(list));
			return;
		}

		if (args[0].equals("bot")) message.getChannel().sendMessage(message.getClient().getApplicationName() +
				" by " + message.getClient().getApplicationOwner());

		if (args[0].equals("КТО")) message.getChannel().sendMessage(
				"<:richter:286853903277096961>" +
						"<:tony:286857551042052097>" +
						"<:rasmus:286853270746431488>" +
						"<:louie:286855987485212672>" +
						"<:dennis:286858463827722240>" +
						"<:yanica:346606870255108117>");

		StringBuilder arguments = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			arguments.append(args[i]);
			if (i + 1 != args.length) arguments.append(" ");
		}
		message.getClient().changePlayingText(arguments.toString());
	}

	private String getRandomLessThen32(ArrayList<String> list) {
		ArrayList<String> sublists = new ArrayList<>();
		for (String s : list) {
			if (s.length() < 32) sublists.add(s);
		}
		return sublists.get(ThreadLocalRandom.current().nextInt(sublists.size()));
	}

	private String getRandom(ArrayList<String> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР