package ru.salaleser.vacdbot.bot.command.support;

import com.voicerss.tts.*;
import ru.salaleser.vacdbot.*;
import ru.salaleser.vacdbot.bot.TTSColumns;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TTSCommand extends Command {

	public TTSCommand() {
		super("tts", SUPPORT, "Озвучивает текст.");
	}

	private static final String TABLE = "tts";
	private static final String PATH = "cache_tts/";
	private static final String EXTENSION = ".mp3";

	public void handle(IGuild guild, IMessage message, String[] args) {
		if (args.length == 0) {
			File folder = new File(PATH);
			long folderSize = 0;
			File[] files = folder.listFiles();
			for (File file : files) folderSize += file.length();
			message.getChannel().sendMessage(Util.i("База данных содержит " +
					Util.b(DBHelper.executeQuery("SELECT COUNT(*) FROM " + TABLE)[0][0] + " ссылок") + ". " +
					"Всего в кэше " + Util.b(files.length + " файлов") + ", а их суммарный размер составляет " +
					Util.b((folderSize / 1024 / 1024) + " Мбайт")));
			return;
		}

		//определяю язык, по умолчанию на основании региона гильдии:
		String language = Languages.English_UnitedStates;
		switch (guild.getRegion().getName()) {
			case "Brazil": language = Languages.Portuguese_Brazil; break;
			case "Western Europe":
			case "Central Europe": language = Languages.English_GreatBritain; break;
			case "Hong Kong": language = Languages.Chinese_HongKong; break;
			case "Japan": language = Languages.Japanese; break;
			case "Russia": language = Languages.Russian; break;
			case "Sydney": language = Languages.English_Australia; break;
			case "Singapore":
			case "US Central":
			case "US East":
			case "US South":
			case "US West": language = Languages.English_UnitedStates; break;
		}
		// TODO: 23.02.2018 научить бота определять составные фразы из разных языков, сейчас фраза должна состоять из слов одного языка
		//определяю язык по первой букве фразы:
		if (args[0].substring(0, 1).matches("^[\\u4E00-\\u9FA5]+$")) language = Languages.Chinese_China;
		else if (args[0].substring(0, 1).matches("^[А-ЯЁа-яё]+$")) language = Languages.Russian;
		else if (args[0].substring(0, 1).matches("^[A-Za-z]+$")) language = Languages.English_UnitedStates;

		//добавлю эмоции с флагами по фану (если сообщения нет, то и добавлять не к чему):
		if (message != null) {
			switch (language) {
				case "en-gb": message.addReaction("\uD83C\uDDEC\uD83C\uDDE7"); break;
				case "en-us": message.addReaction("\uD83C\uDDFA\uD83C\uDDF8"); break;
				case "pt-br": message.addReaction("\uD83C\uDDE7\uD83C\uDDF7"); break;
				case "zh-hk": message.addReaction("\uD83C\uDDED\uD83C\uDDF0"); break;
				case "ja-jp": message.addReaction("\uD83C\uDDEF\uD83C\uDDF5"); break;
				case "ru-ru": message.addReaction("\uD83C\uDDF7\uD83C\uDDFA"); break;
				case "en-au": message.addReaction("\uD83C\uDDE6\uD83C\uDDFA"); break;
				case "zh-cn": message.addReaction("\uD83C\uDDE8\uD83C\uDDF3"); break;
			}
		}

		String text = String.join(" ", args);
		String filename;
		String timeupdated = String.valueOf(System.currentTimeMillis() / 1000L);
		boolean cached = false;
		String sql = "SELECT * FROM " + TABLE + " WHERE text = '" + text + "' AND language = '" + language + "'";
		//если запись уже существует, то не стоит тревожить лишний раз синтезатор, тем более, что он платный:
		if (DBHelper.executeQuery(sql)[0][0] != null) {
			cached = true;
			//вытаскиваю всю строку:
			String[] row = DBHelper.executeQuery(sql)[0];
			filename = row[TTSColumns.Filename];
			//увеличиваю счетчик проигрываний:
			int counter = Integer.parseInt(row[TTSColumns.Counter]);
			row[TTSColumns.Counter] = String.valueOf(++counter);
			row[TTSColumns.Updated] = timeupdated;
			//пихаю обратно:
			DBHelper.update(TABLE, row);
		} else {
			//если же этот набор символов впервые запущен, то сгенерирую имя файла:
			String id = DBHelper.getNewId(TABLE, "filename");
			String zeroes;
			switch (String.valueOf(id).length()) {
				case 1: zeroes = "0000"; break;
				case 2: zeroes = "000"; break;
				case 3: zeroes = "00"; break;
				case 4: zeroes = "0"; break;
				default: zeroes = "";
			}
			filename = zeroes + id;

			VoiceProvider voiceProvider = new VoiceProvider(Config.getVoiceRssApiKey());
			VoiceParameters params = new VoiceParameters(text, language);
			params.setCodec(AudioCodec.WAV);
			params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
			params.setBase64(false);
			params.setSSML(false);

			int speed = 0;
			String speedString = DBHelper.getOption(guild.getStringID(), "tts", "speed");
			if (Util.isNumeric(speedString)) {
				speed = Integer.parseInt(speedString);
				if (speed < -10) speed = -10;
				if (speed > 10) speed = 10;
			}
			params.setRate(speed);

			byte[] voice = new byte[0];
			try {
				voice = voiceProvider.speech(params);
			} catch (Exception e) {
				e.printStackTrace();
			}

			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(PATH + filename + EXTENSION);
				DBHelper.insert(TABLE, new String[]{text, filename, "1", language, timeupdated});
			} catch (FileNotFoundException e) {
				Logger.error("Ошибка чтения файла!");
				e.printStackTrace();
			}
			try {
				fileOutputStream.write(voice, 0, voice.length);
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Player.queueFile(guild, PATH + filename + EXTENSION);

		//значок кэшированной записи для красоты:
		Util.delay(100);
		if (cached && message != null) message.addReaction("\uD83D\uDCBE");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР