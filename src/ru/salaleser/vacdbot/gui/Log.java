package ru.salaleser.vacdbot.gui;

import javax.swing.*;
import java.awt.*;

public class Log extends JFrame {

	private JTextArea textAreaLog;

	JTextArea getTextAreaLog() {
		return textAreaLog;
	}

	public Log() {
		super("Укажите путь до конфигурационного файла");
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Лог"));
		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		textAreaLog.setColumns(64);
		JButton buttonClearLog = new JButton("Очистить");
		JToggleButton toggleButtonWrap = new JToggleButton("Перенос слов");
		JToggleButton toggleButtonWrapStyle = new JToggleButton("Перенос по словам");
		JButton buttonHide = new JButton("Скрыть лог");
		JToggleButton toggleButtonAlwaysOnTop = new JToggleButton("Всегда сверху");
		buttonClearLog.addActionListener(e -> textAreaLog.setText(null));
		toggleButtonWrap.addActionListener(e -> {
			textAreaLog.setLineWrap(toggleButtonWrap.isSelected());
			toggleButtonWrapStyle.setEnabled(toggleButtonWrap.isSelected());
		});
		toggleButtonWrapStyle.addActionListener(e -> textAreaLog.setWrapStyleWord(toggleButtonWrapStyle.isSelected()));
		buttonHide.addActionListener(e -> setVisible(false));
		toggleButtonAlwaysOnTop.addActionListener(e -> setAlwaysOnTop(toggleButtonAlwaysOnTop.isSelected()));
		JPanel panelButtons = new JPanel(new GridLayout(1, 5));
		panelButtons.add(buttonClearLog);
		panelButtons.add(toggleButtonWrap);
		panelButtons.add(toggleButtonWrapStyle);
		panelButtons.add(buttonHide);
		panelButtons.add(toggleButtonAlwaysOnTop);

		panel.add(panelButtons, BorderLayout.NORTH);
		panel.add(new JScrollPane(textAreaLog), BorderLayout.SOUTH);

//		setUndecorated(true);
//		setOpacity(0.80F);
		getRootPane().setDefaultButton(buttonClearLog);
		setContentPane(panel);
		pack();
		this.setLocationRelativeTo(null);
	}

}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР