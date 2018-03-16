package ru.salaleser.vacdbot.bot.command.steam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;

public class CostCommand extends Command {

	private IGuild guild;
	private final HttpClient httpClient = new HttpClient();
	private ParserInventory parserInventory = new ParserInventory();

	public CostCommand() {
		super("cost", STEAM, "Считает стоимость инвентаря.");
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		this.guild = guild;
		IChannel channel = message.getChannel();
		IUser user = message.getAuthor();
		String discordid = user.getStringID();
		String steamid = Util.getSteamID64ByDiscordID(guild.getStringID(), user.getStringID());
		for (String arg : args) {
			if (Util.isCommunityURL(arg)) arg = Util.getSteamID64ByCommunityURL(arg);
			if (Util.isSteamID64(arg)) {
				steamid = arg;
				discordid = Util.getDiscordidBySteamid(steamid);
			} else if (Util.isDiscordUser(arg)) {
				discordid = arg.replaceAll("[<@!>]", "");
				steamid = Util.getSteamID64ByDiscordID(guild.getStringID(), discordid);
			}
		}
		user = guild.getUserByID(Long.parseLong(discordid));

		channel.sendMessage(Util.i("Проверяю стоимость предметов инвентаря " + user.getName() + "..."));
		StringBuilder jsonInventory;
		long totalCost = 0;
		//сначала беру джейсон со всеми предметами второго контекста инвентаря:
		jsonInventory = httpClient.connect("http://steamcommunity.com/inventory/" +
				steamid + "/730/2?l=russian&count=5000");
		if (jsonInventory == null) {
			Logger.error("Время ожидания превышено! Повторите операцию.", guild);
			return;
		}
		//разбираю только classid вместе с instanceid в одну строку через знак "_" и market_hash_name:
		HashMap<String, String> items = parserInventory.parse(jsonInventory);

		//уже известно количество предметов, показываю в дискорд:
		channel.sendMessage("Всего предметов в инвентаре (context=2): " +
				Util.b(items.size() + "\n") + Util.i("Считаю стоимость инвентаря..."));

		//для каждого предмета в отдельности беру текущую стоимость и суммирую их:
		for (Map.Entry<String, String> item : items.entrySet()) {
			totalCost += getPriceByMarketCsgoCom(item);
			//по правилам сайта https://market.csgo.com/docs/ не более пяти запросов в секунду:
			Util.delay(200);
		}

		//целочисленная сумма в рублях и копейках:
		String rubkop = Util.toRubKop(String.valueOf(totalCost));
		message.reply("Общая стоимость предметов (context=2) " + user.getName() + ": " + Util.b(rubkop));
	}

	/**
	 * Способ парсинга json с сайта market.csgo.com. Кулдаун не дают, поэтому можно парсить все предметы без
	 * проблем, за исключением одной — не все предметы есть в этом магазине, например, некоторые относительно
	 * редкие стикеры
	 *
	 * @param item предмет
	 * @return стоимость
	 */
	private long getPriceByMarketCsgoCom(Map.Entry<String, String> item) {
		long price = 0;
		String url = "https://market.csgo.com/api/SearchItemByName/";
		String market_hash_name = item.getValue().replaceAll(" ", "%20");
		StringBuilder jsonMarketTM = new StringBuilder();

		//проверка на исключение чтобы не пропустить предмет:
		boolean wasSocketTimeoutException = true;
		//считаю количество таймаутов чтобы не попасть в вечный цикл:
		int counterOfTimeouts = 0;
		while (wasSocketTimeoutException) {
			//если таймаутов слишком много, то придётся повторить операцию позднее:
			if (counterOfTimeouts > 20) {
				Logger.error("Скорее всего отсутствует соединение с интернетом. Операция отменена.", guild);
				return 0;
			}
			jsonMarketTM = httpClient.connect(url + market_hash_name + "/?key=" + Config.getCsgotmApiKey());
			//повторить операцию, если соединение с интернетом нестабильное:
			if (jsonMarketTM == null) {
				counterOfTimeouts++;
				wasSocketTimeoutException = true;
				Logger.error("Время ожидания вышло, повторяю операцию... (" + item.getValue() + ")", guild);
			} else {
				wasSocketTimeoutException = false;
			}
		}

		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(jsonMarketTM));
			boolean success = (boolean) jsonObject.get("success");
			if (success) {
				JSONArray list = (JSONArray) jsonObject.get("list");
				if (list.isEmpty()) {
					System.out.println("Предмет " + item.getValue() + " никто не продаёт. Пробую посмотреть цену в Steam...");
					return getPriceBySteamMarketJson(item.getValue());
				}
				JSONObject firstItem = (JSONObject) list.get(0);
				price = (long) firstItem.get("price");
			} else {
				String error = (String) jsonObject.get("error");
				Logger.error(Util.decode(error), guild);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("Минимальная стоимость " + item.getValue() + " — " + Util.toRubKop(String.valueOf(price)));
		return price;
	}

	/**
	 * Способ парсинга json из Steam Market (больше 20 цен предметов за короткий промежуток времени
	 * запрашивать невозможно, поэтому этим бескостыльным способом как основным пользоваться не удастся)
	 *
	 * @param market_hash_name market_hash_name
	 * @return стоимость предмета
	 */
	private long getPriceBySteamMarketJson(String market_hash_name) {
		long price = 0;
		String url = "http://steamcommunity.com/market/priceoverview/?currency=5&country=us&appid=730&market_hash_name=";
		String itemNameForUrl = market_hash_name.replace(" ", "%20");

		StringBuilder jsonSteamMarket = httpClient.connect(url + itemNameForUrl);
		if (jsonSteamMarket == null) {
			Logger.error("Время ожидания вышло. Возвращаю \"0\" (" + market_hash_name + ")", guild);
			return 0;
		} else {
			JSONParser jsonParser = new JSONParser();
			try {
				JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(jsonSteamMarket));
				boolean success = (boolean) jsonObject.get("success");
				if (success) {
					String lowest_price = (String) jsonObject.get("lowest_price");
					if (lowest_price.isEmpty()) {
						System.out.println("Предмет не продаётся в Steam Market. Скорее всего этот предмет нельзя продать.");
						return 0;
					}
					//выделяю цену как могу:
					String[] priceParts = lowest_price.split(" ");
					String priceKop = priceParts[0].replace(",", "");
					price = Long.parseLong(priceKop);
				} else {
					String error = (String) jsonObject.get("error");
					Logger.error(Util.decode(error), guild);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			System.out.println("Минимальная стоимость " + market_hash_name + " в Steam Market — " +
					Util.toRubKop(String.valueOf(price)));
		}
		return price;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР