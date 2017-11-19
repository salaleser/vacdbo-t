package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.gui.Gui;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

import java.text.SimpleDateFormat;

public class AnnotationListener {


	@EventSubscriber
	public void onReady(ReadyEvent event) {
		Bot.gui.setConnected(event.getClient());
		event.getClient().changePlayingText(Bot.status);
		event.getClient().changePlayingText("твои нервы!");
		Bot.bot = event.getClient().getUserByID(377411774254809088L);
		Bot.guild = event.getClient().getGuildByID(223560049937743872L);
		Bot.log = event.getClient().getChannelByID(377431980658393088L);
		Bot.test = event.getClient().getChannelByID(347333162449502208L);
		Bot.general = event.getClient().getChannelByID(347088817729306624L);
		Bot.KTO = event.getClient().getRoleByID(381124867338272769L);
		Bot.voice = event.getClient().getVoiceChannelByID(347089107949977601L);

		long time = System.currentTimeMillis();
		String date = new SimpleDateFormat("dd.MM.yyyy").format(time);
		Bot.log.sendMessage(date + "\n" +
				event.getClient().getApplicationName() + " | " +
				event.getClient().getApplicationDescription() + " |\n" +
				event.getClient().getApplicationIconURL() + " |\n" +
				event.getClient().getApplicationOwner().getDisplayName(Bot.guild));
		Bot.gui.addText("Успешно подключен.");
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {
		if (event.getUser().hasRole(Bot.KTO)) {
			Bot.general.sendMessage(event.getUser() + ", <:alo:346605809532141570>");
			event.getUser().moveToVoiceChannel(Bot.voice);
		}
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) throws InterruptedException {
		if (event.getMessage().getContent().equals("~")) return;
		if (event.getMessage().getContent().startsWith("~")) {
			Bot.getCommandManager().handle(event.getMessage());
		} else if (event.getMessage().getContent().startsWith("=")) {
			String messageContent = event.getMessage().getContent().substring(1);
			String[] args = messageContent.split(" ");
			Bot.getCommandManager().getCommand("calc").handle(event.getMessage(), args);
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
