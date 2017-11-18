package ru.salaleser.vacdbot.vacdbo;

class Log {

	private static UserInterface ui;

	Log(UserInterface ui) {
		Log.ui = ui;
	}

	static void add(String text) {
		if (ui != null) ui.addTextAreaConsole(text);
		System.out.print(text);
	}

	static void out(String text) {
		if (ui != null) ui.addTextAreaConsole(text + "\n");
		System.out.println(text);
	}
}
