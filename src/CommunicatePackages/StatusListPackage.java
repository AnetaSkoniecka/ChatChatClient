package CommunicatePackages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.Profile;
import Model.Status;

public class StatusListPackage extends MyPackage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8373391834177727755L;
	
	private final Map<Integer, Status> mapStatus;
	public StatusListPackage(Profile sender) {
		super(MyPackage.TypePackage.STATUSLIST, sender);
		mapStatus = new HashMap<Integer, Status>();
	}
	public StatusListPackage(Profile sender, ArrayList<Profile> list) {
		super(MyPackage.TypePackage.STATUSLIST, sender);
		mapStatus = new HashMap<Integer, Status>();
		if(list != null)
			for (Profile profile : list) {
				mapStatus.put(profile.getId(), new Status(3));
			}
	}
	public StatusListPackage(Profile sender, Map<Integer,Status> map) {
		super(MyPackage.TypePackage.STATUSLIST, sender);
		mapStatus = map;
	}
	public void put(Integer id, Status status) {
		mapStatus.put(id, status);
	}
	public Map<Integer, Status> getMap() {
		return mapStatus;
	}


}
