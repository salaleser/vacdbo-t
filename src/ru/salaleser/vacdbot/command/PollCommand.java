package ru.salaleser.vacdbot.command;

import com.vdurmont.emoji.Emoji;
import ru.salaleser.vacdbot.Bot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {

	public PollCommand() {
		super("poll", "**Описание:** Создаёт голосование\n" +
				"**Использование:** `~poll [<вопрос>? [<вариант_ответа> / ... [<вариант_ответа>]]]`\n" +
				"**Предустановки:** `~poll` - голосование с рандомным вопросом;" +
						"`~poll map` - голосование за карту в ксго.\n" +
				"**Пример:** `~poll Есть ли жизнь на Марсе? Да / Нет / Не уверен`\n" +
				"**Примечание:** вариантов ответов может быть до 10; разделителем для вопроса является " +
				"вопросительный знак `?`, а для вариантов ответов - слэш `/`");
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
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
		} else if (answers.length <= 10) {
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
			TimeUnit.SECONDS.sleep(1);
			progressBar = fillProgressBar(i);
			qMessage.edit("*Голосование завершится через " + i + " с*" +
					"```" + progressBar + "```" + pollWrapper);
		}

		//бот удалает свои реакции перед подсчетом голосов:
		for (IReaction reaction : qMessage.getReactions()) {
			qMessage.removeReaction(Bot.bot, reaction);
			TimeUnit.MILLISECONDS.sleep(100);
		}

		ArrayList<IReaction> reactions = (ArrayList<IReaction>) qMessage.getReactions();
		int users = 0;
		int voters = 0;
		for (IUser user : message.getGuild().getUsers()) {
			if (!user.isBot() && !user.getPresence().getStatus().name().equals("OFFLINE")) {
				users++;
			}
		}
		StringBuilder pollResult = calculatePollResult(reactions, answers);

		//удаляю все реакции, чтобы нельзя больше было баловаться кнопками:
		try {
			qMessage.removeAllReactions();
		} catch (RateLimitException e) {
			e.printStackTrace();
			Bot.log.sendMessage("RateLimitException caught: " + e.getMessage());
		}

		message.getClient().changePlayingText(Bot.status);

		qMessage.edit("*Голосование завершено!*" +
				"``` ```" +
				"В голосовании приняли участие " + reactions.size() + " из " + users + " участников.\n" +
				pollResult.toString());
	}

	private String getRandomQuestion() {
		String array[] = {
				"Ты за луну или за солнце? ",
				"Хочешь пить? ",
				"Как вам эта песня? ",
				"Сколько стоит аренда аккаунта?Складной нож/зависит от внешности арендатора/Не измеряется деньгами",
				"Шла Саша по шоссе?Шла/По кривой дорожке/Повернула \"налево\"/Бежала/Наташа/Не сушку",
				"Сколько будет 2+2*2?1/2/3/4/5/6/7/8",
				"Сколько тебе лет?12/13/14/Старше 14"
		};
		int random = new Random().nextInt(array.length);
		return array[random];
	}

	private String getMapQuestion() {
		StringBuilder mapQuestion = new StringBuilder("В какую карту хочешь сыграть?");
		String[] maps = {
				"de_train",
				"de_nuke",
				"de_dust2",
				"de_cache",
				"de_mirage",
				"de_inferno",
				"de_cobblestone",
				"de_overpass",
				"cs_office",
				"cs_agency"
		};
		for (int i = 0; i < maps.length; i++) {
			mapQuestion.append(maps[i]);
			if (i + 1 != maps.length) mapQuestion.append("/");
		}
		return mapQuestion.toString();
	}

	private String getNumberEmoji(int number) {
		String numberEmoji[] = {":one:", ":two:", ":three:", ":four:",
				":five:", ":six:", ":seven:", ":eight:", ":nine:", ":ten:"};
		return numberEmoji[number];
	}

	private String[] splitQuestionAndAnswers(String[] args) {
		//если нет аргументов, то возвращаю рандомный вопрос:
		if (args.length == 0) {
			return getRandomQuestion().split("\\?");
		}
		//если первый аргумент "map", то отдаю предустановку:
		if (args[0].equals("map")) {
			return getMapQuestion().split("\\?");
		}
		//создаю новый массив аргументов:
		StringBuilder newArgs = new StringBuilder();
		//возвращаю обратно пробелы,
		for (String arg : args) {
			newArgs.append(arg).append(" ");
		}
		//добавляю разделитель за ленивых:
		if (!newArgs.toString().contains("?")) {
			newArgs.append("?");
		}
		System.out.println(newArgs.toString());
		//выделяю вопрос отдельно от вариантов ответов.
		return newArgs.toString().split("\\?");
	}

	private StringBuilder calculatePollResult(ArrayList<IReaction> reactions, String[] answers) {
		StringBuilder result = new StringBuilder();
		result.append("Результаты голосования:\n");
		if (reactions.isEmpty()) {
			return result.replace(0, result.length(), "Никто не голосовал.");
		}
		for (int i = 0; i < reactions.size(); i++) {
			result.append(reactions.get(i).getEmoji());
			if (answers.length > 1) result.append("`").append(answers[i]).append("`");
			result.append(" = ").append("**").append(reactions.get(i).getCount()).append("**: ");
			for (IUser user : reactions.get(i).getUsers()) result.append(user.getDisplayName(Bot.guild)).append(", ");
			if (!reactions.get(i).getUsers().isEmpty()) result.replace(result.length() - 2, result.length(), ".\n");
			else result.append("нет голосов.\n");
		}
		return result;
	}

	private StringBuilder fillProgressBar(int c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c; i++) sb.append('█');
		return sb;
	}
}
