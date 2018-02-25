package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;

public class TestCommand extends Command {

	private IPrivateChannel privateChannel;

	public TestCommand() {
		super("test", MISC, "Запускает тестирование.", new String[]{"тест"});
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		privateChannel = message.getAuthor().getOrCreatePMChannel();
		privateChannel.sendMessage("" +
				"Вы готовы потратить 10 минут времени на то, чтобы ответить на все вопросы теста?");

	}

	private void ask(int number) {
		String sql = "SELECT question FROM test WHERE number = '" + number + "'";
		String question = DBHelper.executeQuery(sql)[0][0];
		privateChannel.sendMessage(question);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР