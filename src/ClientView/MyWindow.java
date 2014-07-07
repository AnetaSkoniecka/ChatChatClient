package ClientView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import ClientControler.ClientControler;
import Commands.Executable;
import Model.DebugPrint;
import Model.Message;
import Model.Model;
import Model.Profile;
import Model.Status;

/**
 * Tworzy okno g�owne.
 * Okno nas�uchuje samo siebie za pomoc� WindowListenera i gdy okno b�dzie zamykane to zostan� wywo�ane przed zamkni�ciem
 * odpowiednie operacje.
 * @author necia
 *
 */
public class MyWindow implements WindowListener  {

	/* Start domestic class ==================================================== */
	/**
	 * Enum przydany do okre�lniania kt�ry typ okna jest obecnie wy�wietlany.
	 */
	public enum CurrentMode {
		REGISTRATION, LOGIN, COMMUNICATION, STARTPAGE;		
	}
	/* Stop domestic class ===================================================== */

	/* Start objects =========================================================== */
	private JPanel mainPanel;
	private JPanel currentPanel;
	private CurrentMode currentMode;
	private ClientControler controler;
	private Profile currentProfile = null;
	private final JFrame window;
	/* End objects ============================================================= */
	
	/* Start constructors functions ============================================ */
	
	/**
	 * Tworzy okno glowne, komponenty, ustawia domy�lny modu� i wy�wietla okno.
	 * @param model
	 */
	public MyWindow(Model model) {
		window = new JFrame("ChatChat");
		try { 
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			  throw new RuntimeException(e); 
		}
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI();
	    window.setVisible(true);
	    window.addWindowListener(this);
	}
	/**
	 * Funkcja wywo�ywana w konstruktorze. Wywo�uje konstrukcje menubaru i domy�lnie pierwsze wy�wietlane okno.
	 */
	private void createUI() {
		setWindowMode(CurrentMode.REGISTRATION);
	}
	/** 
	 * Ustawia podany modu� w oknie, po czym wywo�uje tworzenie tego okna.
	 * @param mode
	 */
	private void setWindowMode(CurrentMode mode) {
		this.currentMode = mode;
		createMainFrame();
	}
	/**
	 * Funkcja dost�pna dla innych obiekt�w widoku do zmiany bie��cego modu�u.
	 * mode == 0 ustawia modu� rejestracji
	 * mode == 1 ustawia modu� logowania
	 * @param mode
	 */
	public void setWindowMode(int mode) {
		if(mode == 0) {
			setWindowMode(CurrentMode.REGISTRATION);
		} else if(mode == 1) {
			setWindowMode(CurrentMode.LOGIN);
		}
	}
	/**
	 * Po wywo�aniu, funkcja sprawdza jaki obecnie ma by� modu� otwarty, po czym prze��cza obecne okno na nowy modu�.
	 */
	private void createMainFrame() {
		mainPanel = new JPanel(new GridLayout());
		switch(currentMode){
			case REGISTRATION:
				currentPanel = new RegisterPanel(this);
				break;
			case LOGIN:
				currentPanel = new LoginPanel(this);
				break;
			case COMMUNICATION:
				currentPanel = new CommunicatePanel(this, currentProfile);
				break;
			default:	
		}
		mainPanel.setBackground(Color.pink);
		mainPanel.add(currentPanel);
		window.setContentPane(mainPanel);
		mainPanel.setPreferredSize(new Dimension(900, 600));
		window.pack();
	}
	
	/* Stop constructors functions ============================================= */
	
	/* Start controlers functions ============================================== */

