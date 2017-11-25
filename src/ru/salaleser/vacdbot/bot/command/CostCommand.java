package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.*;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CostCommand extends Command {

	private final HttpClient httpClient = new HttpClient();
	private ParserInventory parserInventory = new ParserInventory();

	public CostCommand() {
		super("cost", "Стомость шмоток и игр на акке.");
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		String steamid = Util.getSteamidByDiscordUser(message.getAuthor().getStringID());
		if (args.length > 0) {
			if (Util.isSteamID64(args[0])) {
				steamid = args[0];
			} else {
				message.reply(Util.b("ошибка в профиле") +
						" (проверяю стоимость предметов инвентаря " + message.getAuthor() + ")");//fixme повтор кода
			}
		} else {
			message.reply(Util.b("аргументы не заданы") +
					" (проверяю стоимость предметов инвентаря " + message.getAuthor() + ")");
		}
		String name = Util.getDiscordUserBySteamid(steamid);

		message.getChannel().sendMessage("Проверяю стоимость предметов инвентаря " + name + "'а...");
		StringBuilder jsonInventory = httpClient.connect("http://steamcommunity.com/inventory/" +
				steamid + "/730/2?l=russian&count=5000");
		if (jsonInventory == null) {
			message.getChannel().sendMessage(Util.b("Время ожидания вышло! Повторите запрос..."));
			return;
		}
		HashMap<String, String> items = parserInventory.parse(jsonInventory);
		message.getChannel().sendMessage("" +
				"Всего предметов в инвентаре (context=2): " + Util.b(items.size() + ""));

		float totalCost = 0;
		//неудачные способы не включаю в обработку todo удалить их вообще
		for (Map.Entry<String, String> item : items.entrySet()) {
//			totalCost += getPriceBySteamMarketJson(item.getValue());
//			totalCost += getPriceBySteamMarketHtml(item.getValue());
			totalCost += getPriceByMarketCsgoCom(item);
			//задержка для сайта https://market.csgo.com/docs/
			TimeUnit.MILLISECONDS.sleep(200);
		}

		message.getChannel().sendMessage("" +
				Util.u("Общая стоимость предметов (context=2): " + Util.b(totalCost + "")));
	}

	/**
	 * Способ парсинга json (больше 20 цен предметов за короткий промежуток времени запрашивать невозможно):
	 *
	 * @param itemWithSpaces название предмета
	 * @return стоимость предмета
	 */
	private float getPriceBySteamMarketJson(String itemWithSpaces) {
		float price = 0;
		String item = itemWithSpaces.replace(" ", "%20");
		StringBuilder jsonItem = httpClient.connect("http://steamcommunity.com/market/priceoverview/" +
				"?currency=5&country=us&appid=730&market_hash_name=" + item + "&format=json");
		if (jsonItem != null) {
			price = parserInventory.getItemsValue(jsonItem);
			Logger.debug(itemWithSpaces + " = " + price);
		} else {
			Logger.error("getPriceBySteamMarketJson: " + itemWithSpaces + " — не смогла...");
		}
		return price;
	}

	/**
	 * Способ парсинга html (больше 20 цен предметов за короткий промежуток времени запрашивать невозможно):
	 *
	 * @param itemWithSpaces название предмета
	 * @return стоимость предмета
	 */
	private float getPriceBySteamMarketHtml(String itemWithSpaces) {
		float price;
		String itemWithPluses = itemWithSpaces.replace(" ", "+");
		price = parserInventory.parseMarketHtml("http://steamcommunity.com/market/search?appid=730&l=en&q=" +
				itemWithPluses);
		Logger.debug(itemWithSpaces + " = " + price);
		return price;
	}

	/**
	 * Способ парсинга html сайта market.csgo.com
	 *
	 * @return стоимость предмета
	 */
	private float getPriceByMarketCsgoCom(Map.Entry<String, String> item) {
		float price = 0;
		String classid_instanceid = item.getKey();
		String market_hash_name = item.getValue();
		StringBuilder jsonItem = httpClient.connect("https://market.csgo.com/api/BuyOffers/" +
				classid_instanceid + "/?key=" + Config.getMarketCsgoComKey());
		if (jsonItem != null) {
			price = parserInventory.getItemPriceByMarketCsgoCom(jsonItem);
		} else {
			Logger.error("getPriceByMarketCsgoCom: " + market_hash_name + " — не смогла...");
		}
		Logger.debug(market_hash_name + " = " + price);
		return price;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР