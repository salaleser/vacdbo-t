package ru.salaleser.vacdbot.bot.command;

import ru.salaleser.vacdbot.Utilities;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;

public class CalcCommand extends Command {

	private final String[] ops = new String[]{"+", "-", "*", "/", "^"};
	// FIXME: 19.11.2017 надо бы запятую с точкой алиасить
	private ArrayList<String> operands = new ArrayList<>();
	private ArrayList<String> operators = new ArrayList<>();

	public CalcCommand() {
		super("calc", "Считает немножко.");
	}

	@Override
	public void handle(IMessage message, String[] args) {
		if (args.length < 3) {
			System.out.println("Недостаточно операндов!");
			return;
		}
		for (String arg : args) {
			if (Utilities.isNumeric(arg)) operands.add(arg);
			if (isOperator(arg)) operators.add(arg);
		}

		double operand1 = Double.parseDouble(operands.get(0));
		double operand2 = Double.parseDouble(operands.get(1));
		ArrayList<Double> temp = new ArrayList<>();
		StringBuilder expression = new StringBuilder();
		expression.append(operand1).append(operators.get(0))
				.append(operand2).append("=");
		switch (operators.get(0)) {
			case "+":
				temp.add(add(operand1, operand2));
				break;
			case "-":
				temp.add(sub(operand1, operand2));
				break;
			case "*":
				temp.add(mul(operand1, operand2));
				break;
			case "/":
				temp.add(div(operand1, operand2));
				break;
			case "^":
				temp.add(xxx(operand1, operand2));
				break;
		}
		expression.append("**").append(temp.get(0)).append("**");
		message.getChannel().sendMessage(expression.toString());
	}

	private boolean isOperator(String s) {
		for (String op : ops) if (op.equals(s)) return true;
		return false;
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
	private double xxx(double operand1, double operand2) {

	}
}
