package ru.salaleser.vacdbot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

public class AnnotationListener {


	@EventSubscriber
	public void onReady(ReadyEvent event) {
		event.getClient().changePlayingText(Bot.status);
		event.getClient().changePlayingText("твои нервы!");
		Bot.bot = event.getClient().getUserByID(377411774254809088L);
		Bot.guild = event.getClient().getGuildByID(223560049937743872L);
		Bot.log = event.getClient().getChannelByID(377431980658393088L);
		Bot.log.sendMessage("**___Я тут___**");
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {

	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		if (event.getMessage().getContent().equals("~")) return;
		if (event.getMessage().getContent().startsWith("~")) {
			Bot.getCommandManager().handle(event.getMessage());
		} else {
			if (event.getMessage().getMentions().size() != 0) { // Содержит ли текущее сообщение упоминание
				for (int i = 0; i < event.getMessage().getMentions().size(); i++) { // Перебираю упомянутых пользователей
					// УПОМИНАНИЕ БОТА
					if (event.getMessage().getMentions().get(i).getName().equals("VACDBO-T")) {
						event.getMessage().getChannel().sendMessage(event.getMessage().getAuthor() +
								", я выявляю недавно получивших VAC-бан друзей" +
								"(команда \"~vac\"");
					}
				}
			}
		}
	}
}
