package ru.salaleser.vacdbot.bot.command.steam;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.salaleser.vacdbot.Config.STEAMID64;
import static ru.salaleser.vacdbot.Util.*;
import static ru.salaleser.vacdbot.Util.b;
import static ru.salaleser.vacdbot.Util.u;
import static ru.salaleser.vacdbot.Util.ub;

public class RepCommand extends Command {
	private final String BASE_URL = "http://steamcommunity.com/profiles/";

	private String profile_level;
	private String profile_summary;
	private String numBadges = "0";
	private String numGames = "0";
	private int numInventory = 0;
	private String numScreenshots = "0";
	private String numVideos = "0";
	private String numWorkshopItems = "0";
	private String numReviews = "0";
	private String numArtwork = "0";
	private String numGroups = "0";
	private String numFriends = "0";
	private int friendsLevels;
	private Document document;

	public RepCommand() {
		super("rep", STEAM, "Считает репутацию по странной формуле.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~rep [<SteamID64>|<Discord_ID>|<Custom_URL> [v]]`.",
				"`~rep` — считает репутацию вызвавшего пользователя.",
				"`~rep 76561198095972970`, `~rep @salaleser`.",
				"`v` в конце для отображения всех комментариев."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		ArrayList<String> positiveList = fillList("txt/positive_list.txt");
		ArrayList<String> negativeList = fillList("txt/negative_list.txt");
		ArrayList<String> complaintsList = fillList("txt/complaints_list.txt");

		this.guild = guild;
		IChannel channel = message.getChannel();
		String discordid = message.getAuthor().getStringID();
		String steamid = getSteamID64ByDiscordID(guild.getStringID(), discordid);
		HashMap<String, String> argsMap = getArgs(guild, args);
		if (argsMap.containsKey(STEAMID64)) steamid = argsMap.get(STEAMID64);

		document = getDocument(BASE_URL + steamid);
		Elements profile = document.getElementsByClass("no_header");
		if (profile.isEmpty()) {
			message.reply(i("профиль не существует"));
			return;
		}

		// FIXME: 26.11.2017 парсить джейсоны вместо эйчтиэмль https://lab.xpaw.me/steam_api_documentation.html#IPlayerService_GetSteamLevel_v1
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
		channel.sendMessage(ub(title) + "\n" +
				"Level: " + b(profile_level) + "\n" +
				"Status: " + b(status) + " - " + last_online + "\n" +
				"Recent activity: " + b(recent_activity) + "\n" +
				"Summary: " + profile_summary + "\n" +
				"Badges: " + b(numBadges) + "\n" +
				"Games: " + b(numGames) + "\n" +
				"Inventory: " + b(numInventory+"") + "\n" +
				"Screenshots: " + b(numScreenshots) + "\n" +
				"Videos: " + b(numVideos) + "\n" +
				"Workshop Items: " + b(numWorkshopItems) + "\n" +
				"Reviews: " + b(numReviews) + "\n" +
				"Artwork: " + b(numArtwork) + "\n" +
				"Groups: " + b(numGroups) + "\n" +
				"Friends: " + b(numFriends));

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
				int realLength = comment.length();
				if (comment.length() > maxLenght) realLength = maxLenght;
				String reducedComment = comment.substring(0, realLength);
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
		channel.sendMessage("Всего комментариев: **" + commentCounter + "**\n" + "Репутация абсолютная: **" + rep + "**\n" + "Обвинений в нечестной игре: **" + complaints + "** (" + (int) cheatRep + "%)\n" + "Репутация комментариев: **" + commentsRep + "**\n" + "ВСЕГО: " + reputation);

		if (args.length > 1 && args[1].equals("v")) {
			channel.sendMessage(u("Первая сотня комментариев из профиля:"));
			for (StringBuilder comment : commentsAll) {
				channel.sendMessage(comment.toString());
				delay(1000);
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
	private boolean checkComment(ArrayList<String> list, String comment) {
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
	private ArrayList<String> fillList(String listFile) {
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

	private float calcLevel() {
		return Float.parseFloat(profile_level) * 10;
	}

	private float calcSummary() {
		if (profile_summary.equals("No information given.")) return -10;
		return 10;
	}

	private float calcBadges() {
		return Float.parseFloat(numBadges) * 3;
	}

	private float calcGames() {
		return Float.parseFloat(numGames) * 2;
	}

	private float calcScreenshots() {
		return Float.parseFloat(numScreenshots) * 1;
	}

	private float calcVideos() {
		return Float.parseFloat(numVideos) * 3;
	}

	private float calcWorkshopItems() {
		return Float.parseFloat(numWorkshopItems) * 10;
	}

	private float calcReviews() {
		return Float.parseFloat(numReviews) * 7;
	}

	private float calcArtwork() {
		return Float.parseFloat(numArtwork) * 4;
	}

	private float calcGroups() {
		return Float.parseFloat(numGroups) * 2;
	}

	private float calcFriends(Document doc) {
		Elements eFriends = doc.getElementsByClass("friendPlayerLevel");
		for (Element w : eFriends) friendsLevels += Integer.parseInt(w.text());
		return Float.parseFloat(numFriends) * 1 + friendsLevels / 10;
	}

	private float calcInventory() {
		return numInventory * 2;
	}

	private int getInventory(String steamid) {
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

	private Document getDocument(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			Logger.error("Нет соединения с интернетом", guild);
		}
		if (document == null) {
			Logger.error("Пустой ответ от сервера", guild);
		}
		return document;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР