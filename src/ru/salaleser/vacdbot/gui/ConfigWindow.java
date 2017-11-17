package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.Bot;
import ru.salaleser.vacdbot.Config;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ConfigWindow extends JFrame {

	private JTextField pathTextField;

	public ConfigWindow() {
		super("Укажите путь до конфигурационного файла");
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel contents = new JPanel();
		pathTextField = new JTextField("/Users/salaleser/IdeaProjects/vacdbo-t/out/artifacts/vacdbo_t_jar/");
		JButton button = new JButton("Загрузить");
		button.setDefaultCapable(true);
		button.addActionListener(e -> {
			dispose();
			String path = pathTextField.getText();
			Bot.gui.addText("Пытаюсь загрузить конфигурационный файл " +
					path.substring(0, 8) + "..." + path.substring(path.length() - 8, path.length()));
			Config.readConfigFile(path);
		});

		contents.add(pathTextField);
		contents.add(button);

		setContentPane(contents);
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}
}
