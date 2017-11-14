package ru.salaleser.vacdbot.command;

import ru.salaleser.vacdbot.Bot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {

	public PollCommand() {
		super("poll", "создаёт голосование\n" +
				"Использование: ```~poll [<вопрос>? [<вариант_ответа> / ... [<вариант_ответа>]]]```\n" +
				"Пример: ```~poll Есть ли жизнь на Марсе? Да / Нет / Не уверен```" +
				"Примечание: вариантов ответов может быть до 10; разделителем для вопроса является " +
				"вопросительный знак `?`, а для вариантов ответов - слэш `/`");
	}

	@Override
	public void handle(IMessage message, String[] args) throws Exception {
		message.getClient().changePlayingText("голосование");
		//defaults:
		int finalCountdown = 5;
		IMessage qMessage = message.getChannel().sendMessage("...");

		StringBuilder answersEnum = new StringBuilder("\n");
		String questionAndAnswers[] = splitQuestionAndAnswers(args);
		String question = "**" + questionAndAnswers[0] + "?**";
		String answers[] = questionAndAnswers[1].split("/");

		//добавляю кнопки для голосования в виде реакций:
		if (answers.length == 0 || answers.length == 1) {
			TimeUnit.MILLISECONDS.sleep(100);
			qMessage.addReaction("👍");
			TimeUnit.MILLISECONDS.sleep(100);
			qMessage.addReaction("👎");
		} else if (answers.length <= 10){
			for (int i = 0; i < answers.length; i++) {
				answersEnum.append(getNumberEmoji(i))
						.append(" - ")
						.append("`")
						.append(answers[i])
						.append("`")
						.append("\n");
			}
			for (int i = 0; i < answers.length; i++) {
				TimeUnit.MILLISECONDS.sleep(100);
				qMessage.addReaction(getNumberEmoji(i));
			}
		} else {
			message.reply("*неправильное количество вариантов ответов*");
		}

		//перерисовываю сообщение:
		StringBuilder progressBar;
		String pollWrapper = "\n" + question + "\n" + answersEnum;
		for (int i = finalCountdown; i >= 0; i--) {
			TimeUnit.MILLISECONDS.sleep(1000);
			progressBar = fillProgressBar(i);
			qMessage.edit("*Голосование завершится через " + i + " с*" +
					"```" + progressBar + "```" + pollWrapper);
		}
		qMessage.edit("*Голосование завершено!*" + pollWrapper);

		message.getClient().changePlayingText(Bot.status);

		//бот удалает свои реакции перед подсчетом голосов:
//		for (IReaction reaction : qMessage.getReactions()) qMessage.removeReaction(Bot.bot, reaction);
		ArrayList<IReaction> reactions = (ArrayList<IReaction>) qMessage.getReactions();
		int voters = 0;
		for (IUser user : message.getGuild().getUsers()) {
			if (!user.isBot() && !user.getPresence().getStatus().name().equals("OFFLINE")) {
				voters++;
			}
		}
		message.getChannel().sendMessage("В голосовании приняли участие " +
				(reactions.size() - answers.length) +
				" из " + voters + " участников.\n");
		StringBuilder pollResult = calculatePollResult(reactions);
		//удаляю все реакции, чтобы нельзя больше было баловаться кнопками:
		qMessage.removeAllReactions();
		message.getChannel().sendMessage(pollResult.toString());
	}

	private String getRandomQuestion() {
		String array[] = {"Ты за луну или за солнце? ", "Сколько будет (2 + 2 * 2)?1/2/3/4/5/6/7/8"};
		int random = new Random().nextInt(array.length);
		return array[random];
	}

	private String getNumberEmoji(int number) {
		String numberEmoji[] = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":ten:"};
		return numberEmoji[number];
	}

	private String[] splitQuestionAndAnswers(String[] args) {
		//если нет аргументов, то возвращаю рандомный вопрос:
		if (args.length == 0) {
			return getRandomQuestion().split("\\?");
		}
		//добавляю разделитель за ленивых:
		if (args.length == 1) {
			args[0] += "?";
		}
		//создаю новый массив аргументов:
		StringBuilder newArgs = new StringBuilder();
		//возвращаю обратно пробелы,
		for (String arg : args) {
			newArgs.append(arg).append(" ");
		}
		//выделяю вопрос отдельно от вариантов ответов.
		return newArgs.toString().split("\\?");
	}

	private StringBuilder calculatePollResult(ArrayList<IReaction> reactions) {
		StringBuilder result = new StringBuilder();
		result.append("Результаты голосования:");
		for (IReaction reaction : reactions) {
			result.append(reaction.getEmoji()).append(" = ");
			result.append(reaction.getCount()).append(": ");
			for (IUser user : reaction.getUsers()) result.append(user.getName()).append(", ");
			if (!reaction.getUsers().isEmpty()) result.replace(result.length() - 2, result.length(), ".\n");
			else result.append("никто не проголосовал.\n");
		}
		return result;
	}

	private StringBuilder fillProgressBar(int c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c; i++) sb.append('█');
		return sb;
	}
}
