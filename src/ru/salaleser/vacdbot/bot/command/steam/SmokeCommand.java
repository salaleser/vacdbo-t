package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class SmokeCommand extends Command {

	public SmokeCommand() {
		super("smoke", STEAM, "Показывает смоки на карте.");
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~smoke [<название_карты>]`.",
				"`~smoke nuke` - смоки на ньюке.",
				"`~smoke nuke`.",
				"в разработке."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		String nukeSmokeOutsideCorner = "https://steamuserimages-a.akamaihd.net/ugc/861738620619026170/0A11B22B786B66FE6301BD871E5A6C690FABDBA0/";
		message.getChannel().sendMessage("**Смок на Периметр:**\n" +
				nukeSmokeOutsideCorner + "\n" +
				"```setpos 123 123 0; setang 123 123 0```");
	}
}// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР