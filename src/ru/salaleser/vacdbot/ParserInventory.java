package ru.salaleser.vacdbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

	/**
	 * Считает стоимость инвентаря
	 *
	 * @return стоимость инвентаря
	 */
	public float getItemsValue(StringBuilder json) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(json));
			boolean success = (boolean) jsonObject.get("success");
			if (success) {
				String lowest_price = (String) jsonObject.get("lowest_price");
				//выделяю цену как могу:
				String[] value = lowest_price.split(" ");
				String valueWithDots = value[0].replace(",", ".");
				return Float.parseFloat(valueWithDots);
			} else {
				Logger.error("getItemsValue: " + json);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
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

	public float getItemPriceByMarketCsgoCom(StringBuilder json) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(json));
			boolean success = (boolean) jsonObject.get("success");
			if (success) {
				String best_offer = (String) jsonObject.get("best_offer");
				return Float.parseFloat(best_offer);
			} else {
				String error = (String) jsonObject.get("error");
				byte[] utf8Bytes = error.getBytes("UTF8");
				String errorUtf8 = new String(utf8Bytes, "UTF8");
				Logger.debug(errorUtf8);
			}
		} catch (ParseException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
//[̲̅$̲̅(̲̅ ͡° ͜ʖ ͡°̲̅)̲̅$̲̅]