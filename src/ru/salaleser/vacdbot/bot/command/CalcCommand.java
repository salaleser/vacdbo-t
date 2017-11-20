package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Util;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CalcCommand extends Command {

	private ArrayList<String> operands = new ArrayList<>();
	private ArrayList<Character> operators = new ArrayList<>();

	public CalcCommand() {
		super("calc", "" +
				Util.b("Описание:") + " Производит нехитрые манипуляции с числами.\n" +
				Util.b("Использование:") + " `~calc <операнд1><оператор><операнд2>`.\n" +
				Util.b("Предустановки:") + " пока нет.\n" +
				Util.b("Пример:") + " `=103*23`.\n" +
				Util.b("Поддерживаемые операции:") + " `+`, `-`, `*`, `/`, `^`. Пока только с двумя операндами за одну операцию.\n" +
				Util.b("Примечание:") + " можно использовать знак `=` для активации команды.");
	}

	@Override
	public void handle(IMessage message, String[] args) throws InterruptedException {
		operands.clear();
		operators.clear();
		if (args.length == 0) return;
		//убрать пробелы:
		StringBuilder formula = new StringBuilder();
		for (String arg : args) formula.append(arg);
		IMessage rMessage = message.getChannel().sendMessage(formula.toString());

		//распихать операнды и операторы по массивам:
		String[] operandsArray = formula.toString().split("[+-/*^]");

		for (String operand : operandsArray) if (Util.isNumeric(operand)) operands.add(operand);
		System.out.println(operands);

		operators.add(formula.charAt(operandsArray[0].length()));
		System.out.println(operators);

		if (operands.size() < 2) {
			message.getChannel().sendMessage("Ниасилил, слишкам многа букав.");
			return;
		}
		double operand1 = Double.parseDouble(operands.get(0));
		double operand2 = Double.parseDouble(operands.get(1));

		String expression = operand1 + "" + operators.get(0) + "" + operand2 + "=" +
				Util.b(calculate(operand1, operand2).get(0).toString());

		TimeUnit.MILLISECONDS.sleep(500);

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
