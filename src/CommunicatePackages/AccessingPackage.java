package CommunicatePackages;

import Model.Profile;
import Model.Status;

/**
 * Paczka ostatecznie niewykorzystywana
 * @author necia
 *
 */
public class AccessingPackage extends MyPackage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5579673231084805038L;
	private Status status;
	public AccessingPackage(Profile sender, Status status) {
		super(MyPackage.TypePackage.ACCESSING, sender);
		this.status = status;
	}
	public Status getStatus() {
		return this.status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}


}
