package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.bot.Bot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Log extends JFrame {

	private JTextArea textAreaLog;
	JFrame frame;

	JTextArea getTextAreaLog() {
		return textAreaLog;
	}

	public Log() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane(new JPanel());

		JPanel panelTitle = new JPanel();
		JLabel labelTitle = new JLabel("Лог");
//		Font titleFont = new Font("Arial", Font.BOLD, 20);
//		labelTitle.setFont(titleFont);
//		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
//		labelTitle.setForeground(Color.WHITE);
//		labelTitle.setPreferredSize(new Dimension(0, 40));
		panelTitle.add(labelTitle);

		JPanel panelTextArea = new JPanel();
		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		textAreaLog.setFocusable(false);
		textAreaLog.setColumns(64);
		JScrollPane scrollPane = new JScrollPane(textAreaLog);
		panelTextArea.add(scrollPane);

		JPanel panelButtons = new JPanel(new GridLayout(1, 4));
		JButton buttonClearLog = new JButton("Очистить");
		JToggleButton toggleButtonWrap = new JToggleButton("Перенос слов");
		JToggleButton toggleButtonWrapStyle = new JToggleButton("Перенос по словам");
		JToggleButton toggleButtonAlwaysOnTop = new JToggleButton("Всегда сверху");
		buttonClearLog.addActionListener(e -> textAreaLog.setText(null));
		toggleButtonWrap.addActionListener(e -> {
			textAreaLog.setLineWrap(toggleButtonWrap.isSelected());
			toggleButtonWrapStyle.setEnabled(toggleButtonWrap.isSelected());
		});
		toggleButtonWrapStyle.addActionListener(e -> textAreaLog.setWrapStyleWord(toggleButtonWrapStyle.isSelected()));
		toggleButtonAlwaysOnTop.addActionListener(e -> setAlwaysOnTop(toggleButtonAlwaysOnTop.isSelected()));
		panelButtons.add(buttonClearLog);
		panelButtons.add(toggleButtonWrap);
		panelButtons.add(toggleButtonWrapStyle);
		panelButtons.add(toggleButtonAlwaysOnTop);

		MouseAdapter listener = new MouseAdapter() {
			int x, y;
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					x = e.getX();
					y = e.getY();
				}
			}
			public void mouseDragged(MouseEvent e) {
				Point currentCoordinates = e.getLocationOnScreen();
				frame.setLocation(currentCoordinates.x - x, currentCoordinates.y - y);
			}
		};
		labelTitle.addMouseListener(listener);
		labelTitle.addMouseMotionListener(listener);

		frame.getContentPane().add(panelTitle);
		frame.getContentPane().add(panelButtons);
		frame.getContentPane().add(panelTextArea);

		frame.getRootPane().setDefaultButton(buttonClearLog);

		frame.setSize(new Dimension(512, 128));

		//		setUndecorated(true);
//		setOpacity(0.75F);
		pack();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР