package ru.salaleser.vacdbot.bot.command.steam;

import ru.salaleser.vacdbot.Config;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.command.Command;
import ru.salaleser.vacdbot.vacdbo.ScannerPlayerBans;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class ScanCommand extends Command {

	public ScanCommand() {
		super("scan", STEAM);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		int threads = Integer.parseInt(DBHelper.getOption(guild.getStringID(), name, "threads"));
		ScannerPlayerBans[] scanners;

		if (args.length == 0) {
			long starts = Config.FIRST_STEAMID64;
			long ends = Config.LAST_STEAMID64;
			long part = (ends - starts) / threads;
			//создаю массив сканеров, где для каждого сканера свой диапазон айдишек
			scanners = new ScannerPlayerBans[threads];
			for (int i = 0; i < threads; i++) {
				scanners[i] = new ScannerPlayerBans(starts + i * part, part, i);
			}
			for (int i = 0; i < scanners.length; i++) {
				Thread thread = new Thread(scanners[i]::scan, "Scanner-" + i);
				thread.start();
			}
			return;
		}

		if (args[0].equals("s")) {
			String[][] suspects = DBHelper.executeQuery("SELECT steamid FROM suspects");
			scanners = new ScannerPlayerBans[suspects.length];
			for (int i = 0; i < suspects.length; i++) {
				scanners[i] = new ScannerPlayerBans(Long.parseLong(suspects[i][0]), 1, i);
			}
			for (int i = 0; i < scanners.length; i++) {
				Thread thread = new Thread(scanners[i]::scan, "Scanner-" + i);
				thread.start();
			}
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР