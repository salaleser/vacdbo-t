package ru.salaleser.vacdbot.bot.command.support;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static ru.salaleser.vacdbot.Util.b;
import static ru.salaleser.vacdbot.Util.u;

public class TransCommand extends Command {

	public TransCommand() {
		super("trans", SUPPORT, "RU<=>EN перевод с сайта http://wooordhunt.ru.", new String[]{"перевод"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~trans <русское_слово|английское_слово>`.",
				"нет.",
				"`~trans constellation`, `~trans кисломолочный`.",
				"пока перевод только одного слова с русского на английский и обратно."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String content = String.join("%20", args);
		ArrayList<String> result = getDataFromYandexTranslator(content);
		StringBuilder translation = new StringBuilder(b("Перевод слова " + u(content) + ":") + "\n");
		for (String line : result) translation.append(line).append(" ");
		message.getChannel().sendMessage(translation.toString());
	}

	private ArrayList<String> getDataFromYandexTranslator(String query) {
		ArrayList<String> result = new ArrayList<>();
//				final String request = "http://translate.yandex.ru/?lang=ru-en&text=" + query;
//				final String request = "https://translate.google.com/#ru/en/" + query;
		final String request = "http://wooordhunt.ru/word/" + query;
		System.out.println("Sending request..." + request);

		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.waitForBackgroundJavaScriptStartingBefore(100);
			webClient.waitForBackgroundJavaScript(100);
			webClient.getOptions().setTimeout(5000);

			WebRequest webRequest = new WebRequest(new URL(request));
			webRequest.setCharset(Charset.forName("utf-8"));

			HtmlPage page = webClient.getPage(webRequest);

			Document document = Jsoup.parse(page.asXml());

			//если иностранное слово:
			Elements english = document.getElementsByClass("t_inline_en");
			for (Element element : english) result.add(element.text());

			//если русское слово:
			Elements russian = document.getElementsByClass("ru_content");
			for (Element element : russian) result.add(element.text());

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР