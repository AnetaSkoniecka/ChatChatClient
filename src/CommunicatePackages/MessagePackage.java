package CommunicatePackages;

import java.util.Date;

import Model.Message;
import Model.Profile;

public class MessagePackage extends MyPackage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4647895912580880507L;
	private final Message message;
	private final Date date;
	private final Profile receiver;
	public MessagePackage(Profile sender, Profile receiver, Message message, Date date) {
		super(MyPackage.TypePackage.MESSAGE, sender);
		this.message = message;
		this.date = date;
		this.receiver = receiver;
	}
	public Profile getReceiver() {
		return receiver;
	}
	public Integer getReceiverId() {
		return receiver.getId();
	}
	public Message getMessage() {
		return message;
	}
	public Date getDate() {
		return date;
	}


}
