package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.DBHelper;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;

public class TestCommand extends Command {

	IPrivateChannel privateChannel;

	public TestCommand() {
		super("test", new String[]{"тест"});
	}

//	@Override
//	public void help(IMessage message) {
//		message.getChannel().sendMessage(buildHelp(
//				"Тестирует новичков на совместимость.",
//				"`~test`.",
//				"нет.",
//				"`~test`.",
//				"в разработке."
//				)
//		);
//	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
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