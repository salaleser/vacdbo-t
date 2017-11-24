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
import java.util.ArrayList;

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
	public ArrayList<String> parse(StringBuilder sb) {
		ArrayList<String> inventory = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			JSONArray descriptions = (JSONArray) jsonObject.get("descriptions");
			for (Object description : descriptions) {
				JSONObject item = (JSONObject) description;
				String market_hash_name = (String) item.get("market_hash_name");
				inventory.add(market_hash_name);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return inventory;
	}

	/**
	 * Считает стоимость инвентаря
	 *
	 * @return стоимость инвентаря
	 */
	public Float getItemsValue(StringBuilder sb) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(sb));
			boolean success = (boolean) jsonObject.get("success");
			if (success) {
				String lowest_price = (String) jsonObject.get("lowest_price");
				//выделяю цену как могу:
				String[] value = lowest_price.split(" ");
				String valueWithDots = value[0].replace(",", ".");
				return Float.parseFloat(valueWithDots);
			} else {
//				Logger.error("Ошибка что-то там"); todo написать тескт ошибки и импортировать моего логгера
				System.out.println("ФЕЙЛ: " + sb.toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0F;
	}

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
}
//[̲̅$̲̅(̲̅ ͡° ͜ʖ ͡°̲̅)̲̅$̲̅]