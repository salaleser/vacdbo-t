import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

class MessageManager {

	void handleMessage(IMessage message) {

		String messageText = message.getContent().substring(1).toLowerCase();
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();

		ArrayList<StringBuilder> friends;
		ArrayList<String> cheaters;

		try {
			if (messageText.equals("vac")) {
				channel.sendMessage("Сканирую друзей на VAC-баны...");
				String steamid = "76561198095972970";
				HttpClient httpClient = new HttpClient();
				ParserPlayerFriends parserFriends = new ParserPlayerFriends();
				ParserPlayerBans parserBans = new ParserPlayerBans();

				StringBuilder jsonFriends = httpClient.connect("http://api.steampowered.com/" +
						"ISteamUser/GetFriendList/v0001/" +
						"?key=393819FBF50B3E63C1C6B60515A1AD0B&steamid=" +
						steamid + "&relationship=friend");
				friends = parserFriends.parse(jsonFriends);
				System.out.println(friends);

				for (StringBuilder steamids : friends) {
					StringBuilder jsonBans = httpClient.connect("http://api.steampowered.com/" +
							"ISteamUser/GetPlayerBans/v1/" +
							"?key=393819FBF50B3E63C1C6B60515A1AD0B&steamids=" + friends);
					cheaters = parserBans.parse(jsonBans);
					for (String id : cheaters) {
						channel.sendMessage(id);
					}
				}
			}

            /* УПОМИНАНИЯ: */
			if (message.getMentions().size() != 0) { // Содержит ли текущее сообщение упоминание
				for (int i = 0; i < message.getMentions().size(); i++) { // Перебираю упомянутых пользователей
					// УПОМИНАНИЕ БОТА
					if (message.getMentions().get(i).getName().equals("VACDBO-T")) {
						channel.sendMessage(author + ", я выявляю недавно получивших VAC-бан друзей" +
								"(команда \"~vac\"");
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}