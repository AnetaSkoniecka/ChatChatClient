package ClientView;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import Model.DebugPrint;
import Model.Profile;

/**
 * Klasa zarz�dzaj�ca rozmowami mi�dzy klientami. Dziedziczy po JTabbedPane by ka�d� rozmowe przechowywa� w oddzielnej zak�adce.
 * Ka�da strona sk�ada si� z klasy wewn�trzej ChatPanel, kt�ra zawiera w sobie pole tekstowe ChatTextArea.
 * Zak�adki sk�adaj� si� z obiekt�w klasy wewn�trznej TabPane, kt�ra zawiera nr z ktorym rozmawiamy i przycisk do zamkni�cia czatu.
 * @author necia
 *
 */
@SuppressWarnings("serial")
public class ChatTabbedPane extends JTabbedPane {
	public ChatTabbedPane() {
		super();
	}
	public ChatTabbedPane(int arg0) {
		super(arg0);
	}
	public ChatTabbedPane(int arg0, int arg1) {
		super(arg0, arg1);
	}
	/**
	 * Dodaje now� karte czatu. Przypisuje do tego spersonalizowan� zak�adke. Ustawia nowododan� karte jako aktywn�.
	 * Najpierw sprawdza czy ju� dana karta nie istnieje. Jak istnieje to nie tworzy nowej.
	 * @param receiver
	 */
	public ChatPanel addChat(Profile profile) {
		for(int index = 0 ; index < this.getTabCount() ; index++){
			if( ((ChatPanel)getComponentAt(index)).getProfile().getId().equals(profile.getId()) ){
				return (ChatPanel)getComponentAt(index);
			}
		}
		ChatPanel panel = new ChatPanel(profile);
		this.addTab(profile.getId().toString(), panel);
		this.setTabComponentAt(this.getTabCount()-1, new TabPane(profile.getId().toString(), this));
		this.setSelectedIndex(this.getTabCount()-1);
		return panel;
	}
	/**
	 * Dodaje przekazan� wiadomo�� do aktualnie wybranej karty. Funkcja wywo�ywana przy wpisaniu przez u�ytkownika tekstu 
	 * i klikni�cia enter.
	 * @param text
	 */
	public void addMessage(Profile sender, String message) {
		((ChatPanel)getSelectedComponent()).addMessage(sender, message);
	}
	/**
	 * Funkcja wywo�ywana przy otrzymaniu od serwera wiadomo�ci. Najpierw s� przeszukiwane obecnie istniej�ce karty
	 * czy w�r�d nich istnieje ju� rozmowa z senderem. Je�li nie, to tworzona jest nowa karta.
	 * @param sender
	 * @param receiver
	 * @param message
	 */
	public void addMessage(Profile sender, Profile receiver, String message) {
		ChatPanel panel = null;
		for(int index = 0 ; index < this.getTabCount() ; index++){
			if( ((ChatPanel)getComponentAt(index)).getProfile().getId().equals(sender.getId()) ){
				panel = (ChatPanel)getComponentAt(index);
				break;
			}
		}
		if(panel == null) {
			panel = this.addChat(sender);
		}
		DebugPrint.print("addmessage chattabbedpane "+sender.getId().toString(),0);
		panel.addMessage(sender, receiver, message);
	}
	/**
	 * Zwraca profil z kt�rym jest po��czony obecnie wy�wietlany czat.
	 * @return
	 */
	public Profile getProfile(){
		return ((ChatPanel)getSelectedComponent()).getProfile();
	}
	
	/* Start domestic class ==================================================== */
	/**
	 * Pole tekstowe, w kt�rym wy�wietlana jest konwersacja mi�dzy klientami.
	 * @author necia
	 *
	 */
	private class ChatTextArea extends JTextArea {
		private ChatTextArea() {
			super();
			setFont(new Font("Arial", Font.PLAIN, 10));
		}
		/**
		 * Funkcja dodaje do pola tekstowego odpowiednio sformatowany tekst, w kt�rym zawarci s� rozm�wcy i przesy�ana wiadomo��.
		 * @param sender
		 * @param receiver
		 * @param message
		 */
		private void addMessage(Profile sender, Profile receiver, String message) {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("\nFrom: ");
			strBuilder.append(sender.getUserName());
			strBuilder.append("(");
			strBuilder.append(sender.getId().toString());
			strBuilder.append(") To: ");
			strBuilder.append(receiver.getUserName());
			strBuilder.append("(");
			strBuilder.append(receiver.getId().toString());
			strBuilder.append(")\n");
			strBuilder.append(message);
			this.append(strBuilder.toString());
		}
		/**
		 * Funkcja czyszcz�ca pole tekstowe.
		 */
		private void clearChat() {
			this.setText("");
		}
	}
	/**
	 * Panel dodawany do chatPanelu(kt�ry jest obiektem typu JTabbedPane).
	 * Panel ChatPanel b�dzie jedn� z wielu wy�wietlanych tam kart.
	 * Posiada w sobie pole do wy�wietlania wiadomo�ci i obs�uguje to pole.
	 * @author necia
	 *
	 */
	private class ChatPanel extends JPanel {
		private final ChatTextArea chatTextArea;
		private final Profile profile;
		private ChatPanel(Profile profile) {
			super();
			DebugPrint.print("new chatpanel, profileid:"+profile.getId().toString(),0);
			this.setLayout(new GridLayout());
			this.profile = profile;
			chatTextArea = new ChatTextArea();
			chatTextArea.setWrapStyleWord(true);
			chatTextArea.setLineWrap(true);
			JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
			chatTextArea.setEditable(false);
			this.add(chatScrollPane);
		}
		/**
		 * Zwraca profil ktory jest przypisany do panelu. Pomaga w identyfikacji do ktorego panelu doda� przychodz�c� wiadomo��
		 * @return
		 */
		private Profile getProfile() {
			return profile;
		}
		/**
		 * Dodaje do okna tekstowego wiadomo��, okre�lony jest nadawca i odbiorca wiadomo�ci.
		 * @param sender
		 * @param receiver
		 * @param message
		 */
		private void addMessage(Profile sender, Profile receiver, String message) {
			chatTextArea.addMessage(sender, receiver, message);
		}
		/**
		 * Dodaje wiadomo�� do okna gdzie nadawc� jest u�ytkownik klienta.
		 * @param sender
		 * @param message
		 */
		private void addMessage(Profile sender, String message) {
			chatTextArea.addMessage(sender, profile, message);
		}
	}
	/**
	 * TabPane definiuje wygl�d zak�adki w JTabbedPane. Posiada nazwe (nr z kt�rym rozmawiamy) oraz przycisk
	 * do zamkni�cia okna.
	 * @author necia
	 *
	 */
	private class TabPane extends JPanel {
		private final JLabel title;
		private final JButton closeButton;
		private TabPane(String title, JTabbedPane myPane) {
			this.title = new JLabel(title);
			closeButton = new JButton("x");
			closeButton.setName(title);
			closeButton.setContentAreaFilled(false);
			closeButton.setFocusable(false);
			closeButton.setBorder(BorderFactory.createEtchedBorder());
			closeButton.setBorderPainted(false);
			closeButton.setRolloverEnabled(true);
			this.setOpaque(false);
			this.title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
			this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.add(this.title);
			this.add(closeButton);

			class CloseActionListener implements ActionListener {
				private final JTabbedPane myPane;
				private CloseActionListener(JTabbedPane myPane) {
					this.myPane = myPane;
				}
				@Override
				public void actionPerformed(ActionEvent e) {
					int index = myPane.indexOfTab( ((JButton)e.getSource()).getName() );
					if(index >= 0)
						myPane.remove(index);
				}
			}
			closeButton.addActionListener(new CloseActionListener(myPane));
		}
	}
	/* Stop domestic class ===================================================== */

}
