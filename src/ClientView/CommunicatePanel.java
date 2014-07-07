package ClientView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Commands.Executable;
import Model.DebugPrint;
import Model.Message;
import Model.Profile;
import Model.Status;

public class CommunicatePanel extends JPanel {
		
	/* Start objects =========================================================== */
//	private JPanel leftPanel;
//	private JPanel rightPanel;	
//	private JPanel writePanel;
//	private JPanel writeKitPanel;
//	private JPanel profilePanel;
//	private JPanel contactsPanel;
//	private JPanel historyPanel;
//	private JLabel labelUserName;
//	private JLabel labelIdentity;
	
	private ContactsList contactsList;
	private JButton unlogButton;
	private JButton addContactButton;
	private JButton removeContactButton;
	private JComboBox<String> status;
	private JButton sendButton;
	private JButton addConversationButton;
	
	private JTextArea writeTextArea;
	private ChatTabbedPane chatPane;
	private final Profile currentProfile;
	private final MyWindow mainWindow;
	/* Stop objects ============================================================ */

	/* Start constructors functions ============================================ */
	public CommunicatePanel(MyWindow mainWindow, Profile currentProfile) {
		super();
		this.mainWindow = mainWindow;
		this.currentProfile = currentProfile;
		createComponents2();
		//fillPanels();
		createActions();
	}
	private void createComponents2() {
		this.setLayout(new GridBagLayout());
		this.setBackground(new Color(0x385B55));
		GridBagConstraints c = new GridBagConstraints();
		
		
		// lewy panel
		c.insets = new Insets(10,10,10,10);
		c.weightx = 2;
		c.weighty = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		JPanel lefPanel = new JPanel(new GridBagLayout());
		lefPanel.setOpaque(false);
		this.add(lefPanel, c);
		
		// prawy panel
		c.weightx = 20;
		c.weighty = 1;
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		JPanel righPanel = new JPanel();
		righPanel.setOpaque(false);
		this.add(righPanel, c);
		
		//profil panel
		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		JPanel profPanel = new JPanel();
		profPanel.setOpaque(false);
		lefPanel.add(profPanel, c);
		
		//contacts panel
		c.weighty = 6;
		c.gridy = 1;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		JPanel contPanel = new JPanel();
		contPanel.setOpaque(false);
		lefPanel.add(contPanel, c);
		
		// prof panel cd
		profPanel.setLayout(new BoxLayout(profPanel, BoxLayout.Y_AXIS));
		
		// title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setOpaque(false);
		JLabel title1 = new JLabel("ChatChat");
		title1.setFont(new Font("Arial", Font.BOLD, 56));
		title1.setForeground(new Color(0xFFFCC5));
		title1.setHorizontalAlignment(SwingConstants.LEFT);
		title1.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel title2 = new JLabel(currentProfile.getUserName()+" ("+currentProfile.getId().toString()+") ");
		title2.setFont(new Font("Arial", Font.BOLD, 14));
		title2.setForeground(new Color(0xFFFCC5));
		title2.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		titlePanel.add(Box.createRigidArea(new Dimension(0,30)));
		titlePanel.add(title1);
		titlePanel.add(Box.createRigidArea(new Dimension(0,30)));
		titlePanel.add(title2);	
		profPanel.add(titlePanel);

		//
		//butt;
		// buttony statusu i wylogowania panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		unlogButton = new JButton("Wyloguj");
		status = new JComboBox<String>();
		status.addItem("dostepny");
		status.addItem("niedostepny");
		status.addItem("zaraz wracam");
		buttonPanel.add(unlogButton);
		buttonPanel.add(status);
	
		profPanel.add(buttonPanel);
		
		// contc panel
		JPanel friendsPanel = new JPanel();
		friendsPanel.setOpaque(false);
		JLabel friendsLabel = new JLabel("Znajomi");
		friendsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
		friendsLabel.setForeground(new Color(0xFFFCC5));
		addContactButton = new JButton("+");
		removeContactButton = new JButton("-");
		friendsPanel.add(friendsLabel);
		friendsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		friendsPanel.add(addContactButton);
		friendsPanel.add(removeContactButton);
		
		contPanel.setLayout(new BorderLayout(0, 5));
		contPanel.add(friendsPanel, BorderLayout.PAGE_START);
		chatPane = new ChatTabbedPane();
		contactsList = new ContactsList(chatPane);
		contactsList.setSize(200, 700);
		contPanel.add(contactsList, BorderLayout.CENTER);
		
		// right panel cd
		c = new GridBagConstraints();
		righPanel.setLayout(new GridBagLayout());
		// gorny panel
		c.weightx = 1;
		c.weighty = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		JPanel readPanel = new JPanel(new GridBagLayout());
		readPanel.setOpaque(false);
		righPanel.add(readPanel, c);
		
		// dolny panel
		c.weightx = 1;
		c.weighty = 2;
		c.gridy = 4;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		JPanel writePanel = new JPanel();
		writePanel.setOpaque(false);
		righPanel.add(writePanel, c);
		
		// writekit, writeArea
		writePanel.setLayout(new BorderLayout(0, 0));
		
		JPanel writeKitPanel = new JPanel();
		writeKitPanel.setOpaque(false);
		sendButton = new JButton("Wyslij");
		addConversationButton = new JButton("Stworz rozmowe");
		writeKitPanel.add(sendButton);
		writeKitPanel.add(addConversationButton);
		writePanel.add(writeKitPanel, BorderLayout.PAGE_START);

		writeTextArea = new JTextArea();
		writeTextArea.setFont(new Font("Arial", Font.PLAIN, 10));
		writeTextArea.setWrapStyleWord(true);
		writeTextArea.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(writeTextArea);
		writePanel.add(scroll, BorderLayout.CENTER);
		
		// readPanel
		readPanel.setLayout(new GridLayout());
		readPanel.add(chatPane);
	}
	private void createActions() {
		/**
		 * Kiedy w polu pisania wiadomoœci klikniemy enter to wiadomoœæ ma zostaæ wys³ana.
		 */
		writeTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {/*do nothing*/}			
			@Override
			public void keyReleased(KeyEvent e) {/*do nothing*/}		
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
					writeTextArea.setText(null);
				}
			}
		});
		/**
		 * Kiedy klikniemy przycisk wyœlij, to tekst wpisany w polu pisania wiadomoœci zostanie wys³any jako wiadomoœæ.
		 */
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();		
			}
		});
		/**
		 * Tworzy dialogbox, który prosi o podanie danych do rozmowy.
		 * Jeœli u¿ytkownik w polu id wprowadzi znaki nieliczbowe, to zostanie wyrzucony exception 
		 * i zadna karta sie nie utworzy.
		 */
		addConversationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile profile = createProfileDialogGetter("Utworz nowa rozmowe");
				if(profile != null)
					chatPane.addChat(profile);
			}
		});
		/**
		 * Kiedy wybierzemy w liœcie rozwijalnej inny status, to pobierana jest jego nazwa i  wysy³ana do kontrolera by wyslal
		 * nowy status do serwera.
		 * String pochodz¹cy z widoku jest parsowany w kontrolerze.
		 */
		status.addActionListener(new ActionListener() {	
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(final ActionEvent e) {
				DebugPrint.print("status listener zadzialal");
				try {
					mainWindow.getControler().putCommand( new Executable() {
						@Override
						public void execute() {
							mainWindow.getControler().
							windowChangeStatus( (String)( (JComboBox<String>)e.getSource() ).getSelectedItem() );
						}
					});
				} catch (InterruptedException e1) {}
			}
		});
		/**
		 * Powoduje wys³anie do kontrolera informacji o wylogowaniu konta oraz widok zmienia modu³ w oknie.
		 */
		unlogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.unlogProfile();
			}
		});
		/**
		 * Tworzy okno dialogowe do wprowadzenia nowego kontaktu.
		 */
		addContactButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Profile profile = createProfileDialogGetter("Stworz nowy kontakt");
				if(profile!=null)
					contactsList.addContact(profile);
			}
		});
		/**
		 * Usuwa zaznaczona pozycje z listy kontaktow
		 */
		removeContactButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				contactsList.deleteSelected();	
			}
		});
	}
	
	/* Stop constructors functions ============================================= */
	
	/* Start controlers functions ============================================== */
	/**
	 * Funkcja wywo³ywana przez kontroler, który otrzyma³ now¹ wiadomoœæ.
	 * Sprawdzane jest czy istnieje juz otwarta rozmowa z nadawc¹.
	 * Jeœli tak to nowa wiadomoœæ dodawana jest do istniej¹cego chatu.
	 * Jeœli nie to nowa wiadomoœæ dodawawana jest do nowotworzonego chatu.
	 * @param message
	 */
	public void addNewMessage(Message message) {
		DebugPrint.print("wiadomosc addnewmessage:"+message.getText(), 0);
		chatPane.addMessage(message.getSenderProfile(), message.getReceiverProfile(), message.getText());
	}
	public void updateStatusList(Map<Integer, Status> map){
		for (Integer id : map.keySet()) {
			contactsList.setStatus(id, ((Status)map.get(id)).toString());
		}
	}
	/**
	 * Dodaje now¹ karte czatu z podanym profilem.
	 * @param profile
	 */
	public void addChat(Profile profile) {
		chatPane.addChat(profile);
	}
	/**
	 * Funkcja zwraca to co zwraca funkcja listy kontaktów - wszystkie kontakty jakie u¿ytkownik doda³ do listy - profile.
	 * @return
	 */
	public ArrayList<Profile> getAllContacts() {
		return contactsList.getAllContacts();
	}
	/* Stop controlers functions ====================--======================== */
	
	/* Start view functions ==================================================== */
	/**
	 * Po wpisaniu jakiegoœ tekstu do konsoli i naciœniêciu enter lub naciœniêciu przycisku wys³ania 
	 * sprawdzane jest czy chatPanel posiada jakieœ otwarte rozmowy.
	 * Jeœli tak to wysy³a tekst zawarty w konsoli do tej rozmowy i wysy³a do serwera informacje by wys³a³ wiadomoœæ do 
	 * profilu przypisanego do tej rozmowy.
	 * Po wys³aniu. Konsola jest czyszczona.
	 */
	private void sendMessage()  {
		if(chatPane.getTabCount() > 0){
			final String message = new String(writeTextArea.getText());
			DebugPrint.print("wiadomosc sendmessage:"+message, 0);
			chatPane.addMessage(currentProfile, message);
			DebugPrint.print(chatPane.getProfile().getId().toString(),0);
			try {
				mainWindow.getControler().putCommand(new Executable() {
					@Override
					public void execute() {
						mainWindow.getControler().windowMessenging(currentProfile, 
								chatPane.getProfile(), message);
					}
				});
			} catch (InterruptedException e) { /* do nothing */}
		}
		writeTextArea.setText("");
		DebugPrint.print("Wyslano do serwera");
	}
	/**
	 * Wywo³uje okno dialogowe z tytu³em/poleceniem dla u¿ytkownika takim jak parametr text.
	 * Na podstawie wprowadzonych przez u¿ytkownika danych tworzony jest profil i zwracany.
	 * Jeœli u¿ytkownik Ÿle wprowadzi³ dane to zostanie zwrócone null.
	 * @param text
	 * @return
	 */
	private Profile createProfileDialogGetter(String text) {
		JTextField identity = new JTextField();
		JTextField name = new JTextField();
		Profile profile;
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Id"),
				identity,
				new JLabel("Username"),
				name
		};
		JOptionPane.showMessageDialog(null, inputs, text, JOptionPane.PLAIN_MESSAGE);
		DebugPrint.print(identity.getText());
		try {
			profile = new Profile(Integer.parseInt(identity.getText()), name.getText());
		}
		catch(NumberFormatException ex) { 
			return null;
		}
		return profile;
	}
	/* Stop view functions ===================================================== */
	
}