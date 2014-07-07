package ClientControler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

import ClientView.MyWindow;
import Commands.Executable;
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
import Model.Message;
import Model.Model;
import Model.Profile;
import Model.Statement;
import Model.Status;
/**
 * Tworzy polaczenie z serverem, tworzy socket i na jego podstawie tworzy watki poboczne, ktore obsluguja wejscie i wyjscie. 
 * Przyjmuje zadania od widoku.
 * Dzia�a na BlockingQueue(packageQueue), jako producent dodaje do kolejki pakiet�w, a jako konsument 
 * z kolejki komend odbiera zadania odebrane od widoku.
 * @author necia
 *
 */
public class ClientControler extends Thread {
	
	/* Start objects =========================================================== */
	private final int port;
	private final String hostname;
	private Socket socket;
	private final MyWindow window;
	private final Model model;
	private Profile currentProfile;
	private String currentPassword;
	private final BlockingQueue<MyPackage> packageQueue;
	private final BlockingQueue<Executable> commandQueue;
	private ClientInputProtocolThreads inputThread;
	private ClientOutputProtocolThread outputThread;
	private final Semaphore accountSemaphore;
	/* End objects ============================================================= */
	
	/* Start constructors functions ============================================ */
	/**
	 * Tworzy po��czenie z modelem i oknem g��wnym. Tworzy po��czenie z serverem - gdy to si� nie powiedzie, wy��cza aplikacje.
	 * @param port
	 * @param hostname
	 * @param model
	 * @param window
	 */
	public ClientControler(Model model, MyWindow window) {
		Object[] connection = new Object[] {new Integer(0), new String("")};
		window.SetConnection(connection);
		this.port = (Integer)connection[0];
		this.hostname = (String)connection[1];
		this.packageQueue = new LinkedBlockingQueue<MyPackage>();
		this.commandQueue = new LinkedBlockingQueue<Executable>();
		this.model = model;
		this.window = window;
		this.window.setControler(this);
		this.accountSemaphore = new Semaphore(1, true);
		createStreams();
	}
	/**
	 * Tworzony jest socket i nawi�zywane jest po��czenie.
	 * Tworzone s� w�tki poboczne, kt�re b�d� wysy�a� i odbiera� pakiety, na jednym sockecie. 
	 */
	private void createStreams() {
		try {
			socket = new Socket(this.hostname, this.port);DebugPrint.print("socket dziala");
            DebugPrint.print("Connection on port " + port); 
            
        	ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
        	outstream.flush();
			outputThread = new ClientOutputProtocolThread(outstream, packageQueue,this);DebugPrint.print("outputTh");
			
			ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
			inputThread = new ClientInputProtocolThreads(instream, this) ;DebugPrint.print("inputTh");
		}
		catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			closeAll(-1);
		}
	}
	/* Stop constructors functions ============================================= */
	
	/* Start thread executing ================================================== */
	/** 
	 * Uruchomienie w�tk�w czytaj�cych/wysy�aj�cych z socketa i przetwarzaj�cych pakiety.
	 * Odbieranie z kolejki komend i wykonywanie ich.
	 */
	public void run() {
		inputThread.start();DebugPrint.print("inputTh");
		outputThread.start();DebugPrint.print("outputTh");
        while(true) {
        	try {
				Executable executable = (Executable)commandQueue.take();
				executable.execute();
			} catch (InterruptedException e) {
				try {
					DebugPrint.print("client controler interputter exception");
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				break;
			}
        }
    }
	/* Stop thread executing =================================================== */
	
	/* Start window/controler functions ======================================== */
	/**
	 * Operacja dodania do kolejki komendy. Komenda dostarczana dla pobocznych kontroler�w i dla widoku (ogolnie od innych w�tk�w)
	 * @param command
	 * @throws InterruptedException
	 */
	public void putCommand(Executable command) throws InterruptedException {
		commandQueue.put(command);
	}
	/**
	 * Gdy jestesmy zalogowani to z 5 sekundow� przerw� wysylane jest do serwera zapytanie czy nie 
	 * ma nowych widomosci dla danego konta.
	 * Wymagane postawienie semafora by nie wysy�a� zapytania kiedy nie jeste�my ju� zalogowani.
	 * Kiedy zdarzy si�, �e nie mamy zalogowanego usera a wci�� w kolejce by�y jakie� zapytania do serwera w sprawie tego konta,
	 * to nale�y zako�czy� w�tek wysy�ania zapyta� za pomoc� rzucanego wyj�tku.
	 * @throws CloseException 
	 */
	public void sendMessageAsk() throws CloseException {
		try {
			accountSemaphore.acquire();
		} catch (InterruptedException e1) {DebugPrint.print("sendMessageask interrupted");}
		if(currentProfile != null) {
			outputThread.put( new LogingPackage(currentProfile, Statement.LOGGED, currentPassword, 
					TypePackage.TAKEMESSAGES) );
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				DebugPrint.print("Sleep interrupted");
			}	
			DebugPrint.print("dodano do kolejki zapytanie o wiadomosci");
			accountSemaphore.release();
		}
		else{
			accountSemaphore.release();
			throw new CloseException();
		}
	}
	/** 
	 * Analogicznie do sendMessageAsk. Co 5 sek wysy�ane s� pakiety z pro�b� o liste status�w z naszej listy kontakt�w.
	 * @throws CloseException 
	 */
	public void sendStatusAsk() throws CloseException {
		try {
			accountSemaphore.acquire();
			ArrayList<Profile> list = window.getAllContacts();
			if(currentProfile != null) {
				outputThread.put( new StatusListPackage(currentProfile, list) );
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					DebugPrint.print("Sleep interrupted");
				}	
				DebugPrint.print("dodano do kolejki zapytanie o liste status�w");
				accountSemaphore.release();
			}
			else{
				accountSemaphore.release();
				throw new CloseException();
			}
		} catch (InterruptedException e1) {
			DebugPrint.print("sendStatusAsk interrupted");
		}
	}
	/** Po wywo�aniu przez widok, tworzony zostaje pakiet rejestruj�cy i dodany do kolejki pakiet�w, kt�ry zostanie 
	 * wys�any przez odpowiedni w�tek
	 * @param userName
	 * @param password
	 * @return
	 */
	public Boolean windowRegister(String userName, String password) {
		outputThread.put(new RegisterPackage(new Profile(0000, userName), password));
		return true;	
	}
	/** Po wywo�aniu przez widok, tworzony zostaje pakiet logujacy i dodany do kolejki pakiet�w, kt�ry zostanie 
	 * wys�any przez odpowiedni w�tek
	 * @param userName
	 * @param password
	 * @return
	 */
	public void windowLogin(Integer id, String password) {
		outputThread.put(new LogingPackage( new Profile(id, "nieznany"), Statement.LOGGED, password));
	}
	/**
	 * Po wywo�aniu przez widok, tworzony jets pakiet wylogowywuj�cy i dodany do kolejki pakiet�w, kt�ry zostanie wys�any
	 * przez odpowiedni w�tek.
	 * Do tego zatrzymywane s� w�tki wysy�ania zapyta� o wiadomo�ci i statusy.
	 */
	public void windowUnLogin() {
		try {
			accountSemaphore.acquire();
			DebugPrint.print("windowunlogin proba wylogowania");
			outputThread.put(new LogingPackage( currentProfile, Statement.UNLOGGED, currentPassword));
			if(inputThread.getTakingMessagesThread() != null){
				inputThread.getTakingMessagesThread().interrupt();
				inputThread.setTakingMessagesThread(null);
			}
			
			currentProfile = null;
		} catch (InterruptedException e) {DebugPrint.print("windowunlogin interput");}
		accountSemaphore.release();
	}
	/**
	 * Gdy zalogowany user zmienia w widoku status to jest ta informacja �adowana w pakiet i wysy�ana do serwera by serwer uaktualni�
	 * baze.
	 * @param status
	 */
	public void windowChangeStatus(String status) {
		if(currentProfile == null)
			return;
		if( status.equals("dostepny") ) {
			outputThread.put(new StatusPackage(currentProfile, currentPassword, new Status(1)));
		} else if( status.equals("niedostepny") ) {
			outputThread.put(new StatusPackage(currentProfile, currentPassword, new Status(0)));
		} else if( status.equals("zaraz wracam") ) {
			outputThread.put(new StatusPackage(currentProfile, currentPassword, new Status(2)));
		} else {
		DebugPrint.print("wyslano status");
		}
	}
	/**
	 * Po wywo�aniu przez widok, tworzony jets pakiet wiadomo�ci i dodany do kolejki pakiet�w, kt�ry zostanie wys�any
	 * przez odpowiedni w�tek.
	 * @param sender
	 * @param receiver
	 * @param text
	 */
	public void windowMessenging(Profile sender, Profile receiver, String text) {
		outputThread.put( new MessagePackage(sender, receiver, new Message(sender, receiver, text), null) );
	}
	/* Stop window/controler functions ========================================= */
	
	/* Start general controler functions ======================================= */
	/**
	 * Wywo�ywane przez w�tek odbieraj�cy pakiety od serwera. Przekazuje wiadomo�� do widoku by ten j� wy�wietli�.
	 * @param message
	 */
	public void addMessage(Message message) {
		window.addNewMessage(message);
	}
	/**
	 * Wywo�ywane przez w�tek odbieraj�cy pakiety. Przekazuje mape powi�zanych nr_id ze statusami i uaktualnia je na li�cie.
	 * @param map
	 */
	public void updateStatusList(Map<Integer, Status> map) {
		window.updateStatusList(map);
	}
	/**
	 * Funkcja zwracaj�ca obecnie ustawione has�o dla obecnego konta
	 * @return
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}
	/**
	 * Funkcja zwracaj�ca obecnie zalogowane konto
	 * @return
	 */
	public Profile getCurrentProfile() {
		return currentProfile;
	}
	/**
	 * Ustawia nowe konto
	 * @param profile
	 * @param password
	 */
	public void setCurrentProfile(Profile profile, String password) {
		currentProfile = profile;
		currentPassword = password;
	}
	/**
	 * Przydaje sie dodatkowym w�tkom kontroluj�cym by uzyska� dost�p do widoku.
	 * @return
	 */
	public MyWindow getMainWindow() {
		return window;
	}
	/* Stop general controler functions ======================================== */
	
	/* Closing application functions =========================================== */
	/**
	 * Funkcja wysy�a do serwera informacje o zamkni�ciu po czym zamyka po��czenie.
	 * 1. Wysy�a na output pakiet o zamkni�ciu, kt�ry jest wysy�any do serwera. Potem w�tek zamyka strumien out
	 * i ko�czy dzia�anie.
	 * 2. Wysy�a na input pakiet o zamkni�ciu, kt�ry zamyka tamte strumienie i w�tki.
	 * 3. Gdy poboczne w�tki ju� nie s� Alive to mo�na zamkn�� socket i aplikacje.
	 */
	private void closeConnection() {
		DebugPrint.print("closeSocket");
		outputThread.put(new InfoPackage(TypePackage.CLOSING, null));
		while(outputThread.isAlive()) {;}
		DebugPrint.print("output thread interrupted");
		inputThread.put(new InfoPackage(TypePackage.CLOSING, null));
		while(inputThread.isAlive()) {}
		DebugPrint.print("input thread interrupted");
		try {
			socket.close();
		} catch (IOException e) {}
	}
	/**
	 * Generalna funkcja kt�ra musi si� wykona� przy zamykaniu aplikacji.
	 * W przypadku state = -1 oznacza to zamkni�cie natychmiastowe z bl�dem - wywo�ywane przy b��dzie po��czenia z kontrolera.
	 * W przypadku zamykania okna przez u�ytkownika, zostaje ta funkcja wywo�ana z parametrem state = 0,
	 * dane powinny by� zapisane do pliku(narazie nie ma zapisu do plikow),
	 * zalogowane konto powinno zosta� wylogowane,
	 * sokety powinny zosta� zamkni�te.
	 * @param state
	 */
	public void closeAll(int state) {
		if(state == -1){
			window.showMessage("Aplikacja nie mog�a po��czy� si� z serwerem.");
			System.exit(state);
		}
		else if(state == -2) {
			window.showMessage("Wyst�pi� b��d serwera.");
			if(currentProfile != null)
				windowUnLogin();
			System.exit(-1);
		}
		else {
			if(currentProfile != null)
				windowUnLogin();
			closeConnection();
		}
	}
}
