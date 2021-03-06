package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.HttpClient;

import java.net.SocketTimeoutException;

public class ScannerPlayerSummaries extends Scanner {

	ScannerPlayerSummaries(long starts, long range, int instance) {
		super(starts, range, instance);
		String sInterface = "ISteamUser";
		String sMethod = "GetPlayerSummaries";
		String sVersion = "v0002";
		baseQuery = Config.BASE_URL + "/" + sInterface + "/" + sMethod + "/" + sVersion + "/?key=" + Config.getSteamWebApiKey() + "&steamids=";
	}

	protected void scan() {
		parser = new ParserPlayerSummaries();
		httpClient = new HttpClient();

		StringBuilder steamids;

		for (long i = 0; i < range; i += 100) {
			steamids = new StringBuilder();
			parser.setTime();

			for (long id = i; id < i + 100; id++) steamids.append(",").append(starts + id);
			String s = steamids.substring(1);
			String sStarts = s.substring(0, 17);
			String sEnds = s.substring(s.length() - 17, s.length());

			System.out.print("Поток " + space() + thread + " => Сканирую SteamIDs: " + sStarts + "-" + sEnds + " | ");

			response = httpClient.connect(baseQuery + steamids);
			if (response == null) {
				i -= 100;
				continue;
			}
			if (!parser.parse(response, null)) {
				i -= 100;
				continue;
			}
			System.out.println(" \\ " + parser.getElapsed() + " ms");
			Config.addTotalScanned(100);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР