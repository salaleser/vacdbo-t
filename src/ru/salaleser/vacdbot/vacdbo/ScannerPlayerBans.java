package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.HttpClient;

import java.net.SocketTimeoutException;

public class ScannerPlayerBans extends Scanner {

	ScannerPlayerBans(long starts, long range, int instance) {
		super(starts, range, instance);
		String sInterface = "ISteamUser";
		String sMethod = "GetPlayerBans";
		String sVersion = "v1";
		baseQuery = Config.BASE_URL + "/" + sInterface + "/" + sMethod + "/" + sVersion + "/?key=" + Config.getSteamWebApiKey() + "&steamids=";
		steamidCount = 100;
	}

	protected void scan() {
		parser = new ParserPlayerBans();
		httpClient = new HttpClient();

		StringBuilder steamids;

		for (long i = 0; i < range; i += steamidCount) {
			steamids = new StringBuilder();
			parser.setTime();

			for (long id = i; id < i + steamidCount; id++) steamids.append(",").append(starts + id);
			String s = steamids.substring(1);
			String sStarts = s.substring(0, 17);
			String sEnds = s.substring(s.length() - 17, s.length());

			System.out.print("Поток " + space() + thread + " => Сканирую SteamIDs: " + sStarts + "-" + sEnds + " | ");

			response = httpClient.connect(baseQuery + steamids);
			if (response == null) {
				i -= steamidCount;
				continue;
			}
			if (!parser.parse(response, null)) {
				i -= steamidCount;
				continue;
			}
			System.out.println(" \\ " + parser.getElapsed() + " ms");
			Config.addTotalScanned(steamidCount);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР