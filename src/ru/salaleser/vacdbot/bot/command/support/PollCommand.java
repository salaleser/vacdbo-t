package ru.salaleser.vacdbot.bot.command.support;

import com.vdurmont.emoji.EmojiManager;
import ru.salaleser.vacdbot.DBHelper;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static ru.salaleser.vacdbot.Util.*;

public class PollCommand extends Command {

	public PollCommand() {
		super("poll", SUPPORT, "Создаёт голосование.", new String[]{"голосование"});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~poll [<вопрос>?[<вариант_ответа>/[<вариант_ответа>]]]`.",
				"`~poll` — голосование с рандомным вопросом;\n" +
						"`~poll map` — голосование за карту в ксго.",
				"`~poll Есть ли жизнь на Марсе? Да / Нет / Не уверен`.",
				"вариантов ответов может быть до 10; разделителем для вопроса является\n" +
						"вопросительный знак `?`, а для вариантов ответов — слэш `/`."
				)
		);
	}

	// FIXME: 23.02.2018 на данный момент при равном количестве голосов бот засчитывает победу первому варианту

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		message.getClient().changePlayingText("голосование");
		this.guild = guild;

		int countdown = 20;
		String countdownValue = DBHelper.getOption(guild.getStringID(), name, "countdown");
		if (isNumeric(countdownValue) &&
				Integer.parseInt(countdownValue) >= 5 &&
				Integer.parseInt(countdownValue) <= 60) {
			countdown = Integer.parseInt(countdownValue);
		}

		IMessage qMessage = message.getChannel().sendMessage("Инициализация голосования...");

		StringBuilder answersEnum = new StringBuilder("\n");
		String questionAndAnswers[] = splitQuestionAndAnswers(args);
		String question = b(questionAndAnswers[0] + "?");
		String answers[] = questionAndAnswers[1].split("/");

		//добавляю кнопки для голосования в виде реакций:
		// FIXME: 30.11.2017 Лёха из будущего, ну научись уже пользоваться новыми молодёжными эмодзи, а?
		if (answers.length == 0 || answers.length == 1) {
			delay(100);
			qMessage.addReaction(EmojiManager.getByUnicode("👍"));
			delay(100);
			qMessage.addReaction(EmojiManager.getByUnicode("👎"));
		} else if (answers.length <= 10) {
			for (int i = 0; i < answers.length; i++) {
				answersEnum.append(EmojisNumbers.values()[i].emoji()).append(" — ").append(code(answers[i])).append("\n");
			}
			for (int i = 0; i < answers.length; i++) {
				delay(100);
				qMessage.addReaction(EmojisNumbers.values()[i].emoji());
			}
		} else {
			message.reply(i("неправильное количество вариантов ответов"));
		}

		//перерисовываю сообщение:
		StringBuilder progressBar;
		String barchar = "█";
		if (DBHelper.getOption(guild.getStringID(), name, "barchar").length() == 1) {
			barchar = DBHelper.getOption(guild.getStringID(), name, "barchar");
		}
		String pollWrapper = "\n" + question + "\n" + answersEnum;
		for (int i = countdown; i > 0; i--) {
			delay(1000);
			progressBar = fillProgressBar(barchar, i);
			qMessage.edit(i("Голосование завершится через " + i + " с") + block(progressBar.toString()) + pollWrapper);
		}
		delay(1000);
		qMessage.edit(i("Ставки сделаны! Ставок больше нет. Идёт подсчёт голосов...") + block(" ") + pollWrapper);

		//получаю реакции-голоса пользователей в отдельный лист:
		delay(2000);
		List<IReaction> reactions = qMessage.getReactions();

		// FIXME: 17.11.2017 чёрная магия:
		//проверка на чёрную магию дискорда (по невыясненным причинам иногда реакций нет совсем):
		if (reactions.isEmpty()) {
			qMessage.edit(i("Произошла магия, скорее всего чёрная, реакции не посчитались, скорее всего " +
					"это проблема в дискорде, но может быть и в моих кривых руках, в любом случае я постараюсь это " +
					"починить. Голосование объявляется несостоявшимся по техническим причинам. Попробуйте ещё раз."));
			qMessage.removeAllReactions();
			message.getClient().changePlayingText(Bot.STATUS);
			return;
		}
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
			for (IUser user : reactions.get(i).getUsers()) if (user.isBot()) reactions.get(i).getUsers().remove(user);
			//попутно считаю уникальных проголосовавших пользователей:
			for (IUser user : reactions.get(i).getUsers()) if (!user.isBot()) voters.add(user);
		}

		//отменяю голосование, если никто не голосовал (не считая бота):
		if (voters.isEmpty()) {
			qMessage.edit("\n" + question + "\n\n" + i("Никто не голосовал! Голосование отменено, результаты аннулированы."));
			message.getClient().changePlayingText(Bot.STATUS);
			qMessage.removeAllReactions(); // FIXME: 17.11.2017 повтор кода
			return;
		}

		StringBuilder pollResult = calculatePollResult(reactions, answers);

		//удаляю все реакции, чтобы нельзя больше было баловаться кнопками:
		qMessage.removeAllReactions();

		//считаю пользователей не офлайн:
		int usersNumber = 0;
		for (IUser user : message.getGuild().getUsers()) {
			if (!user.isBot() && !user.getPresence().getStatus().name().equals("OFFLINE")) usersNumber++;
		}

		String result = b("Голосование завершено!") + block(" ") + "\n" + question + "\n\n" +
				"В голосовании приняли участие " + voters.size() + " из " + usersNumber +
				" участников.\n\n" + pollResult.toString() + "\n" + "Вариант " + winner.getEmoji();
		if (answers.length > 2) result += code(answers[winnerNumber]) + " набрал наибольшее количество голосов!";
		else result += " набрал наибольшее количество голосов!";
		qMessage.edit(result);

		message.getClient().changePlayingText(Bot.STATUS);
	}

	private String getRandomQuestion() {
		String array[] = {
				"Кто пойдёт в пубг? ",
				"Сколько будет 2+2*2?1/2/3/4/5/6/7/8",
				"Сколько тебе лет?12/13/14/Старше 14"
		};
		int random = new Random().nextInt(array.length);
		return array[random];
	}

	private String getMapQuestion() {
		StringBuilder mapQuestion = new StringBuilder("В какую карту хочешь сыграть?");
		String[] maps = {"de_train", "de_nuke", "de_dust2", "de_cache", "de_mirage",
				"de_inferno", "de_cobblestone", "de_overpass", "cs_office", "cs_agency"};
		for (int i = 0; i < maps.length; i++) {
			mapQuestion.append(maps[i]);
			if (i + 1 != maps.length) mapQuestion.append("/");
		}
		return mapQuestion.toString();
	}

	private String[] splitQuestionAndAnswers(String[] args) {
		//если нет аргументов, то возвращаю рандомный вопрос:
		if (args.length == 0) return getRandomQuestion().split("\\?");
		//если первый аргумент "map", то отдаю предустановку:
		if (args[0].equals("map")) return getMapQuestion().split("\\?");
		//добавляю разделитель за ленивых:
		if (args.length == 1) args[0] += "?";
		//создаю новый массив аргументов:
		StringBuilder newArgs = new StringBuilder();
		//возвращаю обратно пробелы,
		for (String arg : args) newArgs.append(arg).append(" ");
		//выделяю вопрос отдельно от вариантов ответов.
		return newArgs.toString().split("\\?");
	}

	private StringBuilder calculatePollResult(List<IReaction> reactions, String[] answers) {
		StringBuilder result = new StringBuilder();
		result.append(bi("Результаты голосования:\n\n"));
		for (int i = 0; i < reactions.size(); i++) {
			result.append(reactions.get(i).getEmoji());
			if (answers.length > 1) result.append(code(answers[i]));
			result.append(" = **");
			int c = 0;
			for (IUser user : reactions.get(i).getUsers()) if (!user.isBot()) c++;
			result.append(c).append("**: ");
			for (IUser user : reactions.get(i).getUsers()) {
				if (!user.isBot()) result.append(getName(guild, user)).append(", ");
			}
			if (!reactions.get(i).getUsers().isEmpty()) {
				result.replace(result.length() - 2, result.length(), ".\n");
			} else {
				result.append(b("нет голосов.\n"));
			}
		}
		return result;
	}

	private StringBuilder fillProgressBar(String barchar, int c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < c; i++) sb.append(barchar);
		return sb;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР