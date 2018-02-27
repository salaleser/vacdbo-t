package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Logger;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashSet;
import java.util.Map;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", UTILITY, "Показывает как пользоваться командами.", new String[]{"?"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~help [<команда>]`.",
				"`~help` — покажет все доступные команды.",
				"`~help vac`, `~help`.",
				"хелп хелпа, хелп как хелп, что, хелпа никогда не видели?"
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length != 0) {
			Command command = Bot.getCommandManager().getCommand(args[0]);
			if (command == null) {// FIXME: 17.11.2017 такой же блок в менеджере команд
				message.reply("команда " + Util.code(args[0]) + " не поддерживается");
				return;
			}
			command.help(message);
		} else {
			int priority = Util.getPriority(message.getAuthor().getStringID());
			/*
			Здесь будет мой топорный код. Леха из будущего, исправь его, пожалуйста, когда научишься программировать.
			*/
			//узнаю количество категорий команд:
			HashSet<String> categories = new HashSet<>();
			for (Map.Entry<String, Command> entry : Bot.getCommandManager().commands.entrySet()) {
				categories.add(entry.getValue().category);
			}
			//остальное вроде ничо, оптимизировать бы только
			//разбиваю по категориям команды и отправляю разными сообщениями, чтобы избежать лимит в 2000 символов:
			StringBuilder helpBuilder = new StringBuilder("Ваш уровень доступа " +
					Util.ub(String.valueOf(priority)) + ", доступные команды:" + "\n");
			int i = 0;
			String strike = "";
			for (String cat : categories) {
				i++;
				StringBuilder categoryBuilder = new StringBuilder(Util.ub(cat) + ":\n");
				for (Map.Entry<String, Command> entry : Bot.getCommandManager().commands.entrySet()) {
					if (!cat.equals(entry.getValue().category)) continue;
					boolean accessible = Util.isAccessible(guild.getStringID(), entry.getValue().name);
					String level = DBHelper.getOption(guild.getStringID(), entry.getKey(), "level");
					if (level == null) continue; //если в БД нет такой команды, значит это алиас, он в списке не нужен
					if (accessible) strike = "";
					else strike = "~~";
					categoryBuilder
							.append(strike)
							.append(Util.code(entry.getKey()))
							.append(" — ")
							.append(Util.i(entry.getValue().description))
							.append(" (")
							.append(Util.b(level))
							.append(")")
							.append(strike)
							.append("\n");
				}
				helpBuilder.append(categoryBuilder);
				//у сообщений лимит 2000 символов, три категории наверняка уложатся в это число,
				//просто посылать каждую категорию отдельным сообщением тоже не получится из-за ограничения отправки
				//подряд нескольких сообщений (RateLimitException), установка задержки между отправками тоже не поможет.
				if (i % 3 == 0) {
					 //не стоит слишком часто посылать сообщения (RateLimitException)
					Util.delay(100);
					message.getChannel().sendMessage(helpBuilder.toString());
					helpBuilder = new StringBuilder();
				}
			}
			helpBuilder
					.append("\n")
					.append(Util.u("Легенда:"))
					.append(" ")
					.append(Util.code("~команда"))
					.append(" — ")
					.append(Util.i("Описание"))
					.append(" (")
					.append(Util.b("необходимый уровень доступа для использования"))
					.append(")")
					.append("\nЗачеркнутые команды запрещены по разным причинам " +
							"(нестабильные, в разработке, личные и т.д.) " +
							"Чтобы разблокировать их свяжитесь со мной (Лёха <@!223559816239513601>)")
					.append("\n")
					.append("Префикс бота — ")
					.append(Util.code(Bot.PREFIX))
					.append(". Пример использования: ")
					.append(Util.code("~help"))
					.append(", ")
					.append(Util.code("~tts"))
					.append(".");
			message.getChannel().sendMessage(helpBuilder.toString());
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР