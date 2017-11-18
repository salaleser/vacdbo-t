package ru.salaleser.vacdbot.vacdbo;

import javax.swing.*;

abstract class Scanner extends SwingWorker<Integer, String> {

	/**
	 * Примеры запросов:
	 * <p>
	 * http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561197960483970
	 * http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561197960268454
	 * http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=76561197991250563
	 */

	String baseQuery;
	Client client;
	Parser parser;
	StringBuilder response;
	long starts, range;
	int thread;
	int steamidCount;

	Scanner(long starts, long range, int thread) {
		this.starts = starts;
		this.range = range;
		this.thread = thread;
	}

	protected abstract void scan();

	String space() {
		if (thread < 10) return "0";
		else return "";
	}

	@Override
	protected Integer doInBackground() throws Exception {
		if (!isCancelled()) {
			scan();
		}
		return 0;
	}

	@Override
	protected void done() {
		if (isCancelled()) Log.add("\nПоток " + space() + thread + " => Сканирование отменено");
		else Log.add("\nПоток " + space() + thread + " => Сканирование завершено. Всего ");
	}
}
