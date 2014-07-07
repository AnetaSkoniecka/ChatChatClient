package Model;

import java.io.Serializable;

/**
 * Wiadomoœæ jest obiektem przechowuj¹cym informacje o rozmówcach i przechowuje treœæ wiadomoœci.
 * @author necia
 *
 */
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2631006868391363121L;
	private final Profile senderProfile;
	private final Profile receiverProfile;
	private final String text;
	public Message(final Profile senderProfile, final Profile receiverProfile, final String text) {
		this.senderProfile = senderProfile;
		this.receiverProfile = receiverProfile;
		this.text = text;
	}
	public final Profile getSenderProfile() {
		return senderProfile;
	}
	public final Profile getReceiverProfile() {
		return receiverProfile;
	}
	public final String getText() {
		return text;
	}
}
