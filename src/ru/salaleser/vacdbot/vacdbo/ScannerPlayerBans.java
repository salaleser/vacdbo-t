package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.HttpClient;

public class ScannerPlayerBans extends Scanner {

	public ScannerPlayerBans(long starts, long range, int instance) {
		super(starts, range, instance);
		baseQuery = Config.BASE_URL + "/ISteamUser/GetPlayerBans/v1/?key=" + Config.getSteamWebApiKey() + "&steamids=";
	}

	public void scan() {
		//прогоняю сотню айдишек за один запрос:
		for (long i = 0; i < range; i += 100) {
			ParserPlayerBans parser = new ParserPlayerBans();
			StringBuilder steamids = new StringBuilder();

			//строю строку из сотни стимайди, разделенных запятыми:
			for (long id = i; id < i + 100; id++) steamids.append(",").append(starts + id);
			//удаляю первую запятую:
			String s = steamids.substring(1);
			//вычленяю первый steamid для вывода в консоль:
			String sStarts = s.substring(0, 17);
			//вычленяю последный steamid для вывода в консоль:
			String sEnds = s.substring(s.length() - 17, s.length());

			System.out.println("Поток " + space() + thread + " => Сканирую SteamIDs: " + sStarts + "-" + sEnds + " | ");

			parser.setTime();

			//получаю json:
			HttpClient httpClient = new HttpClient();
			StringBuilder json = httpClient.connect(baseQuery + steamids);
			//если ответ от сервера пустой, то придётся повторить запрос:
			if (json == null) {
				i -= 100;
				continue;
			}
			//если Parser.parse вернул false, то повторяю попытку записать в БД:
			if (!parser.parse(json, null)) {
				i -= 100;
				continue;
			}

//			System.out.println(" \\ " + parser.getElapsed() + " ms");
			Config.addTotalScanned(100);
		}
		System.out.println("Сканирование завершено.");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР