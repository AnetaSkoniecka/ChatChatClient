package CommunicatePackages;

import Model.Profile;

/**
 * Paczka przekazuje informacje jedynie za pomoc¹ nazwy swojego typu.
 * @author necia
 *
 */
public class InfoPackage extends MyPackage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1973830504487529435L;

	public InfoPackage(TypePackage typePackage, Profile sender) {
		super(typePackage, sender);
	}

}
