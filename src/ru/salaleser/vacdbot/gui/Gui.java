package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gui extends JFrame {

	private static JTextArea textArea;
	private JFrame frame;
	private static final long serialVersionUID = 1L;

	private JTextField textField = new JTextField(12);
	private JLabel labelStatus;

	private ArrayList<IUser> userList = new ArrayList<>();
	private DefaultListModel listModelUsers = new DefaultListModel();
	private JList listOfUsers = new JList(listModelUsers);

	private ArrayList<IChannel> channelList = new ArrayList<>();
	private DefaultListModel listModelChannels = new DefaultListModel();
	private JList listOfChannels = new JList(listModelChannels);

	public Gui() {
		frame = new JFrame("Версия: " + serialVersionUID + " § Играет в " + Bot.status);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(new JPanel());

		JPanel panelStatus = new JPanel(new BorderLayout());
		ImageIcon disconnectedIcon = resizeImage("img/red_dot.png");
		labelStatus = new JLabel("Disconnected", disconnectedIcon, JLabel.CENTER);
		panelStatus.setBorder(BorderFactory.createTitledBorder("Choose user to reply"));
		panelStatus.add(labelStatus);

		JPanel panelLeft = new JPanel(new BorderLayout());
		panelLeft.add(panelStatus, BorderLayout.NORTH);
		panelLeft.add(new JScrollPane(listOfUsers), BorderLayout.SOUTH);
		panelLeft.add(new JScrollPane(listOfChannels), BorderLayout.CENTER);

		JButton buttonSendMessage = new JButton("Send");
		buttonSendMessage.addActionListener(e -> {
			IChannel channel = channelList.get(listOfChannels.getSelectedIndex());
			if (listOfUsers.isSelectionEmpty()) {
				channel.sendMessage(textField.getText());
			} else {
				channel.sendMessage(userList.get(listOfUsers.getSelectedIndex()) + ", " + textField.getText());
			}
		});

		//добавить тестовые кнопки в окно:
		JComboBox<String> commandComboBox = new JComboBox<>();
		for (Map.Entry<String, Command> entry : Bot.getCommandManager().commands.entrySet()) {
			commandComboBox.addItem("~" + entry.getKey());
		}
		JButton buttonTest = new JButton("Test");
		buttonTest.addActionListener(e -> test(commandComboBox.getSelectedItem().toString()));

		textArea = new JTextArea();

		JPanel panelBottom = new JPanel();
		panelBottom.add(commandComboBox);
		panelBottom.add(buttonTest);
		panelBottom.add(textField);
		panelBottom.add(buttonSendMessage);

		JPanel panel = new JPanel();
		panel.add(textArea);

		frame.getContentPane().add(panelLeft);
		frame.getContentPane().add(panelBottom);
		frame.getContentPane().add(panel);
		frame.getRootPane().setDefaultButton(buttonSendMessage);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(Box.createVerticalGlue());
		setJMenuBar(menuBar);
		menuBar.add(createFileMenu());

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
		JMenu file = new JMenu("Файл");
		JMenuItem openMenuItem = new JMenuItem("Открыть", new ImageIcon("images/open.png"));
		JMenuItem loginMenuItem = new JMenuItem("Подключиться");

		file.add(openMenuItem);
		file.add(loginMenuItem);
		file.addSeparator();

		openMenuItem.addActionListener(e -> new ConfigWindow());
		loginMenuItem.addActionListener(e -> Bot.login());

		return file;
	}

	public void addText(String text) {
		if (textArea.getText().length() != 0) textArea.append("\n");
		textArea.append(text);
		frame.pack();
	}

	private ImageIcon resizeImage(String filename) {
		Image image = new ImageIcon(filename).getImage();
		Image scaledImage = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	public void setConnected(IDiscordClient client) {
		listModelChannels.clear();
		for (int i = 0; i < client.getChannels().size(); i++) {
			channelList.add(client.getChannels().get(i));
			listModelChannels.addElement(client.getChannels().get(i).getName());
		}
		listOfChannels.setSelectedIndex(0);
		listModelUsers.clear();
		for (int i = 0; i < client.getUsers().size(); i++) {
			userList.add(client.getUsers().get(i));
			listModelUsers.addElement(client.getUsers().get(i).getName());
		}

		ImageIcon connectedIcon = resizeImage("img/green_dot.png");
		labelStatus.setIcon(connectedIcon);
		labelStatus.setText("Connected");
	}
}