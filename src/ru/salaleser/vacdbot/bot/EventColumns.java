package ru.salaleser.vacdbot.bot;

//здесь храню номера колонок таблицы "events", они меняются иногда у меня, ведь я еще не в полной мере понимаю чего хочу
public class EventColumns {
	public static int ID = 0;
	public static int GuildID = 1;
	public static int Hour = 2;
	public static int Minute = 3;
	public static int Date = 4;
	public static int Command = 5;
	public static int Args = 6;
	public static int Enabled = 7;
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР