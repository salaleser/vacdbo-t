package ru.salaleser.vacdbot.gui;

import com.sun.java.swing.action.ExitAction;
import ru.salaleser.vacdbot.Bot;
import ru.salaleser.vacdbot.command.Command;
import ru.salaleser.vacdbot.command.VacCommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gui extends JFrame {

	private static JTextArea textArea;

	private static final long serialVersionUID = 1L;

	public Gui() {
		super("Играет в " + Bot.status);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel contents = new JPanel();

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(Box.createVerticalGlue());
		setJMenuBar(menuBar);
		menuBar.add(createFileMenu());

		//добавить тестовые кнопки в окно:
		JPanel buttonsPanel = new JPanel();
		for (Map.Entry<String, Command> entry : Bot.getCommandManager().commands.entrySet()) {
			JButton button = new JButton("~" + entry.getKey());
			buttonsPanel.add(button);
			button.addActionListener(e -> test(button.getText()));
		}
		contents.add(buttonsPanel, BorderLayout.NORTH);

		JPanel consolePanel = new JPanel();
		textArea = new JTextArea();
		consolePanel.add(textArea);
		contents.add(consolePanel, BorderLayout.SOUTH);

		setContentPane(contents);
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}

	private void test(String text) {
		Command command = Bot.getCommandManager().getCommand(text.substring(1));
		try {
			command.handle(Bot.test.sendMessage("*Тест* `" + text + "`:"), new String[]{});
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private JMenu createFileMenu() {
		// Создание выпадающего меню
		JMenu file = new JMenu("Файл");
		// Пункт меню "Открыть" с изображением
		JMenuItem openMenuItem = new JMenuItem("Открыть", new ImageIcon("images/open.png"));
		JMenuItem loginMenuItem = new JMenuItem("Подключиться");
		// Пункт меню из команды с выходом из программы
//		JMenuItem extiMenuItem = new JMenuItem(new ExitAction());
		// Добавление к пункту меню изображения
//		extiMenuItem.setIcon(new ImageIcon("images/exit.png"));

		// Добавим в меню пункта open
		file.add(openMenuItem);
		file.add(loginMenuItem);
		// Добавление разделителя
		file.addSeparator();
//		file.add(extiMenuItem);

		openMenuItem.addActionListener(e -> new ConfigWindow());
		loginMenuItem.addActionListener(e -> Bot.login());

		return file;
	}

	public void addText(String text) {
		if (textArea.getText().length() != 0) textArea.append("\n");
		textArea.append(text);
		pack();
	}
}