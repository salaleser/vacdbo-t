package ru.salaleser.vacdbot.vacdbo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {

	public static String convertTime(long unixTime) {
		Date date = new Date(unixTime * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
		return sdf.format(date);
	}
}
