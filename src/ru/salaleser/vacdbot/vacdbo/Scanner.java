package ru.salaleser.vacdbot.vacdbo;

import ru.salaleser.vacdbot.HttpClient;

import javax.swing.*;

public abstract class Scanner {

	/**
	 * Примеры запросов:
	 * <p>
	 * http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561197960483970
	 * http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561197960268454
	 * http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=76561197991250563
	 */

	String baseQuery;
	HttpClient httpClient;
	Parser parser;
	StringBuilder response;
	long starts, range;
	int thread;

	Scanner(long starts, long range, int thread) {
		this.starts = starts;
		this.range = range;
		this.thread = thread;
	}

	protected abstract void scan();

	/**
	 * добавляет ноль перед одноразрядным числом потоков
	 *
	 * @return ничего не добавляет если номер потока двузначный
	 */
	String space() {
		if (thread < 10) return "0";
		else return "";
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР