package ru.salaleser.vacdbot;

import sx.blah.discord.handle.obj.IUser;

/**
 * К сожалению, такой класс вряд ли будет уместен в моей системе,
 * так как для каждой гильдии должен быть свой геймер, а если
 * геймер участвует более чем в одной гильдии, то изменив настройки
 * в одной гильдии они поменяются и в другой, а это почва для
 * баловства как минимум. Усложнять же этот метод листами и другими
 * костылями нет необходимости, проще по-старинке в БД все хранить
 */
public class Gamer {

	public Gamer(IUser user) {
		this.discordUser = user;
		this.sex = null;
		this.faceit = null;
		this.steamid = null;
		this.realname = null;
	}

	private String realname;

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	private String sex;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	private String faceit;

	public String getFaceit() {
		return faceit;
	}

	public void setFaceit(String faceit) {
		this.faceit = faceit;
	}

	private String steamid;

	public String getSteamid() {
		return steamid;
	}

	public void setSteamid(String steamid) {
		this.steamid = steamid;
	}

	private IUser discordUser;

	public IUser getDiscordUser() {
		return discordUser;
	}

	public void setDiscordUser(IUser discordUser) {
		this.discordUser = discordUser;
	}

	public String getDiscordid() {
		return discordUser.getStringID();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР