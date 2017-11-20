package ru.salaleser.vacdbot.bot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

public class AnnotationListener {

	private Snitch snitch = new Snitch();

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		Bot.gui.setConnected(event.getClient());
		event.getClient().changePlayingText(Bot.status);
		Bot.userBot = event.getClient().getUserByID(377411774254809088L);
		Bot.guildKTO = event.getClient().getGuildByID(223560049937743872L);
		Bot.channelKTOLog = event.getClient().getChannelByID(377431980658393088L);
		Bot.channelKTOTest = event.getClient().getChannelByID(347333162449502208L);
		Bot.channelKTOGeneral = event.getClient().getChannelByID(347088817729306624L);
		Bot.roleOfficers = event.getClient().getRoleByID(382154712524259337L);
		Bot.voiceChannelGeneral = event.getClient().getVoiceChannelByID(347089107949977601L);
		Bot.gui.addText("Успешно подключен.");
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent event) {
		if (event.getUser().hasRole(Bot.roleOfficers)) {
			Bot.channelKTOGeneral.sendMessage(event.getUser() + ", <:alo:346605809532141570>");
			event.getUser().moveToVoiceChannel(Bot.voiceChannelGeneral);
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
			snitch.snitch(event.getMessage());
		}
	}

//	@EventSubscriber

}
