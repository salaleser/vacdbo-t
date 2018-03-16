package ru.salaleser.vacdbot.bot;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;
import java.util.HashMap;

import static ru.salaleser.vacdbot.Util.code;
import static ru.salaleser.vacdbot.Util.delay;
import static ru.salaleser.vacdbot.bot.Bot.PREFIX;

public class CommandManager {

	public HashMap<String, Command> commands; // fixme это не должно быть паблик!

	CommandManager() {
		this.commands = new HashMap<>();
	}

	void addCommand(Command command) {
		commands.put(command.name, command);
		for (String alias : command.aliases) commands.put(alias, command);
	}

	public Command getCommand(String commandsKey) {
		return commands.get(commandsKey);
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		IGuild guild = event.getGuild();
		IMessage message = event.getMessage();
		String content = message.getContent();

		String guildname = "PM";
		if (!event.getChannel().isPrivate()) guildname = event.getGuild().getName();
		Logger.onMessage(guildname + " / " + event.getChannel().getName() + " / " +
				event.getAuthor().getName() + ": " + event.getMessage().getContent());

		if (content.equals(PREFIX)) content = "~help"; //если ~, то просто помочь выбрать команду
		//проверю на особые алиасы:
		switch (content.substring(0, 1)) {
			case PREFIX: content = content.substring(1); break;
			case "=": content = "calc " + content.substring(1); break;
			case "\"": content = "tts " + content.substring(1); break;
			default: return;
		}

		//распихать аргументы по ячейкам массива:
		String[] args = content.split(" ");
 		Command command = getCommand(args[0].toLowerCase());
		if (command == null) {
			message.reply("команда " + code(args[0]) + " не поддерживается");
			return;
		}
		Logger.info("Получил команду " + command.name + ".", guild);

		//проверка на лицуху:
		if (!Util.isAccessible(guild.getStringID(), command.name)) {
			Logger.info("Команда " + command.name + " запрещена для гильдии " + guild.getName() + ".", guild);
			message.addReaction("\uD83D\uDEAB");
			return;
		}

		//Проверка на право использования команды:
		int rank = Util.getRank(guild, message.getAuthor());
		int permissions = Util.getLevel(guild.getStringID(), command.name);
		if (rank > permissions) {
			message.reply("Вы не обладаете достаточными правами для использования команды " + code(command.name) + "!");
			return;
		}

		//передаю управление дальше по команде:
		try {
			command.handle(guild, message, Arrays.copyOfRange(args, 1, args.length));
//			message.delete();
			record(guild.getStringID(), command.name);
		} catch (DiscordException e) {
			Logger.error(e.getMessage(), guild);
			e.printStackTrace();
		} catch (RateLimitException e) {
			Logger.error("Бот сгенерировал слишком много действий за единицу времени!", guild);
		}
	}

	private void record(String guildid, String commandname) {
		String table = "statistics";
		String column = "invocations";
		String timeupdated = String.valueOf(System.currentTimeMillis() / 1000L);
		String query = "SELECT " + column + " FROM " + table + " WHERE guildid = '" + guildid + "' AND command = '" + commandname + "'";
		String value = DBHelper.executeQuery(query)[0][0];
		if (value == null) {
			DBHelper.insert(table, new String[]{guildid, commandname, "1", timeupdated});
		} else {
			int invocations = Integer.parseInt(value);
			String updateQuery = "UPDATE " + table + " SET " + column + " = ?, timeupdated = ? " +
					"WHERE guildid = '" + guildid + "' AND command = '" + commandname + "'";
			DBHelper.commit(table, updateQuery, new String[]{String.valueOf(++invocations), timeupdated});
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР