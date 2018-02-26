package ru.salaleser.vacdbot.bot.command.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FindCommand extends Command {

	public FindCommand() {
		super("find", SUPPORT, "Возвращает четыре первые ссылки по запросу в гугл.");
	}

	@Override
	public void help(IMessage message) {
		message.reply(buildHelp(description,
				"`~find <поисковый_запрос>`.",
				"нет.",
				"`~find как похудеть`.",
				"ничего особенного."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		StringBuilder query = new StringBuilder();
		for (String arg : args) query.append(arg).append("+");
		query.replace(query.length() - 1, query.length(), "");
		Set<String> result = getDataFromGoogle(query.toString());

		StringBuilder results = new StringBuilder(Util.i("Вот что я нашёл вашему запросу:") + "\n");
		int c = 0;
		for (String line : result) {
			if (c < 4) results.append(line).append("\n");
			c++;
		}
		message.getChannel().sendMessage(results.toString());
	}

	private Set<String> getDataFromGoogle(String query) {
		Set<String> result = new HashSet<>();
		String request = "https://www.google.com/search?q=" + query + "&num=20";
		System.out.println("Sending request..." + request);
		try {
			Document doc = Jsoup
					.connect(request)
					.userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/userBot.html)")
					.timeout(5000).get();
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String temp = link.attr("href");
				if (temp.startsWith("/url?q=") && !temp.contains("http://webcache.googleusercontent.com")) {
					result.add(reduce(temp));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String reduce(String string) {
		String splitted[] = string.replace("/url?q=", "").split("&");
		return splitted[0];
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР