package ru.salaleser.vacdbot.bot.command;

import com.voicerss.tts.*;
import ru.salaleser.vacdbot.*;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TTSCommand extends Command {

	public TTSCommand() {
		super("tts");
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
		String text = String.join(" ", args);
		String filename;
		//если запись уже существует, то не стоит тревожить лишний раз синтезатор, тем более, что он платный:
		if (DBHelper.isExists(TABLE, "text", text)) {
			filename = DBHelper.executeQuery("SELECT filename FROM " + TABLE + " WHERE text = '" + text + "'")[0][0];
			//вытаскиваю количество проигрываний этого звука:
			String sqlCounter = "SELECT counter FROM " + TABLE + " WHERE text = '" + text + "'";
			int counter = Integer.parseInt(DBHelper.executeQuery(sqlCounter)[0][0]);
			//вытаскиваю всю строку для модификации:
			String query = "SELECT * FROM " + TABLE + " WHERE text = '" + text + "'";
			String[] row = DBHelper.executeQuery(query)[0];
			//модифицирую:
			row[2] = String.valueOf(++counter);
			//пихаю обратно:
			DBHelper.update(TABLE, row);
		} else {
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
			String language = Languages.Russian;
//			if (Util.isRussian(args[0])) language = Languages.Russian;
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
				DBHelper.insert(TABLE, new String[]{text, filename, "1"});
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
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР