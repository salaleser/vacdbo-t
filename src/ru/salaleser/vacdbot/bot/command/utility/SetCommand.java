package ru.salaleser.vacdbot.bot.command.utility;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class SetCommand extends Command {

	public SetCommand() {
		super("set", UTILITY, "Устанавливает параметры командам.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~set <команда> <параметр> <значение>`.",
				"нет.",
				"`~set poll countdown 15`.",
				"используйте с умом, ибо можно прострелить себе ногу случайно этой командой."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		// FIXME: 30.11.17 при установке несуществующего параметра возвращает true (есть мнение, что это не баг, а фича)
		if (args.length != 3 || !Util.isCommand(args[0]) ) {
			message.reply("неправильный синтаксис.");
			return;
		}

		String table = "settings";
		String command = args[0];
		String key = args[1];
		String value = args[2];


		//проверяю на существование такой команды:
		if (!Util.isCommand(command)) {
			message.reply("команда " + command + " не поддерживается.");
			return;
		}

		//проверяю на попытку взлома:)
		if (key.equals(Config.Accessible)) {// TODO: 01.03.2018 убрать хардкод
			if (!value.equals("0") || !value.equals("1")) {
				message.reply("последний агрумент должен быть 1 или 0.");
				return;
			}
			if (!message.getAuthor().getStringID().equals(Bot.SALALESER)) {//мне можно всё!
				message.reply("нельзя изменить этот ключ, обратитесь к Лёхе <@!" + Bot.SALALESER + ">.");
				return;
			}
		}

		//проверяю на попытку изменить уровень доступа выше своего:
		if (key.equals(Config.Level)) {
			if (!Util.isNumeric(value) || Integer.parseInt(value) < Config.MIN_LEVEL || Integer.parseInt(value) > Config.MAX_LEVEL) {
				message.reply("последний агрумент должен быть числом в диапазоне от " + Config.MIN_LEVEL + " до " + Config.MAX_LEVEL + ".");
				return;
			}
			int rank = Util.getRank(guild, message.getAuthor());
			int permissions = Util.getLevel(guild.getStringID(), command);
			if (rank > permissions) {
				message.reply("Вы не можете изменить уровень доступа команде, которую не можете использовать.");
				return;
			} else if (rank > Integer.parseInt(value)) {
				message.reply("Вы не можете назначить уровень доступа выше своего ранга.");
				return;
			}
		}

		//если такого ряда еще нет, то добавить, если есть -- изменить:
		String query = "SELECT command FROM " + table + " WHERE guildid = '" + guild.getStringID() + "' " +
				"AND command = '" + command + "' AND key = '" + key + "'";
		String[] row = new String[]{guild.getStringID(), command, key, value};
		if (DBHelper.executeQuery(query)[0][0] == null) {
			if (DBHelper.insert(table, row)) message.reply("параметр добавлен.");
			else message.reply("параметр не добавлен.");
		} else {
			if (DBHelper.update(table, row)) message.reply("параметр установлен.");
			else message.reply("параметр не установлен.");
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР