package ru.salaleser.vacdbot.bot.command;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindCommand extends Command {

	public FindCommand() {
		super("find", "" +
				Util.b("Описание:") + " Возвращает четыре первые ссылки по запросу.\n" +
				Util.b("Использование:") + " `~find <поисковый_запрос>`.\n" +
				Util.b("Предустановки:") + " нет.\n" +
				Util.b("Пример:") + " `~find как похудеть`.\n" +
				Util.b("Примечание:") + " ничего особенного.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		StringBuilder query = new StringBuilder();
		for (String arg : args) query.append(arg).append("+");
		query.replace(query.length() - 1, query.length(), "");
		Set<String> result = getDataFromGoogle(query.toString());

		StringBuilder results = new StringBuilder(Util.i("Вот что я нашёл вашему запросу:\n"));
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