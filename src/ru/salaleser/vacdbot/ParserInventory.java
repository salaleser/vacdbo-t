package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

/**
 * Парсит json с инвентарём и json'ы со стоимостями предметов
 */
public class ParserInventory {
	/**
	 * Парсит json с инвентарём
	 *
	 * @param sb json
	 * @return стоимость всех предметов инвентаря (не всех, пока только context=2) todo добавить контексты
	 */
	public HashMap<String, String> parse(StringBuilder sb) {
		HashMap<String, String> inventory = new HashMap<>();
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray descriptions = (JSONArray) jsonObject.get("descriptions");
			for (Object description : descriptions) {
				JSONObject item = (JSONObject) description;
				//сразу исключить медали, трофеи и прочие непродаваемые предметы:
				if ((long) item.get("tradable") == 0) continue;
				String classid = (String) item.get("classid");
				String instanceid = (String) item.get("instanceid");
				String classid_instanceid = classid + "_" + instanceid;
				String market_hash_name = (String) item.get("market_hash_name");
				inventory.put(classid_instanceid, market_hash_name);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return inventory;
	}

	//todo убрать нерабочие способы
	public float parseMarketHtml(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (document != null) {
			Elements elements = document.select("span.normal_price");
			String[] normal_price = elements.get(1).text().split(" ");
			return Float.parseFloat(normal_price[0].substring(1));
		}
		return 0;
	}

	//не вышло опять... на пушки страница без js приходит, а на кейсы -- уже с ним...
	//можно потом еще допилить попробовать этот метод с использованием HtmlUnit todo
	public String parseMarketHtmlListings(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (document == null) return "document=null";
		Elements prices = document.getElementsByClass("market_listing_price market_listing_price_without_fee");
		System.out.println(prices);
		Element price = prices.first();
		System.out.println(price);
		return price.text();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР
//[̲̅$̲̅(̲̅ ͡° ͜ʖ ͡°̲̅)̲̅$̲̅]