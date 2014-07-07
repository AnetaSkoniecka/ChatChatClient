package CommunicatePackages;

import Model.Profile;
import Model.Status;

public class StatusPackage extends MyPackage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8270858092513620064L;
	private final String password;
	private final Status status;
	public StatusPackage(Profile sender, String password, Status status) {
		super(TypePackage.STATUS, sender);
		this.password = password;
		this.status = status;
	}
	public String getPassword() {
		return password;
	}
	public Status getStatus() {
		return status;
	}

}
