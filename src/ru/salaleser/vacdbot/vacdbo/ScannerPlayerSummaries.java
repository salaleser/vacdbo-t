package ru.salaleser.vacdbot.vacdbo;

public class ScannerPlayerSummaries extends Scanner {

	ScannerPlayerSummaries(long starts, long range, int instance) {
		super(starts, range, instance);
		String sInterface = "ISteamUser";
		String sMethod = "GetPlayerSummaries";
		String sVersion = "v0002";
		baseQuery = Settings.BASE_URL + "/" + sInterface + "/" + sMethod + "/" + sVersion + "/?key=" +
				Settings.getKey() + "&steamids=";
		steamidCount = 100;
	}

	protected void scan() {
		parser = new ParserPlayerSummaries();
		client = new Client();

		StringBuilder steamids;

		for (long i = 0; i < range; i += steamidCount) {
			steamids = new StringBuilder();
			parser.setTime();

			for (long id = i; id < i + steamidCount; id++) steamids.append(",").append(starts + id);
			String s = steamids.substring(1);
			String sStarts = s.substring(0, 17);
			String sEnds = s.substring(s.length() - 17, s.length());

			System.out.print("Поток " + space() + thread + " => Сканирую SteamIDs: " + sStarts + "-" + sEnds + " | ");

			response = client.connect(baseQuery + steamids);
			if (response == null) {
				i -= steamidCount;
				continue;
			}
			if (!parser.parse(response, null)) {
				i -= steamidCount;
				continue;
			}
			Log.out(" \\ " + parser.getElapsed() + " ms");
			Settings.addTotalScanned(steamidCount);
		}
	}
}
