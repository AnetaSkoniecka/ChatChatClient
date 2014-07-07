package Model;

import java.io.Serializable;

/**
 * Status (nie mylic ze Statement) informuje o statusie jaki u¿ytkownik chce by inni o nim wiedzeili.
 * Domyœlnie status jest niezarejestrowany, co daje innym u¿ytkownikom informacje ¿e takie konta nie ma.
 * @author necia
 *
 */
public class Status implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7977771626314412292L;
	public enum Stat {
		INACCESSIBLE, ACCESSIBLE, AFK, UNREGISTERED ;
	}
	
	private Stat status;
	public Status() {
		status = Stat.UNREGISTERED;
	}
	public Status(int i) {
		switch(i) {
		case 1: 
			status = Stat.ACCESSIBLE;
			break;
		case 2: 
			status = Stat.AFK;
			break;
		case 3: 
			status = Stat.UNREGISTERED;
			break;
		case 0:
		default:
			status = Stat.INACCESSIBLE;
		}
	}
	public void setStatus(int i) {
		switch(i) {
		case 1: 
			status = Stat.ACCESSIBLE;
			break;
		case 2: 
			status = Stat.AFK;
			break;
		case 3: 
			status = Stat.UNREGISTERED;
			break;
		case 0:
		default:
			status = Stat.INACCESSIBLE;
		}
	}
	public void setStatus(Status status) {
		this.status = status.status;
	}
	public Status getStatus() {
		return this;
	}
	public int getStatus(int i) {
		switch(status) {
		case ACCESSIBLE: 
			return 1;
		case AFK: 
			return 2;
		case UNREGISTERED: 
			return 3;
		case INACCESSIBLE:
		default:
			return 0;
		}
	}
	public String toString(){
		return status.name();
	}
}