	/**
	 * Funkcja pobieraj�ca od u�ytkownika dane dotycz�ce po��czenia z serwerem i rejestruje je w kontrolerze
	 * @param connection
	 * @param dialongName
	 */
	public void SetConnection(Object[] connection){
		JTextField portField = new JTextField("6666");
		JTextField hostField = new JTextField("localhost");
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Numer portu"),
				portField,
				new JLabel("Adres ip/nazwa hosta"),
				hostField 
		};
		JOptionPane.showMessageDialog(null, inputs, "Dane serwera", JOptionPane.PLAIN_MESSAGE);
		while(true){		
		    try{
		        Integer.parseInt(portField.getText());
		        break;
		    }
		    catch(Exception exc){
		    	JOptionPane.showMessageDialog(null, inputs, "B��d po��czenia z serwerem", JOptionPane.PLAIN_MESSAGE);
		    }
		}
    	connection[0] = Integer.parseInt(portField.getText());
    	connection[1] = hostField.getText();
	}
	/**
	 * Wy�wietla komunikaty zadane przez kontroler
	 * @param str
	 */
	public void showMessage(String str) {
		JOptionPane.showMessageDialog(window, str);
	}
	/**
	 * Funkcja wywo�ywana z poziomu kontrolera, do zmiany widoku w panelu rejestracja
	 * @param regPack
	 */
	public void setRegisteredAccount(Profile profile) {
		if(currentPanel instanceof RegisterPanel)
			((RegisterPanel)currentPanel).setRegisteredAccount(profile);
	}	
	/**
	 * Funkcja wywo�ywana z poziomu kontrolera, do zmiany widoku w panelu logowanie
	 */
	public void setNotSuccesfulLogging() {
		if(currentPanel instanceof LoginPanel)
			((LoginPanel)currentPanel).setNotSuccesfulLogging();
	}
	/**
	 * Funkcja wywo�ywana z poziomu kontrolera, do ustawienia zalogowania na konto
	 * @param logPack
	 */
	public void setLoggedAccount(Profile profile) {
		if(currentPanel instanceof LoginPanel) {
			currentProfile = profile;
			setWindowMode(CurrentMode.COMMUNICATION);
		}
	}
	/**
	 * Funkcja wywo�ywana z poziomu kontrolera, do dodania nowej wiadomo�ci.
	 * @param message
	 */
	public void addNewMessage(Message message) {
		if(currentPanel instanceof CommunicatePanel) {
			((CommunicatePanel)currentPanel).addNewMessage(message);
		}
	}
	/**
	 * Funkcja wywo�ywana z poziomu kontrolera do uaktualnienia listy status�w.
	 * @param map
	 */
	public void updateStatusList(Map<Integer, Status> map){
		if(currentPanel instanceof CommunicatePanel) {
			((CommunicatePanel)currentPanel).updateStatusList(map);
		}
	}
	/**
	 * Funkcja wywo�ywana przez kontroler, by po��czy� si� dwustronnie z widokiem.
	 * @param con
	 */
	public void setControler(ClientControler con) {
		controler = con;
	}
	/**
	 * Funkcja zwraca kontroler, z kt�rym po��czony jest window. Funkcja potrzebna poszczeg�lnym panelom.
	 * @return
	 */
	public ClientControler getControler() {
		return controler;
	}	
	/* Stop controlers functions =============================================== */
	
	/* Start view functions ==================================================== */	
	/**
	 * Funkcja wywo�ywana przez modu� komunikacyjny informujca o pro�bie wylogowania konta.
	 * Do kontrolera wysy�ana jest informacja o wylogowanie konta, a w oknie zmieniany jest modu�.
	 */
	public synchronized void unlogProfile() {
		if(currentPanel instanceof CommunicatePanel) {
			try {
				controler.putCommand(new Executable() {
					@Override
					public void execute() {
						controler.windowUnLogin();
					}
				});
			} catch (InterruptedException e1) {/* Do nothing */}
			setWindowMode(CurrentMode.LOGIN);
			DebugPrint.print("wylogowano");
		}
	}
	/**
	 * Gdy w�tek wysy�aj�cy zapytania o statusy u�ytkownik�w to poprzez kontroler �ci�gana jest ta lista kontakt�w.
	 * @return
	 */
	public ArrayList<Profile> getAllContacts() {
		if(currentPanel instanceof CommunicatePanel)
			return ((CommunicatePanel)currentPanel).getAllContacts();
		else 
			return null;
	}
	/* Stop view functions ===================================================== */
	
	/* Start WindowListener functions ========================================== */
	/*
	 * (non-Javadoc)
	 * Funkcje WindowListenera, kt�ry reaguje gdy kto� chce zamkn�� okno.
	 * Wywo�uje funkcje bezpo�rednio w swoim w�tku, poniewa� aplikacja musi zosta� zawieszona do momentu bezpiecznego 
	 * zamkni�cia po��cze�.
	 */
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		controler.closeAll(0);
		DebugPrint.print("zamknieto");
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	/* Stop WindowListener functions =========================================== */

}
