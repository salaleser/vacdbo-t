package ru.salaleser.vacdbot.vacdbo;

import javax.swing.*;

public class UserInterface {
	private JPanel panel;
	private JTextField textFieldStarts;
	private JTextField textFieldEnds;
	private JButton buttonStart;
	private JButton buttonStop;
	private JComboBox comboBoxMethod;
	private JTextArea textAreaConsole;
	private JSpinner spinner;
	private JLabel labelElapsedIds;
	JProgressBar progressBar;

	private int threads;
	private int counter;

	void addCounter(int steamidCount) {
		counter = Settings.getTotalScanned();
		progressBar.setValue(counter);
		labelElapsedIds.setText("Всего обработано: " + counter);
	}

	private String getMethod() {
		return (String) comboBoxMethod.getSelectedItem();
	}

	void addTextAreaConsole(String text) {
		if (textAreaConsole.getLineCount() > 15) {
			textAreaConsole.replaceRange("...\n", 0, 50);
		}
		textAreaConsole.append(text);
	}

	UserInterface() {
		textFieldStarts.setText(String.valueOf(Settings.getStarts()));
		textFieldEnds.setText(String.valueOf(Settings.getEnds()));
		threads = Settings.getThreads();
		spinner.setValue(threads);
		spinner.addChangeListener(e -> threads = (int) spinner.getValue());

		buttonStart.addActionListener(e -> {
			progressBar.setMaximum((int) (Settings.getEnds() - Settings.getStarts()));
			threads = (int) spinner.getValue();
			counter = 0;
			VACDBA.start(getMethod());
		});

		buttonStop.addActionListener(e -> {
			for (Scanner scanner : VACDBA.scanners) {
				scanner.cancel(true);
			}
		});

		JFrame frame = new JFrame("VACDBA");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
