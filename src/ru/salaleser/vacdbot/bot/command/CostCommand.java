package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.HttpClient;
import ru.salaleser.vacdbot.ParserInventory;
import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CostCommand extends Command {

	private final HttpClient httpClient = new HttpClient();

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
		ParserInventory parserInventory = new ParserInventory();
		ArrayList<String> items = parserInventory.parse(jsonInventory);
		System.out.println(items.size());
		message.getChannel().sendMessage("" +
				"Всего предметов в инвентаре (context=2): " + Util.b(items.size() + ""));

		int c = 0;
		float cost = 0;
		for (String itemWithSpaces : items) {
			//способ парсинга json (больше 20 цен предметов за короткий промежуток времени запрашивать невозможно):
//			String item = itemWithSpaces.replace(" ", "%20");
//			StringBuilder jsonItem = httpClient.connect("http://steamcommunity.com/market/priceoverview/" +
//					"?currency=5&country=us&appid=730&market_hash_name=" + item + "&format=json");
//			if (jsonItem != null) {
//				float itemCost = parserInventory.getItemsValue(jsonItem);
//				System.out.println(itemWithSpaces + " = " + itemCost);
//				cost += itemCost;
//				c++;
//			} else {
//				System.out.println("NULL: " + itemWithSpaces + " — не смогла...");
//			}
			//способ парсинга html магазина (такой же херовый):
			String itemWithPluses = itemWithSpaces.replace(" ", "+");
			float itemCost = parserInventory.parseMarketHtml("http://steamcommunity.com/market/search?appid=730&l=en&q=" +
					itemWithPluses);
			System.out.println(itemWithSpaces + " = " + itemCost);
			cost += itemCost;

//			TimeUnit.SECONDS.sleep(3);
		}

		message.getChannel().sendMessage("Из них посчитано: " + Util.b(c + "\n") +
				Util.u("Общая стоимость предметов (context=2): " + Util.b(cost + "")));
	}
}