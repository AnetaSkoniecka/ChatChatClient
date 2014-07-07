package ClientControler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

import CommunicatePackages.InfoPackage;
import CommunicatePackages.LogingPackage;
import CommunicatePackages.MessagePackage;
import CommunicatePackages.MyPackage;
import CommunicatePackages.MyPackage.TypePackage;
import CommunicatePackages.RegisterPackage;
import CommunicatePackages.StatusListPackage;
import CommunicatePackages.StatusPackage;
import Exception.CloseException;
import Model.DebugPrint;

/**
 * Ma za zadanie wysy³aæ pakiety, które trafiaj¹ do packageQueue.
 * @author necia
 *
 */
public class ClientOutputProtocolThread extends Thread {
	
	/* Start objects =========================================================== */
	private final BlockingQueue<MyPackage> packageQueue;
	private final ObjectOutputStream output;
	private final ClientControler mainControler;
	/* End objects ============================================================= */
	
	/* Start constructors functions ============================================ */
	public ClientOutputProtocolThread(ObjectOutputStream output, BlockingQueue packageQueue, ClientControler controler) {
		super();
		this.packageQueue = packageQueue;
		this.output = output;
		this.mainControler = controler;
	}
	/* Stop constructors functions ============================================= */
	
	/* Start thread executing ================================================== */
	public void run() {
		try {
			while(true) {
				DebugPrint.print("czekam na pakiet do wyslania");
				MyPackage pack = (MyPackage) packageQueue.take();
				DebugPrint.print("dostalem pakiet do wyslania!!!!");
				//DebugPrint.print("nazwa: "+pack.getSenderProfile().getUserName());
				if (pack.getTypePackage() == TypePackage.REGISTER) {DebugPrint.print("4");
					RegisterPackage regPack = (RegisterPackage) pack;
					output.writeObject(regPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet rejestracji");
				}
				else if (pack.getTypePackage() == TypePackage.LOGING) {DebugPrint.print("3");
					LogingPackage logPack = (LogingPackage) pack;
					output.writeObject(logPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet logowania");
				}
				else if (pack.getTypePackage() == TypePackage.MESSAGE) {DebugPrint.print("2");
					MessagePackage messPack = (MessagePackage) pack;
					output.writeObject(messPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet wiadomosci");
				}
				else if (pack.getTypePackage() == TypePackage.STATUS) {DebugPrint.print("5");
					StatusPackage statPack = (StatusPackage) pack;
					output.writeObject(statPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet ustawienia statusu");
				}
				else if (pack.getTypePackage() == TypePackage.TAKEMESSAGES) {DebugPrint.print("1");
					LogingPackage logPack = (LogingPackage) pack;
					output.writeObject(logPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet zapytania o wiadomosci");
				}
				else if (pack.getTypePackage() == TypePackage.STATUSLIST) {DebugPrint.print("6");
					StatusListPackage logPack = (StatusListPackage) pack;
					output.writeObject(logPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet zapytania o statusy");
				}
				
				/**
				 * Przy otrzymaniu pakietu zamykanie. Wysy³a go do serwera, po czym zamyka swój stream i w¹tek koñczy dzia³anie.
				 */
				else if( pack.getTypePackage() == TypePackage.CLOSING) {
					InfoPackage infoPack = (InfoPackage) pack;
					output.writeObject(infoPack);
					output.flush();
					DebugPrint.print("Wyslano pakiet zamkniecia aplikacji");
					throw new CloseException();
				}
				else {
					DebugPrint.print("Bl¹d w output");
					throw new CloseException();
				}
			}
		} catch (java.net.SocketException e3) {
			mainControler.closeAll(-2);
		} catch (InterruptedException e0) {
			e0.printStackTrace();
			DebugPrint.print("output protocol interputter exception");
		} catch (IOException e3) {
		} catch (NullPointerException e1) {
			e1.printStackTrace();
			DebugPrint.print("dostarczono profil ktory juz nie istnieje "+ e1.getMessage());
		} catch (CloseException e2) {
			try {
				output.close();
				this.interrupt();
			} catch (IOException e) {}
		} 
	}
	/* Stop thread executing =================================================== */

	/* Start others functions ================================================== */
	/**
	 * Daje mozliwoœæ kontrolerowi dodanie do kolejki pakietu
	 * @param pack
	 */
	public void put(MyPackage pack) {
		try {
			packageQueue.put(pack);
		} catch (InterruptedException e) {/* do nothing */}
	}
	/* Stop others functions =================================================== */
}
