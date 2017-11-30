package ru.salaleser.vacdbot.bot.task;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.HtmlUnitContextFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.TimerTask;

public class SDTDFreeSlotCheckerTask extends TimerTask{
	@Override
	public void run() {
		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			webClient.getOptions().setThrowExceptionOnScriptError(true);

			final HtmlPage page = webClient.getPage("https://map2.7dtd.zone/static/index.html");
			System.out.println(page.getBody().getTextContent());

//			Document document = Jsoup.parse(page.asXml());
//			System.out.println(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР