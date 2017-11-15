package ru.salaleser.vacdbot;

/**
 * Карты
 */
public enum Map {

	DE_DUST2,
	DE_TRAIN,
	DE_NUKE,
	DE_MIRAGE,
	DE_INFERNO,
	DE_CACHE,
	DE_OVERPASS,
	DE_COBBLESTONE,
	DE_DUST,
	CS_OFFICE,
	CS_ASSAULT,
	UNKNOWN;

	public static Map get(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return UNKNOWN;
		}
	}
}
