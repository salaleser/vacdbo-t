package ru.salaleser.vacdbot.vacdbo;

public class ScannerOwnedGames extends Scanner {

	ScannerOwnedGames(long starts, long range, int instance) {
		super(starts, range, instance);
		String sInterface = "IPlayerService";
		String sMethod = "GetOwnedGames";
		String sVersion = "v0001";
		baseQuery = Settings.BASE_URL + "/" + sInterface + "/" + sMethod + "/" + sVersion + "/?key=" +
				Settings.getKey() + "&steamid=";
		steamidCount = 1;
	}

	protected void scan() {
		parser = new ParserOwnedGames();
		client = new Client();

		for (long i = 0; i < range; i++) {
			String id = String.valueOf(starts + i);
			parser.setTime();

			System.out.print("Поток " + space() + thread + " => Сканирую SteamID: " + id + " | ");

			if (parser.isExists("player_summaries", id)) {
				response = client.connect(baseQuery + id);
				if (response == null) {
					i--;
					continue;
				}
				if (!parser.parse(response, id)) {
					i--;
					continue;
				}
			} else {
				Log.add("SteamID не существует");
			}
			Log.out(" \\ " + parser.getElapsed() + " ms");
			Settings.addTotalScanned(steamidCount);
		}
	}
}
