package ru.salaleser.vacdbot.bot.command;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TransCommand extends Command {

	public TransCommand() {
		super("trans", "" + Util.b("Описание:") + " Конвертирует слова в слова.\n" + Util.b("Использование:") + " `~trans <слово>`.\n" + Util.b("Предустановки:") + " нет.\n" + Util.b("Пример:") + " `~trans раш б с диглами`.\n" + Util.b("Примечание:") + " команда в разработке.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		StringBuilder query = new StringBuilder();
		//перегоняю все аргументы в одну строку
		for (String arg : args) query.append(arg).append("%20");
		query.replace(query.length() - 3, query.length(), "");
		ArrayList<String> result = getDataFromYandexTranslator(query.toString());

		StringBuilder translation = new StringBuilder("");
		for (String line : result) translation.append(line).append(" ");
		message.getChannel().sendMessage(translation.toString());
	}

	private ArrayList<String> getDataFromYandexTranslator(String query) {
		ArrayList<String> result = new ArrayList<>();
		//		final String request = "http://translate.yandex.ru/?lang=ru-en&text=" + query;
		//		final String request = "https://translate.google.com/#ru/en/" + query;
		final String request = "https://google.com";
		System.out.println("Sending request..." + request);

		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.waitForBackgroundJavaScriptStartingBefore(1000);
			webClient.waitForBackgroundJavaScript(1000);
			webClient.getOptions().setTimeout(5000);

			WebRequest webRequest = new WebRequest(new URL(request));
			webRequest.setCharset(Charset.forName("utf-8"));

			HtmlPage page = webClient.getPage(webRequest);

			Document document = Jsoup.parse(page.asXml());
			System.out.println("\n\n#############################################################################\n\n" +
					document + "\n\n#############################################################################\n\n");
			Elements elements = document.select("pre");
			for (Element element : elements) result.add(element.text());
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}