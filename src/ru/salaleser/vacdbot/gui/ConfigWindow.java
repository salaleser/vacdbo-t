package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.Config;

import javax.swing.*;

public class ConfigWindow extends JFrame {

	private JTextField pathTextField;

	public ConfigWindow() {
		super("Укажите путь до конфигурационного файла");
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel contents = new JPanel();
		pathTextField = new JTextField("/Users/salaleser/IdeaProjects/vacdbo-t/out/artifacts/vacdbo_t_jar/");
		JButton button = new JButton("Загрузить");
		button.addActionListener(e -> {
			dispose();
			Config.readConfigFile(pathTextField.getText());
		});

		contents.add(pathTextField);
		contents.add(button);

		getRootPane().setDefaultButton(button);
		setContentPane(contents);
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР