package ru.salaleser.vacdbot.command;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleCommand extends Command {

	private static Pattern patternDomainName;
	private Matcher matcher;
	private static final String DOMAIN_NAME_PATTERN
			= "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";

	static {
		patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
	}

	public GoogleCommand() {
		super("google", "Использование: ```~google <поисковый_запрос>```\n" +
				"Пример: ```~google как похудеть```");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		StringBuilder query = new StringBuilder();
		for (String arg : args) query.append(arg).append("+");
		query.replace(query.length() - 1, query.length(), "");
		Set<String> result = getDataFromGoogle(query.toString());

		for (String temp : result) {
			System.out.println(temp);
		}
		System.out.println(result.size());

		StringBuilder results = new StringBuilder("*Вот что я нашёл вашему запросу:*\n");
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
					.userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
					.timeout(5000).get();

//			System.out.println(doc);

			// get all links
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String temp = link.attr("href");
				if (temp.startsWith("/url?q=") && !temp.contains("http://webcache.googleusercontent.com")) {
					//use regex to get domain name
//					result.add(getDomainName(temp));
					result.add(reduce(temp));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String getDomainName(String url) {
		String domainName = "";
		matcher = patternDomainName.matcher(url);
		if (matcher.find()) {
			domainName = matcher.group(0).toLowerCase().trim();
		}
		return domainName;
	}

	private String reduce(String string) {
		String splitted[] = string.replace("/url?q=", "").split("&");
		return splitted[0];
	}
}
