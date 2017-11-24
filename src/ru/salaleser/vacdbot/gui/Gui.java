package ru.salaleser.vacdbot.gui;

import ru.salaleser.vacdbot.Util;
import ru.salaleser.vacdbot.bot.Bot;
import ru.salaleser.vacdbot.bot.command.Command;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gui extends JFrame {

	private JFrame frame;
	public static final long serialVersionUID = 1L;

	private IDiscordClient client;

	private JLabel labelStatus;

	private DefaultListModel<String> listModelGuilds = new DefaultListModel<>();
	private JList<String> listOfGuilds = new JList<>(listModelGuilds);

	private DefaultListModel<String> listModelChannels = new DefaultListModel<>();
	private JList<String> listOfChannels = new JList<>(listModelChannels);

	private DefaultListModel<String> listModelUsers = new DefaultListModel<>();
	private JList<String> listOfUsers = new JList<>(listModelUsers);

	private JCheckBox checkBoxBots = new JCheckBox("bots", false);
	private JCheckBox checkBoxNotOfflineUsers = new JCheckBox("!offline", true);
	private JCheckBox checkBoxOfflineUsers = new JCheckBox("offline", false);

	private JTextArea textAreaLog;

	public Gui() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Версия: " + serialVersionUID + "  Играет в " + Bot.status);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane(new JPanel());
		frame.setLayout(new BorderLayout());

		//блок состояния:
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(BorderFactory.createTitledBorder("Статус"));
		ImageIcon disconnectedIcon = resizeImage("img/red_dot.png");
		labelStatus = new JLabel("Disconnected", disconnectedIcon, JLabel.CENTER);
		panelStatus.add(labelStatus);

		//блок настроек:
		JPanel panelSettings = new JPanel(new GridLayout(2, 1));
		panelSettings.setBorder(BorderFactory.createTitledBorder("Настройки"));
		JButton buttonOpenFile = new JButton("Открыть конфигурационный файл");
		buttonOpenFile.addActionListener(e -> new ConfigWindow());
		panelSettings.add(buttonOpenFile);
		JButton buttonRelogin = new JButton("Перелогиниться");
		buttonRelogin.addActionListener(e -> Bot.relogin());
		panelSettings.add(buttonRelogin);

		//общий северный блок:
		JPanel panelNorth = new JPanel(new BorderLayout());
		panelNorth.setBorder(BorderFactory.createTitledBorder("Северная панель"));
		panelNorth.add(panelStatus, BorderLayout.WEST);
		panelNorth.add(panelSettings, BorderLayout.EAST);

		//блок гильдий:
		JPanel panelServers = new JPanel();
		panelServers.setBorder(BorderFactory.createTitledBorder("Серверы"));
		listOfGuilds.addListSelectionListener(e -> {
			updateChannels(listOfGuilds.getSelectedIndices());
			updateUsers(listOfGuilds.getSelectedIndices(), checkBoxBots.isSelected(),
					checkBoxNotOfflineUsers.isSelected(), checkBoxOfflineUsers.isSelected());
		});
		panelServers.add(new JScrollPane(listOfGuilds));

		//блок каналов:
		JPanel panelChannels = new JPanel();
		panelChannels.setBorder(BorderFactory.createTitledBorder("Каналы"));
		listOfChannels.setSelectedIndex(0);
		panelChannels.add(new JScrollPane(listOfChannels));

		//блок пользователей:
		JPanel panelUsers = new JPanel(new GridLayout(1, 2));
		panelUsers.setBorder(BorderFactory.createTitledBorder("Пользователи"));
		checkBoxBots.addChangeListener(new ChangeListenerUsers());
		checkBoxNotOfflineUsers.addChangeListener(new ChangeListenerUsers());
		checkBoxOfflineUsers.addChangeListener(new ChangeListenerUsers());
		listOfGuilds.setPreferredSize(new Dimension(64, 128));
		listOfChannels.setPreferredSize(new Dimension(64, 128));
		listOfUsers.setPreferredSize(new Dimension(256, 1024));
		panelUsers.add(new JScrollPane(listOfUsers));
		JPanel panelUsersSettings = new JPanel(new GridLayout(3, 1));
		panelUsersSettings.add(checkBoxBots);
		panelUsersSettings.add(checkBoxNotOfflineUsers);
		panelUsersSettings.add(checkBoxOfflineUsers);
		panelUsers.add(panelUsersSettings);

		//общий блок слева:
		JPanel panelWest = new JPanel();
		panelWest.setBorder(BorderFactory.createTitledBorder("Западная панель"));
		panelWest.add(panelServers);
		panelWest.add(panelChannels);
		panelWest.add(panelUsers);

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

		//блок отправки сообщения:
		JPanel panelMessage = new JPanel();
		panelMessage.setBorder(BorderFactory.createTitledBorder("Отправка сообщения:"));
		JTextField textFieldMessage = new JTextField(12);
		JButton buttonSendMessage = new JButton("Send");
		buttonSendMessage.addActionListener(e -> {
			if (textFieldMessage.getText().isEmpty()) textFieldMessage.setText("null");
			IChannel channel = client.getChannels().get(listOfChannels.getSelectedIndex());
			if (listOfUsers.isSelectionEmpty()) {
				channel.sendMessage(textFieldMessage.getText());
			}
			else {
				//сначала вырезаю айдишки дискорда из листа:
				ArrayList<String> selectedIDs = new ArrayList<>();
				for (String selected : listOfUsers.getSelectedValuesList()) {
					// TODO: 21.11.2017 найти способ красивее обрезать элементы листа
					selectedIDs.add(selected.substring(selected.length() - 18, selected.length()));
				}
				//далее кастую айдишки в лонги, ищу по ним юзеров и добавляю их в другой лист:
				ArrayList<IUser> selectedUsersList = new ArrayList<>();
				for (String selectedID : selectedIDs) {
					selectedUsersList.add(client.getUserByID(Long.parseLong(selectedID)));
				}
				//наконец, добавляю всех юзеров в стрингбилдер через запятую:
				StringBuilder selectedUsers = new StringBuilder();
				for (IUser user : selectedUsersList) selectedUsers.append(user).append(", ");
				//адресую сообщение всем выделенным в JList listOfUsers:
				channel.sendMessage(selectedUsers + textFieldMessage.getText());
			}
		});
		panelMessage.add(textFieldMessage);
		panelMessage.add(buttonSendMessage);

		//общий блок справа:
		JPanel panelEast = new JPanel(new BorderLayout());
		panelEast.setBorder(BorderFactory.createTitledBorder("Восточная панель"));
		panelEast.add(panelCommands, BorderLayout.NORTH);
		panelEast.add(panelMessage, BorderLayout.SOUTH);

		//блок лога:
		JPanel panelLog = new JPanel(new BorderLayout());
		panelLog.setBorder(BorderFactory.createTitledBorder("Лог"));
		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		textAreaLog.setColumns(64);
		JButton buttonClearLog = new JButton("Очистить");
		JToggleButton toggleButtonWrap = new JToggleButton("Перенос слов");
		JToggleButton toggleButtonWrapStyle = new JToggleButton("Перенос по словам");
		buttonClearLog.addActionListener(e -> textAreaLog.setText(null));
		toggleButtonWrap.addActionListener(e -> {
			textAreaLog.setLineWrap(toggleButtonWrap.isSelected());
			toggleButtonWrapStyle.setEnabled(toggleButtonWrap.isSelected());
		});
		toggleButtonWrapStyle.addActionListener(e -> textAreaLog.setWrapStyleWord(toggleButtonWrapStyle.isSelected()));
		panelLog.add(buttonClearLog, BorderLayout.WEST);
		panelLog.add(toggleButtonWrap, BorderLayout.CENTER);
		panelLog.add(toggleButtonWrapStyle, BorderLayout.EAST);
		panelLog.add(new JScrollPane(textAreaLog), BorderLayout.SOUTH);

		frame.getContentPane().add(panelNorth, BorderLayout.PAGE_START);
		frame.getContentPane().add(panelWest, BorderLayout.WEST);
		frame.getContentPane().add(panelEast, BorderLayout.EAST);
		frame.getContentPane().add(panelLog, BorderLayout.PAGE_END);

		frame.getRootPane().setDefaultButton(buttonSendMessage);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private class ChangeListenerUsers implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			updateUsers(listOfGuilds.getSelectedIndices(),
					checkBoxBots.isSelected(),
					checkBoxNotOfflineUsers.isSelected(),
					checkBoxOfflineUsers.isSelected());
		}
	}

	private void test(String text) {
		Command command = Bot.getCommandManager().getCommand(text.substring(1));
		try {
			command.handle(Bot.channelKTOTest.sendMessage(Util.b("Тест") + Util.code(text) + ":"), new String[]{});
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void addText(String text, Color fg) {
		if (textAreaLog.getText().length() != 0) textAreaLog.append("\n");
		textAreaLog.setForeground(fg);
		textAreaLog.append(text);
		frame.pack();
	}

	private ImageIcon resizeImage(String filename) {
		Image image = new ImageIcon(filename).getImage();
		Image scaledImage = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	public void setConnecting() {
		ImageIcon connectingIcon = resizeImage("img/yellow_dot.png");
		labelStatus.setIcon(connectingIcon);
		labelStatus.setText("Connecting...");
	}

	public void setConnected(IDiscordClient client, String guilds) {
		this.client = client;
		//выделяет все серверы:
		int[] indices = new int[client.getGuilds().size()];
		for (int i = 0; i < indices.length; i++) indices[i] = i;

		updateGuilds(indices);
		updateChannels(indices);
		updateUsers(indices, false, true, false);

		ImageIcon connectedIcon = resizeImage("img/green_dot.png");
		labelStatus.setIcon(connectedIcon);
		labelStatus.setText("Connected to: " + guilds);
	}

	private void updateGuilds(int[] guildIndices) {
		listModelGuilds.clear();
		for (IGuild guild : client.getGuilds()) {
			listModelGuilds.addElement(guild.getName());
		}
		listOfGuilds.setSelectedIndices(guildIndices);
	}

	private void updateChannels(int[] guildIndices) {
		listModelChannels.clear();
		for (int index : guildIndices) {
			for (IChannel channel : client.getGuilds().get(index).getChannels()) {
				listModelChannels.addElement(channel.getName());
			}
		}
		listOfChannels.setSelectedIndex(0);
	}

	private void updateUsers(int[] guildIndices, boolean bots, boolean notOfflineUsers, boolean offlineUsers) {
		listModelUsers.clear();
		for (int index : guildIndices) {
			for (IUser user : client.getGuilds().get(index).getUsers()) {
				if (bots) {
					if (user.isBot()) {
						listModelUsers.addElement(user.getName() + " — " + user.getStringID());
					}
				}
				if (offlineUsers) {
					if (!user.isBot() && user.getPresence().getStatus() == StatusType.OFFLINE) {
						listModelUsers.addElement(user.getName() + " — " + user.getStringID());
					}
				}
				if (notOfflineUsers) {
					if (!user.isBot() && user.getPresence().getStatus() != StatusType.OFFLINE) {
						listModelUsers.addElement(user.getName() + " — " + user.getStringID());
					}
				}
			}
		}
		listOfUsers.clearSelection();
	}
}
// ЭТА ДЛИННАЯ СТРОКА НУЖНА ДЛЯ ТОГО, ЧТОБЫ ПОЯВИЛАСЬ ВОЗМОЖНОСТЬ ГОРИЗОНТАЛЬНО СКРОЛЛИТЬ ДЛЯ ДИСПЛЕЯ С МАЛЕНЬКОЙ ДИАГОНАЛЬЮ, НАПРИМЕР ДЛЯ МОЕГО ОДИННАДЦАТИДЮЙМОВОГО МАКБУКА ЭЙР