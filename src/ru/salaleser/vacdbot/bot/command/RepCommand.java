package ru.salaleser.vacdbot.bot.command;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RepCommand extends Command {
	private static final String BASE_URL = "http://steamcommunity.com/profiles/";

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
	private static int friendsLevels;
	private static Document document;

	public RepCommand() {
		super("rep", "**Описание:** Считает репутацию по странной формуле.\n" +
				"**Использование:** `~rep [<SteamID64>].`\n" +
				"**Предустановки:** `~rep` — считает репутацию salaleser.\n" +
				"**Пример:** `~rep 76561198095972970`.\n" +
				"**Примечание:** SteamID64 должен быть возможным.");
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		String steamid = Utilities
				.getSteamidByDiscordUser(message.getAuthor().getStringID());
		if (args.length > 0 && Utilities.isSteamID64(args[0])) steamid = args[0];
		else message.reply("*ошибка в профиле*");

		ArrayList<String> positiveList = fillList("txt/positive_list.txt");
		ArrayList<String> negativeList = fillList("txt/negative_list.txt");
		ArrayList<String> complaintsList = fillList("txt/complaints_list.txt");

		document = getDocument(BASE_URL + steamid);
		Elements profile = document.getElementsByClass("no_header");
		if (profile.isEmpty()) {
			message.reply("*Профиль не существует*");
			return;
		}
		String title = document.title();
		profile_level = document.getElementsByClass("friendPlayerLevel").first().text();
		String status = document.getElementsByClass("profile_in_game_header").text();
		String last_online = document.getElementsByClass("profile_in_game_name").text();
		profile_summary = document.getElementsByClass("profile_summary").text();
		String recent_activity = document.getElementsByClass("recentgame_recentplaytime").text();
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

		//SUMMARY:
		message.getChannel().sendMessage("__" + title + "__\n" + "Level: **" + profile_level + "**\n" + "Status: **" + status + "** - " + last_online + "\n" + "Recent activity: " + recent_activity + "\n" + "Summary: " + profile_summary + "\n" + "Badges: **" + numBadges + "**\n" + "Games: **" + numGames + "**\n" + "Inventory: **" + numInventory + "**\n" + "Screenshots: **" + numScreenshots + "**\n" + "Videos: **" + numVideos + "**\n" + "Workshop Items: **" + numWorkshopItems + "**\n" + "Reviews: **" + numReviews + "**\n" + "Artwork: **" + numArtwork + "**\n" + "Groups: **" + numGroups + "**\n" + "Friends: **" + numFriends + "**");

		//COMMENTS:
		String url = steamid + "/allcomments?ctp=";
		int page = 0;
		int commentCounter = 0;
		int rep = 0;
		int complaints = 0;
		ArrayList<StringBuilder> commentsAll = new ArrayList<>();
		while (true) {
			page++;
			StringBuilder commentsPage = new StringBuilder();
			document = getDocument(BASE_URL + url + page);
			Elements comments = document.getElementsByClass("commentthread_comment_content");
			if (comments.isEmpty()) break;
			int maxLenght = 40;
			for (Element element : comments) {
				commentCounter++;
				String comment = element.getElementsByClass("commentthread_comment_text").text();
				int realLenght = comment.length();
				if (comment.length() > maxLenght) realLenght = maxLenght;
				String reducedComment = comment.substring(0, realLenght);
				commentsPage.append(commentCounter).append(". ").append(reducedComment);
				if (comment.length() > maxLenght) commentsPage.append("...\n");
				else commentsPage.append("\n");
				if (checkComment(positiveList, comment)) {
					rep++;
				} else if (checkComment(negativeList, comment)) {
					rep--;
					if (checkComment(complaintsList, comment)) {
						complaints++;
					}
				}
			}
			commentsAll.add(commentsPage);
		}
		float cheatRep = (float) complaints / commentCounter * 100;
		float commentsRep = rep / commentCounter * cheatRep + commentCounter;
		float reputation = calcLevel() + calcSummary() + calcBadges() + calcGames() + calcScreenshots() + calcVideos() + calcWorkshopItems() + calcReviews() + calcArtwork() + calcGroups() + calcFriends(document) + calcInventory() + commentsRep;
		message.getChannel().sendMessage("Всего комментариев: **" + commentCounter + "**\n" + "Репутация абсолютная: **" + rep + "**\n" + "Обвинений в нечестной игре: **" + complaints + "** (" + (int) cheatRep + "%)\n" + "Репутация комментариев: **" + commentsRep + "**\n" + "ВСЕГО: " + reputation);

		if (args.length > 1 && args[1].equals("v")) {
			message.getChannel().sendMessage("Все *(почти, не получается больше сотни вывести пока)* комментарии:");
			for (StringBuilder comment : commentsAll) {
				message.getChannel().sendMessage(comment.toString());
				TimeUnit.SECONDS.sleep(1);
			}
		}
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

	/**
	 * Заполняло листов
	 *
	 * @param listFile файл-источник
	 * @return массив строк
	 */
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

	private static float calcLevel() {
		return Float.parseFloat(profile_level) * 10;
	}

	private static float calcSummary() {
		if (profile_summary.equals("No information given.")) return -10;
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

	private static float calcGroups() {
		return Float.parseFloat(numGroups) * 2;
	}

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
			Bot.channelLog.sendMessage("*Нет соединения с интернетом*");
		}
		if (document == null) {
			Bot.channelLog.sendMessage("*Пустой ответ от сервера*");
		}
		return document;
	}

	/*
	  Пока это не работает по причине приватности коплееров
	 */
	/*ArrayList<StringBuilder> getCoplayersList(String steamID64) {
		String url = steamID64 + "/friends/coplay?p=";
		ArrayList<String> coplayers = new ArrayList<>();
		ArrayList<StringBuilder> ret = new ArrayList<>();

		Document doc = getDocument(BASE_URL + steamID64 + "/friends/coplay");
		Elements elements = doc.getElementsByClass("pageLinks");
		System.out.println(doc);
		for (int page = 1; page < 10; page++) {
			document = getDocument(BASE_URL + url + page);
			Elements coplayGroups = document.getElementsByClass("coplayGroup");
			for (Element coplayGroup : coplayGroups) {
				if (coplayGroup.getElementsByClass("gameListRowItem")
						.text().equals("Counter-Strike: Global Offensive")) {
					Elements friendBlock = coplayGroup.getElementsByClass("friendBlock");
					for (Element coplayer : friendBlock) {
						coplayers.add(coplayer.getElementsByTag("data-steamid").text());
					}
				}
			}
			System.out.println("page: " + page + " | " + coplayers);
		}
		return ret;
	}*/
}