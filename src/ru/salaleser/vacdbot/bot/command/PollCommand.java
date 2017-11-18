package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.Config;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {

	public PollCommand() {
		super("poll", "**Описание:** Создаёт голосование.\n" +
				"**Использование:** `~poll [<вопрос>? [<вариант_ответа> / ... [<вариант_ответа>]]]`.\n" +
				"**Предустановки:** `~poll` - голосование с рандомным вопросом;" +
				"`~poll map` - голосование за карту в ксго.\n" +
				"**Пример:** `~poll Есть ли жизнь на Марсе? Да / Нет / Не уверен`.\n" +
				"**Примечание:** вариантов ответов может быть до 10; разделителем для вопроса является " +
				"вопросительный знак `?`, а для вариантов ответов - слэш `/`.");
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		message.getClient().changePlayingText("голосование");

		//defaults:
		int finalCountdown = Config.getPollCountdown();
		IMessage qMessage = message.getChannel().sendMessage("Инициализация голосования...");

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
						.append(" — ")
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
		StringBuilder progressBar = new StringBuilder(" ");
		String pollWrapper = "\n" + question + "\n" + answersEnum;
		for (int i = finalCountdown; i > 0; i--) {
			TimeUnit.SECONDS.sleep(1);
			progressBar = fillProgressBar(i);
			qMessage.edit("*Голосование завершится через " + i + " с*" +
					"```" + progressBar + "```" + pollWrapper);
		}
		TimeUnit.SECONDS.sleep(1);
		qMessage.edit("*Ставки сделаны! Ставок больше нет. Идёт подсчёт голосов...*" +
				"``` ```" + pollWrapper);

		//получаю реакции-голоса пользователей в отдельный лист:
		TimeUnit.SECONDS.sleep(2);
		List<IReaction> reactions = qMessage.getReactions();

		// FIXME: 17.11.2017 чёрная магия:
		//проверка на чёрную магию от дискорда (по невыясненным причинам иногда рекций нет совсем):
		if (reactions.isEmpty()) {
			qMessage.edit("*Произошла магия, скорее всего чёрная, реакции не посчитались, поэтому голосование" +
					" объявляется несостоявшимся по техническим причинам. Попробуйте ещё раз позже.*");
			qMessage.removeAllReactions();
			message.getClient().changePlayingText(Bot.status);
			return;
		}

//		System.out.println("Всего реакций: " + reactions.size() + ":");
//		for (IReaction reaction : reactions) System.out.println(reaction.getEmoji() + " -> " + reaction.getUsers());

		HashSet<IUser> voters = new HashSet<>();
		int winnerNumber = 0;
		IReaction winner = reactions.get(0);
		for (int i = 0; i < reactions.size(); i++) {
			//выявляю вариант-победитель:
			if (reactions.get(i).getUsers().size() > winner.getUsers().size()) {
				winner = reactions.get(i);
				winnerNumber = i;
			}
			//попутно удаляю ботов:
			// FIXME: 17.11.2017 не удаляются приходится обходить
			for (IUser user : reactions.get(i).getUsers()) {
				if (user.isBot()) reactions.get(i).getUsers().remove(user);
			}
			//попутно считаю уникальных проголосовавших пользователей:
			for (IUser user : reactions.get(i).getUsers()) {
				if (!user.isBot()) voters.add(user);
			}
		}

		//отменяю голосование, если никто не голосовал (не считая бота):
		if (voters.isEmpty()) {
			qMessage.edit("\n" + question + "\n\n" +
					"*Никто не голосовал! Голосование отменено, результаты аннулированы.*");
			message.getClient().changePlayingText(Bot.status);
			qMessage.removeAllReactions(); // FIXME: 17.11.2017 повтор кода
			return;
		}

		StringBuilder pollResult = calculatePollResult(reactions, answers);

		//удаляю все реакции, чтобы нельзя больше было баловаться кнопками:
		qMessage.removeAllReactions();

		//считаю пользователей не офлайн:
		int usersNumber = 0;
		for (IUser user : message.getGuild().getUsers()) {
			if (!user.isBot() && !user.getPresence().getStatus().name().equals("OFFLINE")) {
				usersNumber++;
			}
		}

		if (answers.length > 2) {
			qMessage.edit("*Голосование завершено!*" + "``` ```" + "\n" + question + "\n\n" +
					"В голосовании приняли участие " + voters.size() + " из " + usersNumber +
					" участников.\n\n" + pollResult.toString() + "\n" + "Вариант " + winner.getEmoji() +
					"`" + answers[winnerNumber] + "`" + " набрал наибольшее количество голосов!");
		} else {
			qMessage.edit("*Голосование завершено!*" + "``` ```" + "\n" + question + "\n\n" +
					"В голосовании приняли участие " + voters.size() + " из " + usersNumber +
					" участников.\n\n" + pollResult.toString() + "\n" + "Вариант " + winner.getEmoji() +
					" набрал наибольшее количество голосов!");
		}

		message.getClient().changePlayingText(Bot.status);
	}

	private String getRandomQuestion() {
		String array[] = {
				"Как вам эта песня? ",
				"Сколько стоит аренда аккаунта?★ Складной нож | Черный глянец/Зависит от внешности арендатора/Не измеряется деньгами",
				"Шла Саша по шоссе?Шла/По кривой дорожке/Бежала/Наташа/Не сушку",
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
				":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};
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
		System.out.println(newArgs.toString());
		//выделяю вопрос отдельно от вариантов ответов.
		return newArgs.toString().split("\\?");
	}

	private StringBuilder calculatePollResult(List<IReaction> reactions, String[] answers) {
		StringBuilder result = new StringBuilder();
		result.append("_*Результаты голосования:*_\n\n");
		for (int i = 0; i < reactions.size(); i++) {
			result.append(reactions.get(i).getEmoji());
			if (answers.length > 1) result.append("`").append(answers[i]).append("`");
			result.append(" = ").append("**");
			int c = 0;
			for (IUser user : reactions.get(i).getUsers()) {
				if (!user.isBot()) c++;
			}
			result.append(c).append("**: ");
			for (IUser user : reactions.get(i).getUsers()) {
				if (!user.isBot()) {
					result.append(user.getDisplayName(Bot.guild)).append(", ");
				}
			}
			if (!reactions.get(i).getUsers().isEmpty()) {
				result.replace(result.length() - 2, result.length(), ".\n");
			} else {
				result.append("*нет голосов.*\n");
			}
		}
		return result;
	}

	private StringBuilder fillProgressBar(int c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c; i++) sb.append('█');
		return sb;
	}
}
