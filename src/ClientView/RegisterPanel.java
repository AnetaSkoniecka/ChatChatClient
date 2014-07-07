package ClientView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Commands.Executable;
import Model.DebugPrint;
import Model.Profile;


public class RegisterPanel extends JPanel {

	/* Start objects =========================================================== */
	private final MyWindow mainWindow;
	private JTextField textFieldUserName;
	private JTextField textFieldPassword;
	private JLabel labelMessage;
	private JTextField textFieldIdentity;
	/* Stop domestic class ===================================================== */
	
	/* Start constructions functions =========================================== */
	/**
	 * Konstruktor wywo³uje funkcje tworzenia komponentów i dodawania listenerów.
	 */
	RegisterPanel(MyWindow mainWindow) {
		super();
		this.mainWindow = mainWindow;
		createComponents();
	}
	/**
	 * Tworzy wszystkie niezbêdne componenty panelu i dodaje listener do przycisku rejestracji.
	 */
	private void createComponents() {
		this.setPreferredSize(new Dimension(900,600));
		
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.pink);
		GridBagConstraints c = new GridBagConstraints();
		
		// gorny panel
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		JPanel upPanel = new JPanel(new GridBagLayout()); upPanel.setBackground(new Color(0xFEFEFE));
		this.add(upPanel, c);
		
		// dolny panel
		c.weightx = 1;
		c.weighty = 4;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		JPanel downPanel = new JPanel(); downPanel.setBackground(new Color(0x385B55));
		this.add(downPanel, c);
		
		// panel titles
		c.weightx = 2;
		c.weighty = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(30,30,30,30);
		JPanel labelUpPanel = new JPanel(); 
		labelUpPanel.setOpaque(false);
		upPanel.add(labelUpPanel, c);

		// panel upbuttons
		c.weightx = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		JPanel buttonsUpPanel = new JPanel();
		buttonsUpPanel.setOpaque(false);
		upPanel.add(buttonsUpPanel, c);
		
		// dodanie titles
		labelUpPanel.setLayout(new BoxLayout(labelUpPanel, BoxLayout.Y_AXIS));
		JLabel title1 = new JLabel("ChatChat");
		title1.setFont(new Font("Arial", Font.BOLD, 40));
		title1.setForeground(new Color(0x0E9DA1));
		title1.setHorizontalAlignment(SwingConstants.LEFT);
		labelUpPanel.add(title1);
		JLabel title2 = new JLabel("Rejestracja");
		title2.setFont(new Font("Arial", Font.BOLD, 20));
		title2.setForeground(new Color(0x385B55));
		labelUpPanel.add(title2);
		
		// dodanie upbuttons
		JButton registerModeButton = new JButton("Rejestracja");
		JButton loginModeButton = new JButton("Logowanie");
		buttonsUpPanel.setLayout(new BorderLayout());
		JPanel buttonsUpPanel2 = new JPanel();
		buttonsUpPanel2.setOpaque(false);
		buttonsUpPanel2.add(registerModeButton);
		buttonsUpPanel2.add(loginModeButton);
		buttonsUpPanel.add(buttonsUpPanel2, BorderLayout.PAGE_END);

		// dialog panel
		JPanel dialogPanel = new JPanel();
		BoxLayout boxl = new BoxLayout(dialogPanel, BoxLayout.Y_AXIS);

		dialogPanel.setLayout(boxl);
		//dialogPanel.setOpaque(false);
		JLabel userNameLabel = new JLabel("Nazwa uzytkownika", JLabel.TRAILING);
		userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel passwordLabel = new JLabel("Haslo", JLabel.TRAILING);
		passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		textFieldUserName = new JTextField(45);
		textFieldPassword = new JPasswordField(45);
		textFieldIdentity = new JTextField(45);
		textFieldIdentity.setEditable(false);
		textFieldIdentity.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton registerButton = new JButton("Zarejestruj");
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		dialogPanel.add(userNameLabel);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		dialogPanel.add(textFieldUserName);
		dialogPanel.add(Box.createRigidArea(new Dimension(15,15)));
		dialogPanel.add(passwordLabel);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		dialogPanel.add(textFieldPassword);
		dialogPanel.add(Box.createRigidArea(new Dimension(15,15)));
		dialogPanel.add(registerButton);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		dialogPanel.add(textFieldIdentity);
		dialogPanel.add(Box.createRigidArea(new Dimension(30,30)));
		
		labelMessage = new JLabel("brak komunikatow");
		labelMessage.setSize(100, 200);
		labelMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialogPanel.add(labelMessage);
		dialogPanel.setBorder(BorderFactory.createMatteBorder(10, 30, 10, 30, new Color(0xFEFEFE)));
		
		dialogPanel.setBackground(new Color(0xFEFEFE));
		
		downPanel.add(dialogPanel);
		
		registerButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							DebugPrint.print("kliknieto register");
							parseRegister();
							registerData();
						}
						catch(Exception ex) {
							labelMessage.setText(ex.getMessage());
						}
					}	
				}
		);
		
		registerModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainWindow.setWindowMode(0);
			}
		});
		loginModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.setWindowMode(1);
			}
		});
	
	}
	
	/** 
	 * Je¿eli wprowadzone zosta³y poprawne dane, to nie zostanie rzucony wyj¹tek i rejestracja bêdzie przebiegaæ 
	 * dalej. W przeciwnym wypadku zostanie wypisany odpowiedni komunikat i nic wiêcej siê nie stanie.
	 * @throws Exception
	 */
	private void parseRegister() throws Exception {
		if( textFieldUserName.getText() == "" | textFieldUserName.getText() == "nazwa uzytkownika" ) {
			throw new Exception("brak nazwy");
		}
		else if( textFieldUserName.getText().length() < 4 ) {
			throw new Exception("za krotka nazwa");
		}
		else if( textFieldPassword.getText() == "" | textFieldPassword.getText() == "haslo" ) {
			throw new Exception("brak hasla");
		}
		else if( textFieldPassword.getText().length() < 4 ) {
			throw new Exception("za krotkie haslo");
		}
		for(char c : textFieldUserName.toString().toCharArray()) {
			if(c == ' ')
				throw new Exception("nie moga wystepowac spacje");
		}
		for(char c : textFieldPassword.toString().toCharArray()) {
			if(c == ' ')
				throw new Exception("nie moga wystepowac spacje");
		}
		DebugPrint.print("brak bledu parsera");
	}
	/* Stop constructions functions ============================================ */
	
	/* Start controlers functions ============================================== */
	/** Wywo³uje w nowym w¹tku funkcjê z kontrolera - by nie blokowaæ interfejsu, 
	 * jeœli funkcja zwróci false to zostanie wyœwietlony odpowiedni komunikat. 
	 * @throws InterruptedException 
	 */
	private void registerData() throws InterruptedException {
		mainWindow.getControler().putCommand(new Executable() {
			@Override
			public void execute() {
				mainWindow.getControler().windowRegister(textFieldUserName.getText(), textFieldPassword.getText());
			}
		});
		labelMessage.setText("Wyslano do serwera");
	}
	/**
	 * Wywo³ywana z poziomu MainWindow. Jest to odpowiedŸ kontrolera na otrzymanie pakietu rejestracyjnego.
	 * Jeœli przydzielono nowy numer to znaczy, ¿e rejestracja jest poprawna i u¿ytkownik zostaje o tym poinformowany.
	 * @param regPack
	 */
	public void setRegisteredAccount(Profile profile) {
		textFieldIdentity.setText(profile.getId().toString());
		if(profile.getId().toString() != "0000")
			labelMessage.setText("Poprawna rejestracja. Zapisz sobie swoj przydzielony identyfikator, ktory pojawil sie powyzej: ");
	}
	/**
	 * Umieszcza wiadomoœæ.
	 * @param str
	 */
	public void setLabelMessage(String str) {
		labelMessage.setText(str);
	}
	/* Stop controlers functions =============================================== */
}
