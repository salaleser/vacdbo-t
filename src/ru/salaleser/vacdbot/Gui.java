package ru.salaleser.vacdbot;

import javax.swing.*;

public class Gui extends JFrame {

	public Gui() {
		super("Ошибка загрузки конфигурационного файла!");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel contents = new JPanel();

		JLabel label = new JLabel("Ошибка загрузки конфигурационного файла!");
		contents.add(label);

		setContentPane(contents);
		setSize(300, 120);
		this.setLocationRelativeTo(null);
		setVisible(true);
	}
}
