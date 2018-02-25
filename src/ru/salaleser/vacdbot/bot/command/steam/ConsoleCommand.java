package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class ConsoleCommand extends Command {

	public ConsoleCommand() {
		super("console", STEAM, "Показывает полезные консольные команды.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~console`.",
				"нет.",
				"`~console`.",
				"ничего особенного."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		message.getChannel().sendMessage("Получить бронежилет, каску, гранаты и AK-47 на **K**: " +
				"```bind k \"give weapon_ak47; give weapon_hegrenade; give weapon_flashbang;" +
				" give weapon_smokegrenade; give weapon_incgrenade; give item_assaultsuit\"```");
		message.getChannel().sendMessage("Для тренировки: " +
				"```rcon sv_cheats 1; rcon sv_infinite_ammo 2; rcon sv_showimpacts 1; " +
				"rcon ammo_grenade_limit_total 4; rcon mp_spectators_max 10; rcon mp_warmuptime 5400; " +
				"rcon mp_buy_anywhere 1; rcon mp_warmup_start; rcon sv_full_alltalk 1; rcon bot_kick```");
		message.getChannel().sendMessage("Показ местоположения бомбы: " +
				"```alias +bombfind \"+use; gameinstructor_enable 1; cl_clearhinthistory\"\n" +
				"alias -bombfind \"-use; gameinstructor_enable 0; cl_clearhinthistory\"\n" +
				"bind e +bombfind```");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР