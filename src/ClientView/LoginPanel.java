package ClientView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Commands.Executable;
import Model.DebugPrint;

public class LoginPanel extends JPanel {

	/* Start objects =========================================================== */
	private JLabel labelMessage;
	private JTextField textFieldIdentity;
	private JTextField textFieldPassword;
	private final MyWindow mainWindow;
	/* Stop objects ============================================================ */
	
	/* Start constructors functions ============================================ */
	/**
	 * Wywo³uje funkcje tworz¹c¹ wszystkie komponenty i pod³¹cza listenery obs³uguj¹ce buttony.
	 */
	LoginPanel(MyWindow mainWindow) {
		super();
		this.mainWindow = mainWindow;
		createComponents();
	}
	/**
	 * Funkcja tworz¹ca wszystkie potrzebne komponenty i dodaje listenery.
	 * Listener do przycisku zaloguj, parsuje dane loguj¹ce i wysy³a je do kolejki pakietów, sk¹d poleci do serwera.
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
		JLabel title2 = new JLabel("Logowanie");
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
		JLabel idLabel = new JLabel("Numer identyfikacyjny", JLabel.TRAILING);
		idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel passwordLabel = new JLabel("Haslo", JLabel.TRAILING);
		passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		textFieldIdentity = new JTextField(45);
		textFieldPassword = new JPasswordField(45);
		JButton loginButton = new JButton("Zaloguj");
		loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		dialogPanel.add(idLabel);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		dialogPanel.add(textFieldIdentity);
		dialogPanel.add(Box.createRigidArea(new Dimension(15,15)));
		dialogPanel.add(passwordLabel);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		dialogPanel.add(textFieldPassword);
		dialogPanel.add(Box.createRigidArea(new Dimension(15,15)));
		dialogPanel.add(loginButton);
		dialogPanel.add(Box.createRigidArea(new Dimension(10,10)));
		
		labelMessage = new JLabel("brak komunikatow");
		labelMessage.setSize(100, 200);
		labelMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialogPanel.add(labelMessage);
		dialogPanel.setBorder(BorderFactory.createMatteBorder(10, 30, 10, 30, new Color(0xFEFEFE)));
		
		dialogPanel.setBackground(new Color(0xFEFEFE));
		
		downPanel.add(dialogPanel);
		
		loginButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					parseLogin();
					logInto();
				}
				catch(NumberFormatException ex1) {
					labelMessage.setText("Identyfikator powinien skladac sie z cyfr");
				}
				catch(Exception ex) {
					labelMessage.setText(ex.getMessage());
				}
			}
		});
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
	 * Je¿eli wprowadzone zosta³y poprawne dane, to nie zostanie rzucony wyj¹tek i logowanie bêdzie przebiegaæ 
	 * dalej. W przeciwnym wypadku zostanie wypisany odpowiedni komunikat i nic wiêcej siê nie stanie.
	 * @throws Exception
	 */
	private void parseLogin() throws Exception {
		Integer.parseInt(textFieldIdentity.getText());
		if( textFieldIdentity.getText() == "" | textFieldIdentity.getText() == "nazwa uzytkownika" ) {
			throw new Exception("brak nazwy");
		}
		else if( textFieldIdentity.getText().length() < 1 ) {
			throw new Exception("za krotki login");
		}
		else if( textFieldPassword.getText() == "" | textFieldPassword.getText() == "haslo" ) {
			throw new Exception("brak hasla");
		}
		else if( textFieldPassword.getText().length() < 4 ) {
			throw new Exception("za krotkie haslo");
		}
		DebugPrint.print("brak bledu parsera");
	}
	/* Stop constructors functions ============================================= */
	
	/* Start controlers functions ============================================== */
	/**
	 * Funkcja wywo³ywana przez MyWindow, jako odpowiedŸ z serwera o niepoprawnym logowaniu.
	 */
	public void setNotSuccesfulLogging() {
		labelMessage.setText("Niepoprawne logowanie/Poprawne wylogowanie");
	}
	/**
	 * Funkcja wysy³a do w¹tku kontrolera zapytanie o logowanie. Kontroler wysy³a do serwera dane.
	 * @throws InterruptedException
	 */
	private void logInto() throws InterruptedException {
		mainWindow.getControler().putCommand(new Executable() {
			@Override
			public void execute() {
				mainWindow.getControler().windowLogin(
						Integer.parseInt(textFieldIdentity.getText()), textFieldPassword.getText());
			}
		});
		labelMessage.setText("Wyslano do serwera");
	}
	/* Stop controlers functions ====================--======================== */

}
