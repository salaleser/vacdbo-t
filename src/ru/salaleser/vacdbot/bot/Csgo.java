package ru.salaleser.vacdbot.bot;

/**
 * Карты
 */
public enum Csgo {

	de_train,
	de_nuke,
	de_dust2,
	de_cache,
	de_mirage,
	de_inferno,
	de_cobblestone,
	de_overpass,
	cs_office,
	cs_agency,
	UNKNOWN;

	public static Csgo get(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return UNKNOWN;
		}
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР
