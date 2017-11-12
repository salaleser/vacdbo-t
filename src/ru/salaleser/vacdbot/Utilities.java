package ru.salaleser.vacdbot;

public class Utilities {
	public static boolean isNumeric(String string) {
		return string.matches("\\d+") && string.length() < 5;
	}
	public static boolean isSteamID64(String steamID64) {
		return steamID64.length() == 17 &&
				steamID64.matches("\\d+") &&
				Long.parseLong(steamID64) > 76561197960265729L &&
				Long.parseLong(steamID64) < 76561202255233023L;
	}
}
