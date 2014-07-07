package ClientView;

import java.awt.Component;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import Model.Profile;

/**
 * Lista kontaktów.
 * Mo¿na dodawaæ, usuwaæ kontakty. Mo¿na dwukrotnie klikn¹æ kontakt by dodaæ rozmowe z nim.
 * Lista kontaktów (ich statusy) jest odœwie¿ana przez kontroler
 * @author necia
 *
 */
public class ContactsList extends JPanel{
	/**
	 * Element listy.
	 * @author necia
	 *
	 */
	private class ContactItem {
		/**
		 * mo¿e przyjmowaæ: ACCESSIBLE, AFK, UNREGISTERED, INACCESSIBLE
		 */
		private String status;
		private final Profile profile;
		
		private ContactItem(Profile profile) {
			this.profile = profile;
			status = "UNREGISTERED";
		}
		private String getUserName() {
			return profile.getUserName();
		}
		private String getId() {
			return profile.getId().toString();
		}
		private String getStatus() {
			return status;
		}
		private Profile getProfile() {
			return profile;
		}
		private void setStatus(String status) {
			this.status = status;
		}
	}
	/**
	 * Renderer do listy. Zwraca label z nazw¹ kontaktu i ikonk¹
	 * @author necia
	 *
	 */
	private class ContactsListRenderer extends JLabel implements ListCellRenderer<ContactItem> {
		private final Map<String, ImageIcon> iconMap;
		
		private ContactsListRenderer() {
			iconMap = new HashMap<String, ImageIcon>();
			iconMap.put("ACCESSIBLE", new ImageIcon("utilities/statusIcon/accessible.png"));
			iconMap.put("AFK", new ImageIcon("utilities/statusIcon/afk.png"));
			iconMap.put("UNREGISTERED", new ImageIcon("utilities/statusIcon/unregistered.png"));
			iconMap.put("INACCESSIBLE", new ImageIcon("utilities/statusIcon/inaccessible.png"));
			setOpaque(true); 
		}
		@Override
		public Component getListCellRendererComponent(
				JList<? extends ContactItem> list, ContactItem item, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText( item.getUserName()+" ("+item.getId()+") " );
			setIcon(iconMap.get(item.getStatus()));
	        if (cellHasFocus) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        } 
			return this;
		}
	}
	
	/**
	 * Listener nas³uchuje na klikniêcia na dany kontakt. Gdy jest to dwuklik to powoduje utworzenie rozmowy z tym kontaktem w
	 * panelu rozmów.
	 * @author necia
	 *
	 */
	private class ListMouseListener implements MouseListener {
		private final ChatTabbedPane pane;
		private final JList<ContactItem> list;
		private ListMouseListener(ChatTabbedPane pane, JList<ContactItem> list) {
			this.pane = pane;
			this.list = list;
		}
		@Override
		public void mouseClicked(MouseEvent evt) {
	        if (evt.getClickCount() == 2) {
	            int index = list.locationToIndex(evt.getPoint());
	            ContactItem item = (ContactItem)((DefaultListModel)list.getModel()).getElementAt(index);
	            pane.addChat(item.getProfile());
	        }	
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	/* Start objects =========================================================== */
	private final JList<ContactItem> contactsList;
	/* Stop objects ============================================================ */
	
	/* Start constructors functions ============================================ */
	public ContactsList(ChatTabbedPane chatPane) {			
		DefaultListModel<ContactItem> listModel = new DefaultListModel<ContactItem>();
		this.setLayout(new GridLayout());
		this.contactsList = new JList<ContactItem>(listModel);
		this.contactsList.setCellRenderer(new ContactsListRenderer());
		JScrollPane scrollPane = new JScrollPane(contactsList);
		this.add(scrollPane);
		contactsList.addMouseListener(new ListMouseListener(chatPane, contactsList));
	}
	/* Stop constructors functions ============================================= */
	
	/* Start controlers functions ============================================== */
	/**
	 * Dodaje do listy nowy kontakt.
	 * @param profile
	 */
	public void addContact(Profile profile) {
		((DefaultListModel)contactsList.getModel()).addElement(new ContactItem(profile));
	}
	/**
	 * Wyszukuje na liœcie dany profil i jeœli istnieje to zmienia mu status
	 * @param profile
	 * @param status
	 */
	public void setStatus(Profile profile, String status) {
		for(int index = 0 ; index < ((DefaultListModel)contactsList.getModel()).getSize() ; index++) {
			ContactItem item = (ContactItem)((DefaultListModel)contactsList.getModel()).getElementAt(index);
			if(item.getProfile().equals(profile)) {
				item.setStatus(status);
				break;
			}
		}
	}
	/**
	 * Wyszukuje na liœcie dany profil i jeœli istnieje to zmienia mu status
	 * @param profile
	 * @param status
	 */
	public void setStatus(Integer id, String status){
		for(int index = 0 ; index < ((DefaultListModel)contactsList.getModel()).getSize() ; index++) {
			ContactItem item = (ContactItem)((DefaultListModel)contactsList.getModel()).getElementAt(index);
			if(item.getProfile().getId().equals(id)) {
				item.setStatus(status);
				contactsList.repaint();
				break;
			}
		}
	}
	/**
	 * Usuwa zaznaczony element na liœcie. Jeœli na liœcie jest zaznaczone wiêcej elementów to usuwa tylko ten pierwszy.
	 * Jeœli nie ma zaznaczone nic to nic siê nie dzieje.
	 */
	public void deleteSelected(){
		int index = contactsList.getSelectedIndex();
		if (index != -1) {
			((DefaultListModel) contactsList.getModel()).remove(index);
		}
	}
	/**
	 * Zwraca liste kontaktów zapisanych w liœcie.
	 * @return
	 */
	public ArrayList<Profile> getAllContacts() {
		ArrayList<Profile> arrayList = new ArrayList<Profile>();
		int size = this.contactsList.getModel().getSize();
		for(int index = 0 ; index < size ; index++) {
			arrayList.add(contactsList.getModel().getElementAt(index).getProfile());
		}
		return arrayList;
	}
}
