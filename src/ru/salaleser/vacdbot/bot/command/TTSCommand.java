package ru.salaleser.vacdbot.bot.command;

import com.voicerss.tts.*;
import ru.salaleser.vacdbot.*;
import sx.blah.discord.handle.obj.IMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TTSCommand extends Command {

	public TTSCommand() {
		super("tts", 2);
	}

	public void handle(IMessage message, String[] args) {
		// TODO: 02.02.2018 добавить проверку на язык текста и подстановку соответствующего голоса
		String text = String.join(" ", args);
		VoiceProvider tts = new VoiceProvider(Config.getVoiceRssApiKey());

		VoiceParameters params = new VoiceParameters(text, Languages.Russian);
		params.setCodec(AudioCodec.WAV);
		params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
		params.setBase64(false);
		params.setSSML(false);

		int speed = 0;
		String speedString = DBHelper.getValueFromSettings("tts", "speed");
		if (Util.isNumeric(speedString)) {
			speed = Integer.parseInt(speedString);
			if (speed < -10) speed = -10;
			if (speed > 10) speed = 10;
		}
		params.setRate(speed);

		byte[] voice = new byte[0];
		try {
			voice = tts.speech(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream("tts.mp3");
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
		Player.queueFile("tts.mp3");
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР