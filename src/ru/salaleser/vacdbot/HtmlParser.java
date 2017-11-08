package ru.salaleser.vacdbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HtmlParser {

	private static final String BASE_URL = "http://steamcommunity.com/profiles/";

	private static int length = 45;
	private static String title;
	private static String last_online;
	private static String status;
	private static String profile_level;
	private static String profile_summary;
	private static String numBadges = "0";
	private static String numGames = "0";
	private static int numInventory = 0;
	private static String numScreenshots = "0";
	private static String numVideos = "0";
	private static String numWorkshopItems = "0";
	private static String numReviews = "0";
	private static String numArtwork = "0";
	private static String numGroups = "0";
	private static String numFriends = "0";
	private static String numAchievementsCsgo = "0";
	private static float commentsRep;
	private static int friendsLevels;
	private static String recent_activity;
	private static ArrayList<String> positiveList;
	private static ArrayList<String> negativeList;
	private static ArrayList<String> complaintsList;
	private static Document document;

	public static void parse(String args[]) {
		String[] steamids = new String[]{"76561198095972970", "76561198103577490", "76561198041743174", "76561198187239091", "alsdflas;df", "76561198245710318", "76561198038873933", "76561198271090447"};
		positiveList = fillList("positive_list.txt");
		negativeList = fillList("negative_list.txt");
		complaintsList = fillList("complaints_list.txt");
		for (String steamid : steamids) {
			System.out.print('┏');
			line('━', length);
			document = getDocument(BASE_URL + steamid);
			Elements profile = document.getElementsByClass("no_header");
			if (profile.isEmpty()) {
				System.out.println("┃\tПрофиль не существует");
				System.out.print('┗');
				line('━', length);
				continue;
			}
			title = document.title();
			profile_level = document.getElementsByClass("friendPlayerLevel").first().text();
			status = document.getElementsByClass("profile_in_game_header").text();
			last_online = document.getElementsByClass("profile_in_game_name").text();
			profile_summary = document.getElementsByClass("profile_summary").text();
			recent_activity = document.getElementsByClass("recentgame_recentplaytime").text();
			Elements keys = document.getElementsByClass("count_link_label");
			Elements values = document.getElementsByClass("profile_count_link_total");
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i).text().equals("Badges")) numBadges = values.get(i).text();
				else if (keys.get(i).text().equals("Games")) numGames = values.get(i).text();
				else if (keys.get(i).text().equals("Screenshots")) numScreenshots = values.get(i).text();
				else if (keys.get(i).text().equals("Videos")) numVideos = values.get(i).text();
				else if (keys.get(i).text().equals("Workshop Items")) numWorkshopItems = values.get(i).text();
				else if (keys.get(i).text().equals("Reviews")) numReviews = values.get(i).text();
				else if (keys.get(i).text().equals("Artwork")) numArtwork = values.get(i).text();
				else if (keys.get(i).text().equals("Groups")) numGroups = values.get(i).text();
				else if (keys.get(i).text().equals("Friends")) numFriends = values.get(i).text();
			}
			numInventory = getInventory(steamid);

			print();
			parseComments(steamid);

			float reputation = calcLevel() + calcSummary() + calcBadges() + calcGames() + calcScreenshots() + calcVideos() + calcWorkshopItems() + calcReviews() + calcArtwork() + calcGroups() + calcFriends(document) + calcInventory() + commentsRep;
			System.out.println("┃ВСЕГО:\t\t\t\t" + reputation);

			System.out.print('┗');
			line('━', length);
		}
		//		System.out.println("\n\n\n" + document);
	}

	private static void print() {
		System.out.print("┃\t\t" + title + "\n┣");
		line('━', length);
		System.out.println("┃Level:\t\t\t\t" + profile_level + "\n┃Status:\t\t\t" + status + " - " + last_online + "\n┃Recent activity:\t" + recent_activity + "\n┃Summary:\t\t\t" + profile_summary + "\n┃Badges:\t\t\t" + numBadges + "\n┃Games:\t\t\t\t" + numGames + "\n┃Inventory:\t\t\t" + numInventory + "\n┃Screenshots:\t\t" + numScreenshots + "\n┃Videos:\t\t\t" + numVideos + "\n┃Workshop Items:\t" + numWorkshopItems + "\n┃Reviews:\t\t\t" + numReviews + "\n┃Artwork:\t\t\t" + numArtwork + "\n┃Groups:\t\t\t" + numGroups + "\n┃Friends:\t\t\t" + numFriends);
	}

	private static void parseComments(String steamid) {
		String url = steamid + "/allcomments?ctp=";
		int page = 0;
		int count = 0;
		int rep = 0;
		int complaints = 0;
		while (true) {
			page++;
			document = getDocument(BASE_URL + url + page);
			Elements comments = document.getElementsByClass("commentthread_comment_content");
			if (comments.isEmpty()) break;
			for (Element element : comments) {
				String comment = element.getElementsByClass("commentthread_comment_text").text();
				//System.out.println(counter + ". " + s);
				if (checkComment(positiveList, comment)) {
					rep++;
				} else if (checkComment(negativeList, comment)) {
					rep--;
					if (checkComment(HtmlParser.complaintsList, comment)) {
						complaints++;
					}
				}
				count++;
			}
		}
		float cheatRep = (float) complaints / count * 100;
		commentsRep = rep / count * cheatRep + count;
		System.out.println("┃Всего комментариев:\t" + count);
		System.out.println("┃Репутация абсолютная:\t" + rep);
		System.out.println("┃Обвинений в нечестной игре:\t" + complaints + " (" + (int) cheatRep + "%)");
		System.out.println("┃Репутация комментариев:\t" + commentsRep);
	}

	/**
	 * Модуль определения содержимого комментария.
	 * Версия первая, печальная.
	 *
	 * @param comment комментарий
	 * @return true если слово есть в листе
	 */
	private static boolean checkComment(ArrayList<String> list, String comment) {
		comment = comment.toLowerCase();
		for (String word : list) {
			if (comment.contains(word)) {
				return true;
			}
		}
		return false;
	}

	private static ArrayList<String> fillList(String listFile) {
		ArrayList<String> list = new ArrayList<>();
		try {
			File file = new File(listFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = reader.readLine();
			while (line != null) {
				list.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static void line(char c, int d) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < d; i++) line.append(c);
		System.out.println(line);
	}

	private static float calcLevel() {
		return Float.parseFloat(profile_level) * 10;
	}

	private static float calcSummary() {
		if (profile_summary.equals("┃\tNo information given.")) return -10;
		return 10;
	}

	private static float calcBadges() {
		return Float.parseFloat(numBadges) * 3;
	}

	private static float calcGames() {
		return Float.parseFloat(numGames) * 2;
	}

	private static float calcScreenshots() {
		return Float.parseFloat(numScreenshots) * 1;
	}

	private static float calcVideos() {
		return Float.parseFloat(numVideos) * 3;
	}

	private static float calcWorkshopItems() {
		return Float.parseFloat(numWorkshopItems) * 10;
	}

	private static float calcReviews() {
		return Float.parseFloat(numReviews) * 7;
	}

	private static float calcArtwork() {
		return Float.parseFloat(numArtwork) * 4;
	}

	private static float calcGroups() {return Float.parseFloat(numGroups) * 2;}

	private static float calcFriends(Document doc) {
		Elements eFriends = doc.getElementsByClass("friendPlayerLevel");
		for (Element w : eFriends) friendsLevels += Integer.parseInt(w.text());
		return Float.parseFloat(numFriends) * 1 + friendsLevels / 10;
	}

	private static float calcInventory() {
		return numInventory * 2;
	}

	private static int getInventory(String steamid) {
		String url = steamid + "/inventory";
		document = getDocument(BASE_URL + url);

		int numItems = 0;
		Elements items = document.getElementsByClass("games_list_tab_number");
		for (Element element : items) {
			//удаляет скобки:
			String numString = element.text().substring(1, element.text().length() - 1);
			//удаляет разделитель разрядов:
			numString = numString.replaceAll(",", "");
			numItems += Integer.parseInt(numString);
		}
		return numItems;
	}

	private static Document getDocument(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			System.out.println("┃\tНет соединения с интернетом");
		}
		if (document == null) {
			System.out.println("┃\tПустой ответ от сервера");
			System.out.print('┗');
			line('━', length);
		}
		return document;
	}
}

/*
todo парсить это в бд
http://steamcommunity.com/inventory/76561198095972970/730/2?l=russian&count=5000
http://steamcommunity.com/profiles/76561198095972970/inventory/json/753/1

*/
/*
todo парсить это в бд
http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970
http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=76561198095972970

http://api.steampowered.com/ISteamApps/GetAppList/v2
*/
