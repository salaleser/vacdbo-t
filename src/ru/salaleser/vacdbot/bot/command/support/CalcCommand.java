package ru.salaleser.vacdbot.bot.command.support;

import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;

import static ru.salaleser.vacdbot.Util.*;

public class CalcCommand extends Command {

	private ArrayList<String> operands = new ArrayList<>();
	private ArrayList<Character> operators = new ArrayList<>();

	public CalcCommand() {
		super("calc", SUPPORT, "Производит нехитрые манипуляции с числами.", new String[]{"="});
	}

	@Override
	public void help(IMessage message) {
		message.getChannel().sendMessage(buildHelp(description,
				"`~calc <операнд1><оператор><операнд2>`.",
				"пока нет.",
				"`=103*23`.",
				"Поддерживаемые операции: `+`, `-`, `*`, `/`, `^`.\n" +
						"Пока только с двумя операндами за одну операцию." +
						" Можно использовать знак `=` для активации команды."
				)
		);
	}

	@Override
	public void handle(IGuild guild, IMessage message, String[] args) {
		operands.clear();
		operators.clear();
		if (args.length == 0) return;
		//убрать пробелы:
		StringBuilder formula = new StringBuilder();
		for (String arg : args) formula.append(arg);
		IMessage rMessage = message.getChannel().sendMessage(formula.toString());

		//распихать операнды и операторы по массивам:
		String[] operandsArray = formula.toString().split("[+-/*^]");

		for (String operand : operandsArray) if (isNumeric(operand)) operands.add(operand);
		System.out.println(operands);

		operators.add(formula.charAt(operandsArray[0].length()));
		System.out.println(operators);

		if (operands.size() < 2) {
			message.getChannel().sendMessage("не могу такое, я всё ещё учусь.");
			return;
		}
		double operand1 = Double.parseDouble(operands.get(0));
		double operand2 = Double.parseDouble(operands.get(1));

		String expression = operand1 + "" + operators.get(0) + "" + operand2 + "=" +
				b(calculate(operand1, operand2).get(0).toString());

		delay(500);

		rMessage.edit(removeZerosAndDots(expression));
	}

	private double add(double operand1, double operand2) {
		return operand1 + operand2;
	}

	private double sub(double operand1, double operand2) {
		return operand1 - operand2;
	}

	private double mul(double operand1, double operand2) {
		return operand1 * operand2;
	}

	private double div(double operand1, double operand2) {
		return operand1 / operand2;
	}

	private double exp(double operand1, int operand2) {
		for (int i = 1; i < operand2; i++) operand1 *= operand1;
		return operand1;
	}

	//убирает ненужный ноль после точки
	private String removeZerosAndDots(String ex) {
		String current = ex;
		while (ex.charAt(ex.indexOf('.') + 1) == '0') {
			current = ex.substring(0, ex.indexOf('.'));
			current += ex.substring(ex.indexOf('.') + 2, ex.length());
			ex = current;
		}
		return current;
	}

	private ArrayList<Double> calculate(double operand1, double operand2) {
		ArrayList<Double> intermediate = new ArrayList<>();
		switch (operators.get(0)) {
			case '+':
				intermediate.add(add(operand1, operand2));
				break;
			case '-':
				intermediate.add(sub(operand1, operand2));
				break;
			case '*':
				intermediate.add(mul(operand1, operand2));
				break;
			case '/':
				intermediate.add(div(operand1, operand2));
				break;
			case '^':
				intermediate.add(exp(operand1, (int) operand2));
				break;
			default:
				intermediate.add(null);
		}
		return intermediate;
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР