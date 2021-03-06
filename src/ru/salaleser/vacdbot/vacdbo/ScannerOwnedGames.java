package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.HttpClient;

import java.net.SocketTimeoutException;

public class ScannerOwnedGames extends Scanner {

	ScannerOwnedGames(long starts, long range, int instance) {
		super(starts, range, instance);
		String sInterface = "IPlayerService";
		String sMethod = "GetOwnedGames";
		String sVersion = "v0001";
		baseQuery = Config.BASE_URL + "/" + sInterface + "/" + sMethod + "/" + sVersion + "/?key=" + Config.getSteamWebApiKey() + "&steamid=";
	}

	protected void scan() {
		parser = new ParserOwnedGames();
		httpClient = new HttpClient();

		for (long i = 0; i < range; i++) {
			String id = String.valueOf(starts + i);
			parser.setTime();

			System.out.print("Поток " + thread + " => Сканирую SteamID: " + id + " | ");

			if (parser.isExists("player_summaries", id)) {
				response = httpClient.connect(baseQuery + id);
				if (response == null) {
					i--;
					continue;
				}
				if (!parser.parse(response, id)) {
					i--;
					continue;
				}
			} else {
				System.out.println("SteamID не существует");
			}
			System.out.println(" \\ " + parser.getElapsed() + " ms");
			Config.addTotalScanned(1);
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР