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
 * Klasa zarz¹dzaj¹ca rozmowami miêdzy klientami. Dziedziczy po JTabbedPane by ka¿d¹ rozmowe przechowywaæ w oddzielnej zak³adce.
 * Ka¿da strona sk³ada siê z klasy wewnêtrzej ChatPanel, która zawiera w sobie pole tekstowe ChatTextArea.
 * Zak³adki sk³adaj¹ siê z obiektów klasy wewnêtrznej TabPane, która zawiera nr z ktorym rozmawiamy i przycisk do zamkniêcia czatu.
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
	 * Dodaje now¹ karte czatu. Przypisuje do tego spersonalizowan¹ zak³adke. Ustawia nowododan¹ karte jako aktywn¹.
	 * Najpierw sprawdza czy ju¿ dana karta nie istnieje. Jak istnieje to nie tworzy nowej.
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
	 * Dodaje przekazan¹ wiadomoœæ do aktualnie wybranej karty. Funkcja wywo³ywana przy wpisaniu przez u¿ytkownika tekstu 
	 * i klikniêcia enter.
	 * @param text
	 */
	public void addMessage(Profile sender, String message) {
		((ChatPanel)getSelectedComponent()).addMessage(sender, message);
	}
	/**
	 * Funkcja wywo³ywana przy otrzymaniu od serwera wiadomoœci. Najpierw s¹ przeszukiwane obecnie istniej¹ce karty
	 * czy wœród nich istnieje ju¿ rozmowa z senderem. Jeœli nie, to tworzona jest nowa karta.
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
	 * Zwraca profil z którym jest po³¹czony obecnie wyœwietlany czat.
	 * @return
	 */
	public Profile getProfile(){
		return ((ChatPanel)getSelectedComponent()).getProfile();
	}
	
	/* Start domestic class ==================================================== */
	/**
	 * Pole tekstowe, w którym wyœwietlana jest konwersacja miêdzy klientami.
	 * @author necia
	 *
	 */
	private class ChatTextArea extends JTextArea {
		private ChatTextArea() {
			super();
			setFont(new Font("Arial", Font.PLAIN, 10));
		}
		/**
		 * Funkcja dodaje do pola tekstowego odpowiednio sformatowany tekst, w którym zawarci s¹ rozmówcy i przesy³ana wiadomoœæ.
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
		 * Funkcja czyszcz¹ca pole tekstowe.
		 */
		private void clearChat() {
			this.setText("");
		}
	}
	/**
	 * Panel dodawany do chatPanelu(który jest obiektem typu JTabbedPane).
	 * Panel ChatPanel bêdzie jedn¹ z wielu wyœwietlanych tam kart.
	 * Posiada w sobie pole do wyœwietlania wiadomoœci i obs³uguje to pole.
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
		 * Zwraca profil ktory jest przypisany do panelu. Pomaga w identyfikacji do ktorego panelu dodaæ przychodz¹c¹ wiadomoœæ
		 * @return
		 */
		private Profile getProfile() {
			return profile;
		}
		/**
		 * Dodaje do okna tekstowego wiadomoœæ, okreœlony jest nadawca i odbiorca wiadomoœci.
		 * @param sender
		 * @param receiver
		 * @param message
		 */
		private void addMessage(Profile sender, Profile receiver, String message) {
			chatTextArea.addMessage(sender, receiver, message);
		}
		/**
		 * Dodaje wiadomoœæ do okna gdzie nadawc¹ jest u¿ytkownik klienta.
		 * @param sender
		 * @param message
		 */
		private void addMessage(Profile sender, String message) {
			chatTextArea.addMessage(sender, profile, message);
		}
	}
	/**
	 * TabPane definiuje wygl¹d zak³adki w JTabbedPane. Posiada nazwe (nr z którym rozmawiamy) oraz przycisk
	 * do zamkniêcia okna.
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
