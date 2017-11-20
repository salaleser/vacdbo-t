package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gui extends JFrame {

	private JFrame frame;
	public static final long serialVersionUID = 1L;

	private IDiscordClient client;

	private JLabel labelStatus;

	private ArrayList<IGuild> guildList = new ArrayList<>();
	private DefaultListModel<String> listModelGuilds = new DefaultListModel<>();
	private JList<String> listOfGuilds = new JList<>(listModelGuilds);

	private ArrayList<IChannel> channelList = new ArrayList<>();
	private DefaultListModel<String> listModelChannels = new DefaultListModel<>();
	private JList<String> listOfChannels = new JList<>(listModelChannels);

	private ArrayList<IUser> userList = new ArrayList<>();
	private DefaultListModel<String> listModelUsers = new DefaultListModel<>();
	private JList<String> listOfUsers = new JList<>(listModelUsers);
	private JCheckBox checkBoxBots = new JCheckBox("Боты", false);
	private JCheckBox checkBoxNotOfflineUsers = new JCheckBox("Неофлайн", true);
	private JCheckBox checkBoxOfflineUsers = new JCheckBox("Офлайн", false);

	private JTextArea textAreaLog;

	public Gui() {
		frame = new JFrame("Версия: " + serialVersionUID + " § Играет в " + Bot.status);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(new JPanel());

		//блок состояния:
		JPanel panelStatus = new JPanel(new BorderLayout());
		panelStatus.setBorder(BorderFactory.createTitledBorder("Статус"));
		ImageIcon disconnectedIcon = resizeImage("img/red_dot.png");
		labelStatus = new JLabel("Disconnected", disconnectedIcon, JLabel.CENTER);
		panelStatus.add(labelStatus);

		//общий северный блок:
		JPanel panelNorth = new JPanel();
		panelNorth.setBorder(BorderFactory.createTitledBorder("Северная панель"));
		panelNorth.add(panelStatus);

		//блок гильдий:
		JPanel panelServers = new JPanel();
		panelServers.setBorder(BorderFactory.createTitledBorder("Серверы"));
		listOfGuilds.addListSelectionListener(e -> {
			updateChannels(listOfGuilds.getSelectedIndices());
			updateUsers(listOfGuilds.getSelectedIndices(), checkBoxBots.isSelected(),
					checkBoxNotOfflineUsers.isSelected(), checkBoxOfflineUsers.isSelected());
		});
		listOfGuilds.setSelectedIndex(0);
		panelServers.add(new JScrollPane(listOfGuilds));

		//блок каналов:
		JPanel panelChannels = new JPanel();
		panelChannels.setBorder(BorderFactory.createTitledBorder("Каналы"));
		listOfChannels.setSelectedIndex(0);
		panelChannels.add(new JScrollPane(listOfChannels));

		//блок пользователей:
		JPanel panelUsers = new JPanel(new FlowLayout());
		panelUsers.setBorder(BorderFactory.createTitledBorder("Пользователи"));
		JButton buttonUpdate = new JButton("Обновить");

		buttonUpdate.addActionListener(e -> updateUsers(listOfGuilds.getSelectedIndices(), checkBoxBots.isSelected(),
				checkBoxNotOfflineUsers.isSelected(), checkBoxOfflineUsers.isSelected()));
		panelUsers.add(new JScrollPane(listOfUsers));
		panelUsers.add(checkBoxBots);
		panelUsers.add(checkBoxNotOfflineUsers);
		panelUsers.add(checkBoxOfflineUsers);
		panelUsers.add(buttonUpdate);

		//общий блок слева:
		JPanel panelWest = new JPanel();
		panelWest.setBorder(BorderFactory.createTitledBorder("Западная панель"));
		panelWest.add(panelServers);
		panelWest.add(panelChannels);
		panelWest.add(panelUsers);

		//блок отправки сообщения:
		JPanel panelMessage = new JPanel();
		panelMessage.setBorder(BorderFactory.createTitledBorder("Отправка сообщения:"));
		JTextField textFieldMessage = new JTextField(12);
		JButton buttonSendMessage = new JButton("Send");
		buttonSendMessage.addActionListener(e -> {
			// FIXME: 19.11.2017 отправляет сообщения не в тот канал
			if (textFieldMessage.getText().isEmpty()) textFieldMessage.setText("null");
			IChannel selectedChannel = client.getGuilds().get(0).getChannels().get(0);
			if (!listOfChannels.isSelectionEmpty())
				selectedChannel = channelList.get(listOfChannels.getSelectedIndex());
			if (listOfUsers.isSelectionEmpty())
				selectedChannel.sendMessage(textFieldMessage.getText());
			else
				selectedChannel.sendMessage(userList.get(listOfUsers.getSelectedIndex()) +
						", " + textFieldMessage.getText());
		});
		panelMessage.add(textFieldMessage);
		panelMessage.add(buttonSendMessage);

		//блок списка команд:
		JPanel panelCommands = new JPanel();
		panelCommands.setBorder(BorderFactory.createTitledBorder("Отправка команды:"));
		JComboBox<String> commandComboBox = new JComboBox<>();
		for (Map.Entry<String, Command> entry : Bot.getCommandManager().commands.entrySet())
			commandComboBox.addItem("~" + entry.getKey());
		JButton buttonCommand = new JButton("Test");
		buttonCommand.addActionListener(e -> test((String) commandComboBox.getSelectedItem()));
		panelCommands.add(commandComboBox);
		panelCommands.add(buttonCommand);

		//общий блок справа:
		JPanel panelEast = new JPanel();
		panelEast.setBorder(BorderFactory.createTitledBorder("Восточная панель"));
		panelEast.add(panelMessage);
		panelEast.add(panelCommands);

		//блок настроек:
		JPanel panelSettings = new JPanel();
		panelSettings.setBorder(BorderFactory.createTitledBorder("Настройки"));
		JButton buttonOpenFile = new JButton("Открыть конфигурационный файл");
		buttonOpenFile.addActionListener(e -> new ConfigWindow());
		panelSettings.add(buttonOpenFile);
		JButton buttonRelogin = new JButton("Перелогиниться");
		buttonRelogin.addActionListener(e -> Bot.relogin());
		panelSettings.add(buttonRelogin);

		//блок лога:
		JPanel panelLog = new JPanel();
		panelLog.setBorder(BorderFactory.createTitledBorder("Лог"));
		textAreaLog = new JTextArea();
		panelLog.add(textAreaLog);

		//общий блок снизу:
		JPanel panelSouth = new JPanel();
		panelSouth.setBorder(BorderFactory.createTitledBorder("Южная панель"));
		panelSouth.add(panelSettings);
		panelSouth.add(panelLog);

		frame.getContentPane().add(panelNorth, BorderLayout.NORTH);
		frame.getContentPane().add(panelWest, BorderLayout.WEST);
		frame.getContentPane().add(panelEast, BorderLayout.EAST);
		frame.getContentPane().add(panelSouth, BorderLayout.SOUTH);

		frame.getRootPane().setDefaultButton(buttonSendMessage);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void test(String text) {
		Command command = Bot.getCommandManager().getCommand(text.substring(1));
		try {
			command.handle(Bot.channelTest.sendMessage("*Тест* `" + text + "`:"), new String[]{});
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void addText(String text) {
		if (textAreaLog.getText().length() != 0) textAreaLog.append("\n");
		textAreaLog.append(text);
		frame.pack();
	}

	private ImageIcon resizeImage(String filename) {
		Image image = new ImageIcon(filename).getImage();
		Image scaledImage = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	public void setConnected(IDiscordClient client) {
		this.client = client;
		updateLists();
		ImageIcon connectedIcon = resizeImage("img/green_dot.png");
		labelStatus.setIcon(connectedIcon);
		labelStatus.setText("Connected");
	}

	private void updateLists() {
		listModelGuilds.clear();
		for (IGuild guild : client.getGuilds()) {
			guildList.add(guild);
			listModelGuilds.addElement(guild.getName());
		}

		updateChannels(new int[]{0});

		updateUsers(new int[]{0}, false, true, false);
	}

	private void updateChannels(int[] ints) {
		listModelChannels.clear();
		for (int index : ints) {
			for (IChannel channel : client.getGuilds().get(index).getChannels()) {
				channelList.add(channel);
				listModelChannels.addElement(channel.getName());
			}
		}
	}

	private void updateUsers(int[] ints, boolean bots, boolean notOfflineUsers, boolean offlineUsers) {
		listModelUsers.clear();
		for (int index : ints) {
			for (IUser user : client.getGuilds().get(index).getUsers()) {
				if (bots) {
					if (user.isBot()) {
						userList.add(user);
						listModelUsers.addElement(user.getDisplayName(Bot.guildKTO));
					}
				}
				if (offlineUsers) {
					if (!user.isBot() && user.getPresence().getStatus() == StatusType.OFFLINE) {
						userList.add(user);
						listModelUsers.addElement(user.getName());
					}
				}
				if (notOfflineUsers) {
					if (!user.isBot() && user.getPresence().getStatus() != StatusType.OFFLINE) {
						userList.add(user);
						listModelUsers.addElement(user.getName());
					}
				}
			}
		}
	}
}