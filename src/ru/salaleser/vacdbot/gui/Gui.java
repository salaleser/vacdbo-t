package ru.salaleser.vacdbot.gui;

import com.sun.java.swing.action.ExitAction;
import ru.salaleser.vacdbot.Bot;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class Gui extends JFrame {

	private static JTextArea textArea;

	private static final long serialVersionUID = 1L;

	public Gui() {
		super("Окно бота");
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel contents = new JPanel();

		JMenuBar menuBar = new JMenuBar();
		String[][] menuFile = {{"Файл", "Ф", "", ""}, {"Открыть", "О", "O", ""}, {"Сохранить", "С", "S", ""}};
		menuBar.add(createMenuItems(menuFile));
		String[][] menuEdit = {{"Редактирование", "Р", "", ""}, {"Вырезать", "В", "X", "images/cut.png"}, {"Копировать", "К", "C", "images/copy.png"}};
		menuBar.add(createMenuItems(menuEdit));
		menuBar.add(createSubmenus());
		menuBar.add(Box.createHorizontalGlue());
		setJMenuBar(menuBar);

		menuBar.add(createFileMenu());
		menuBar.add(createViewMenu());

		textArea = new JTextArea();
		contents.add(textArea);

		setContentPane(contents);
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}

	private JMenu createFileMenu() {
		// Создание выпадающего меню
		JMenu file = new JMenu("Файл");
		// Пункт меню "Открыть" с изображением
		JMenuItem openMenuItem = new JMenuItem("Открыть", new ImageIcon("images/open.png"));
		JMenuItem loginMenuItem = new JMenuItem("Login");
		// Пункт меню из команды с выходом из программы
		JMenuItem extiMenuItem = new JMenuItem(new ExitAction());
		// Добавление к пункту меню изображения
		extiMenuItem.setIcon(new ImageIcon("images/exit.png"));

		// Добавим в меню пункта open
		file.add(openMenuItem);
		file.add(loginMenuItem);
		// Добавление разделителя
		file.addSeparator();
		file.add(extiMenuItem);

		openMenuItem.addActionListener(e -> new ConfigWindow());
		loginMenuItem.addActionListener(e -> Bot.retry());

		return file;
	}

	private JMenu createViewMenu() {
		// создадим выпадающее меню
		JMenu viewMenu = new JMenu("Вид");
		// меню-флажки
		JCheckBoxMenuItem line  = new JCheckBoxMenuItem("Линейка");
		JCheckBoxMenuItem grid  = new JCheckBoxMenuItem("Сетка");
		JCheckBoxMenuItem navig = new JCheckBoxMenuItem("Навигация");
		// меню-переключатели
		JRadioButtonMenuItem one = new JRadioButtonMenuItem("Одна страница");
		JRadioButtonMenuItem two = new JRadioButtonMenuItem("Две страницы");
		// организуем переключатели в логическую группу
		ButtonGroup bg = new ButtonGroup();
		bg.add(one);
		bg.add(two);
		// добавим все в меню
		viewMenu.add(line);
		viewMenu.add(grid);
		viewMenu.add(navig);
		// разделитель можно создать и явно
		viewMenu.add( new JSeparator());
		viewMenu.add(one);
		viewMenu.add(two);
		return viewMenu;
	}

	public void addText(String text) {
		if (textArea.getText().length() != 0) textArea.append("\n");
		textArea.append(text);
		pack();
	}

	private JMenu createMenuItems(final String[][] items) {
		// Создание выпадающего меню
		JMenu menu = new JMenu(items[0][0]);
		menu.setMnemonic(items[0][1].charAt(0));
		for (int i = 1; i < items.length; i++) {
			// пункт меню "Открыть"
			JMenuItem item = new JMenuItem(items[i][0]);
			item.setMnemonic(items[i][1].charAt(0)); // русская буква
			// установим клавишу быстрого доступа (латинская буква)
			item.setAccelerator(KeyStroke.getKeyStroke(items[i][2].charAt(0), KeyEvent.CTRL_MASK));
			if (items[i][3].length() > 0) item.setIcon(new ImageIcon(items[i][3]));
			menu.add(item);
		}
		return menu;
	}

	private JMenu createSubmenus() {
		JMenu text = new JMenu("Текст");
		// и несколько вложенных меню
		JMenu style = new JMenu("Стиль");
		JMenuItem bold = new JMenuItem("Жирный");
		JMenuItem italic = new JMenuItem("Курсив");
		JMenu font = new JMenu("Шрифт");
		JMenuItem arial = new JMenuItem("Arial");
		JMenuItem times = new JMenuItem("Times");
		font.add(arial);
		font.add(times);
		// размещаем все в нужном порядке
		style.add(bold);
		style.add(italic);
		style.addSeparator();
		style.add(font);
		text.add(style);
		return text;
	}
}