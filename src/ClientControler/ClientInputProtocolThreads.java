package ClientControler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ClientView.MyWindow;
import Commands.Executable;
import CommunicatePackages.InfoPackage;
import CommunicatePackages.LogingPackage;
import CommunicatePackages.MessagePackage;
import CommunicatePackages.MyPackage;
import CommunicatePackages.RegisterPackage;
import CommunicatePackages.MyPackage.TypePackage;
import CommunicatePackages.StatusListPackage;
import Exception.CloseException;
import Model.DebugPrint;
import Model.Statement;

/**
 * Ma za zadanie odbiera� pakiety od servera i zgodnie z protoko�em je obslugiwa�.
 * @author necia
 *
 */
public class ClientInputProtocolThreads {
	
	/* Start objects =========================================================== */
	private final ObjectInputStream input;
	private final BlockingQueue<MyPackage> packageQueue;
	private final ClientControler mainControler;
	private Thread threadStream;
	private Thread threadPackage;
	private Thread takingMessagesThread = null;
	/* End objects ============================================================= */
	
	/* Start constructors functions ============================================ */
	public ClientInputProtocolThreads(ObjectInputStream input, ClientControler mainControler) {
		this.mainControler = mainControler;
		DebugPrint.print("input");
		this.input = input;
		this.packageQueue = new LinkedBlockingQueue<MyPackage>();
	}
	/* Stop constructors functions ============================================= */
	
	/* Start threads executing ================================================= */
	/** Jeden w�tek przyjmuje pakiety ze strumienia od serwera i dodaje je do kolejki pakiet�w. 
	 *  Drugi w�tek wyci�ga pakiety z kolejki i je obs�uguje. 
	 *  Je�li drugi w�tek odbierze pakiet zalogowania to uruchamia dla zalogowanego konta w�tek wysy�aj�cy zapytania do serwera, 
	 *  kt�re �ci�ga wiadomo�ci i statusy innych u�ytkownik�w
	 */
	public void start() {
		/** 
		 * Odbiera pakiety ze strumienia i �aduje je do kolejki.
		 */
		threadStream = new Thread( new Runnable() {
			public void run() {
				try {
					while(true) {
						DebugPrint.print("czekam na pakiet ze strumienia");
						MyPackage pack;
						pack = (MyPackage) input.readObject();
						DebugPrint.print(pack.getSenderProfile().getUserName());
						packageQueue.put(pack);
						DebugPrint.print("dodano pakiet do kolejki");
					}
				} catch (ClassNotFoundException e1) {/* do nothing*/
				} catch (IOException e2) {
				} catch (InterruptedException e3) {
					DebugPrint.print("ClientInputProtocol obsluga strumienia interrupted");
					try {
						input.close();
					} catch (IOException e1) {}
				}
			}
		});
		threadStream.start();
		
		/** 
		 * Obsluguje pakiety ktore wyciaga z kolejki.
		 */
		threadPackage = new Thread( new Runnable() {
			public void run() {
				try {
					while(true) {
						DebugPrint.print("czekam na pakiet z kolejki");
						MyPackage pack = (MyPackage)packageQueue.take();
						DebugPrint.print("wyciagnieto pakiet z kolejki");
						/* REGISTER */
						if (pack.getTypePackage() == TypePackage.REGISTER) {
							DebugPrint.print("odebrano pakiet rejestracji");
							RegisterPackage regPack = (RegisterPackage)pack;
							mainControler.getMainWindow().setRegisteredAccount(regPack.getSenderProfile());
						}
						/* LOGIN */
						else if (pack.getTypePackage() == TypePackage.LOGING) {
							DebugPrint.print("odebrano pakiet logowania");
							LogingPackage logPack = (LogingPackage)pack;
							DebugPrint.print("Pakiet login przyslal: "+logPack.getSenderProfile().getUserName());
							if(logPack.getStatement() == Statement.LOGGED) {
								DebugPrint.print("nastapi zalogowanie");
								mainControler.setCurrentProfile(logPack.getSenderProfile(), logPack.getPassword());
								mainControler.getMainWindow().setLoggedAccount(logPack.getSenderProfile());
								mainControler.windowChangeStatus("dostepny");
								takingMessagesThread= new Thread( new Runnable() {					
									@Override
									public void run() {
										while(true){
											try {
												mainControler.sendMessageAsk();
												mainControler.sendStatusAsk();
											} catch (CloseException e) {
												DebugPrint.print("zatrzymano messengask/statusask");
												break;
											}
										}	
									}
								});
								takingMessagesThread.start();
							}
							else {
								mainControler.getMainWindow().setNotSuccesfulLogging();
								mainControler.setCurrentProfile(null, null);
							}		
						}
						/* MESSAGE */
						else if(pack.getTypePackage() == TypePackage.MESSAGE){
							MessagePackage messPack = (MessagePackage) pack;
							mainControler.addMessage(messPack.getMessage());
						}
						/* STATUS LIST */
						else if(pack.getTypePackage() == TypePackage.STATUSLIST) {
							StatusListPackage statListPack = (StatusListPackage) pack;
							mainControler.updateStatusList(statListPack.getMap());
						}
						else if(pack.getTypePackage() == TypePackage.CLOSING) {
							throw new CloseException();
						}
						/* ERROR */
 						else {
							System.out.println("Bl�d w input");
							// przygotuj wyj�tek do rzucenia - wyj�tek kt�ry og�osi niezgodno�� z protoko�em
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					DebugPrint.print("input protocol interputter exception");
				} catch (CloseException e1) {
					interrupt();
				}
			}
		});
		threadPackage.start();
	}
	/**
	 * Zamyka w�tki i strumienie.
	 */
	private void interrupt() {
		if(this.takingMessagesThread != null)
			this.takingMessagesThread.interrupt();
		this.threadStream.interrupt();if(this.threadStream.isInterrupted()) DebugPrint.print("thread stream inetrputted");
		this.threadPackage.interrupt();if(this.threadPackage.isInterrupted()) DebugPrint.print("interputed thread package");
	}
	/* Stop threads executing ================================================== */
	
	/* Start others functions ================================================== */
	/**
	 * Daje mozliwo�� kontrolerowi dodanie do kolejki pakietu
	 * @param pack
	 */
	public void put(MyPackage pack) {
		try {
			packageQueue.put(pack);
		} catch (InterruptedException e) {/* do nothing */}
	}
	/**
	 * Zwraca informacje czy w�tki w tej klasie s� juz zatrzymane.
	 * @return
	 */
	public synchronized Boolean isAlive() {
		return (threadPackage.isAlive() || threadStream.isAlive() || 
				(takingMessagesThread != null && takingMessagesThread.isAlive()) );
	}
	/**
	 * Zwraca w�tek do g�ownego kontrolera.
	 * @return
	 */
	public Thread getTakingMessagesThread() {
		return takingMessagesThread;
	}
	/**
	 * Ustawienie w�tku przez kontroler.
	 * @param th
	 */
	public void setTakingMessagesThread(Thread th){
		takingMessagesThread = th;
	}
	/* Stop others functions =================================================== */

}
