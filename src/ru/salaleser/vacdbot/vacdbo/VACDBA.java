package ru.salaleser.vacdbot.vacdbo;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

class VACDBA {

	static UserInterface ui = null;
	static Scanner[] scanners;
	static Settings settings;
	private static ColoredPrinter cp;

	public static void main(String args[]) {
		settings = Settings.set();
		cp = new ColoredPrinter.Builder(1, false)
				.foreground(Ansi.FColor.RED).background(Ansi.BColor.NONE).build();

		if (args.length == 1) {
			switch (args[0]) {
				case "--graphics":
				case "-gui":
					ui = new UserInterface();
					break;
				case "--help":
				case "-?":
					help();
					break;
				case "--version":
				case "-ver":
					System.out.println("Версия " + Settings.getVersion());
					break;
				default:
					error();
			}
		} else if (args.length > 2) {
			switch (args[0]) {
				case "--GetOwnedGames":
				case "-og":
					if (!assign(args)) error();
					break;
				case "--GetPlayerSummaries":
				case "-ps":
					if (!assign(args)) error();
					break;
				default:
					error();
			}
		} else {
			error();
		}
	}

	private static boolean assign(String args[]) {
		if (args[1].length() != 17) return false;
		Settings.setStarts(Long.parseLong(args[1]));
		Long range = Long.parseLong(args[2]);
		Settings.setEnds(Settings.getStarts() + range);
		if (args.length > 3) Settings.setThreads(Integer.parseInt(args[3]));
		start("GetOwnedGames");
		return true;
	}

	static void start(String method) {
		new Log(ui);

		int threads = Settings.getThreads();
		scanners = new Scanner[threads];
		long starts = Settings.getStarts();
		long ends = Settings.getEnds();
		long range = ends - starts;
		long part = range / threads;

		switch (method) {
			case "GetPlayerSummaries":
				for (int i = 0; i < threads; i++) {
					scanners[i] = new ScannerPlayerSummaries(starts + i * part, part, i);
				}
				break;
			case "GetPlayerBans":
				for (int i = 0; i < threads; i++) {
					scanners[i] = new ScannerPlayerBans(starts + i * part, part, i);
				}
				break;
			case "GetOwnedGames":
				for (int i = 0; i < threads; i++) {
					scanners[i] = new ScannerOwnedGames(starts + i * part, part, i);
				}
				break;
			case "GetFriendList":
//					scanner = new ScannerFriendList(this);
				break;
		}

		for (Scanner scanner : scanners) {
			scanner.addPropertyChangeListener(event -> {
				if ("progress".equals(event.getPropertyName())) {
					ui.progressBar.setValue((Integer) event.getNewValue());
				}
			});

			Thread thread = new Thread(() -> {
				scanner.scan();
				Log.out("\nПоток " + threads + " => Сканирование завершено.\n" +
						"\t\tВсего просканировано " + Settings.getTotalScanned() + " учётных записей," +
						"из них добавлено: " + Settings.getTotalAdded() +
						", обновлено: " + Settings.getTotalUpdated());
			});
			thread.start();
		}

		cp.println("Начинаю сканирование методом \"" + method + "\"",
				Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
		cp.println("Начальный SteamID64: " + starts +
						"; диапазон: " + range + "; количество потоков: " + threads + ".",
				Ansi.Attribute.NONE, Ansi.FColor.GREEN, Ansi.BColor.NONE);
	}

	private static void showTitle() {
		cp.println("VACDBA (Valve Anti Cheat Data Base Analyst)",
				Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
		cp.println("Утилита для создания и обновления базы данных пользователей Steam.",
				Ansi.Attribute.UNDERLINE, Ansi.FColor.NONE, Ansi.BColor.NONE);
		cp.println("(!c) salaleser, 2017\n",
				Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.NONE);
		cp.clear();
	}

	private static void help() {
		showTitle();
		System.out.println("Usage: vacdba [-options]\n" +
				"\tvacdba [-ps|-pb|-og] <initial_steamid64> <range> [<number_of_threads>]\n" +
				"Например: vacdba -og 76561197960265729 1000000 20\n" +
				"-ps, --GetPlayerSummaries\tдобавить/обновить таблицу player_summaries\n" +
				"-pb, --GetPlayerBans\t\tдобавить/обновить таблицу player_bans\n" +
				"-og, --GetOwnedGames\t\tдобавить/обновить таблицу owned_games\n" +
				"-gui, --graphics\t\tиспользовать графический интерфейс\n" +
				"-?, --help\t\t\tвывод этой подсказки\n" +
				"-ver, --version\t\t\tверсия");
	}

	private static void error() {
		showTitle();
		cp.println("Неверная команда",
				Ansi.Attribute.NONE, Ansi.FColor.RED, Ansi.BColor.NONE);
		cp.clear();
		System.out.println("Используйте ключ -? [|--help] для вывода подсказки");
	}
}