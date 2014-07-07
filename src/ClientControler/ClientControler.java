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
 * Dzia³a na BlockingQueue(packageQueue), jako producent dodaje do kolejki pakietów, a jako konsument 
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
	 * Tworzy po³¹czenie z modelem i oknem g³ównym. Tworzy po³¹czenie z serverem - gdy to siê nie powiedzie, wy³¹cza aplikacje.
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
	 * Tworzony jest socket i nawi¹zywane jest po³¹czenie.
	 * Tworzone s¹ w¹tki poboczne, które bêd¹ wysy³aæ i odbieraæ pakiety, na jednym sockecie. 
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
	 * Uruchomienie w¹tków czytaj¹cych/wysy³aj¹cych z socketa i przetwarzaj¹cych pakiety.
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
	 * Operacja dodania do kolejki komendy. Komenda dostarczana dla pobocznych kontrolerów i dla widoku (ogolnie od innych w¹tków)
	 * @param command
	 * @throws InterruptedException
	 */
	public void putCommand(Executable command) throws InterruptedException {
		commandQueue.put(command);
	}
	/**
	 * Gdy jestesmy zalogowani to z 5 sekundow¹ przerw¹ wysylane jest do serwera zapytanie czy nie 
	 * ma nowych widomosci dla danego konta.
	 * Wymagane postawienie semafora by nie wysy³aæ zapytania kiedy nie jesteœmy ju¿ zalogowani.
	 * Kiedy zdarzy siê, ¿e nie mamy zalogowanego usera a wci¹¿ w kolejce by³y jakieœ zapytania do serwera w sprawie tego konta,
	 * to nale¿y zakoñczyæ w¹tek wysy³ania zapytañ za pomoc¹ rzucanego wyj¹tku.
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
	 * Analogicznie do sendMessageAsk. Co 5 sek wysy³ane s¹ pakiety z proœb¹ o liste statusów z naszej listy kontaktów.
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
				DebugPrint.print("dodano do kolejki zapytanie o liste statusów");
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
	/** Po wywo³aniu przez widok, tworzony zostaje pakiet rejestruj¹cy i dodany do kolejki pakietów, który zostanie 
	 * wys³any przez odpowiedni w¹tek
	 * @param userName
	 * @param password
	 * @return
	 */
	public Boolean windowRegister(String userName, String password) {
		outputThread.put(new RegisterPackage(new Profile(0000, userName), password));
		return true;	
	}
	/** Po wywo³aniu przez widok, tworzony zostaje pakiet logujacy i dodany do kolejki pakietów, który zostanie 
	 * wys³any przez odpowiedni w¹tek
	 * @param userName
	 * @param password
	 * @return
	 */
	public void windowLogin(Integer id, String password) {
		outputThread.put(new LogingPackage( new Profile(id, "nieznany"), Statement.LOGGED, password));
	}
	/**
	 * Po wywo³aniu przez widok, tworzony jets pakiet wylogowywuj¹cy i dodany do kolejki pakietów, który zostanie wys³any
	 * przez odpowiedni w¹tek.
	 * Do tego zatrzymywane s¹ w¹tki wysy³ania zapytañ o wiadomoœci i statusy.
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
	 * Gdy zalogowany user zmienia w widoku status to jest ta informacja ³adowana w pakiet i wysy³ana do serwera by serwer uaktualni³
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
	 * Po wywo³aniu przez widok, tworzony jets pakiet wiadomoœci i dodany do kolejki pakietów, który zostanie wys³any
	 * przez odpowiedni w¹tek.
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
	 * Wywo³ywane przez w¹tek odbieraj¹cy pakiety od serwera. Przekazuje wiadomoœæ do widoku by ten j¹ wyœwietli³.
	 * @param message
	 */
	public void addMessage(Message message) {
		window.addNewMessage(message);
	}
	/**
	 * Wywo³ywane przez w¹tek odbieraj¹cy pakiety. Przekazuje mape powi¹zanych nr_id ze statusami i uaktualnia je na liœcie.
	 * @param map
	 */
	public void updateStatusList(Map<Integer, Status> map) {
		window.updateStatusList(map);
	}
	/**
	 * Funkcja zwracaj¹ca obecnie ustawione has³o dla obecnego konta
	 * @return
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}
	/**
	 * Funkcja zwracaj¹ca obecnie zalogowane konto
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
	 * Przydaje sie dodatkowym w¹tkom kontroluj¹cym by uzyskaæ dostêp do widoku.
	 * @return
	 */
	public MyWindow getMainWindow() {
		return window;
	}
	/* Stop general controler functions ======================================== */
	
	/* Closing application functions =========================================== */
	/**
	 * Funkcja wysy³a do serwera informacje o zamkniêciu po czym zamyka po³¹czenie.
	 * 1. Wysy³a na output pakiet o zamkniêciu, który jest wysy³any do serwera. Potem w¹tek zamyka strumien out
	 * i koñczy dzia³anie.
	 * 2. Wysy³a na input pakiet o zamkniêciu, który zamyka tamte strumienie i w¹tki.
	 * 3. Gdy poboczne w¹tki ju¿ nie s¹ Alive to mo¿na zamkn¹æ socket i aplikacje.
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
	 * Generalna funkcja która musi siê wykonaæ przy zamykaniu aplikacji.
	 * W przypadku state = -1 oznacza to zamkniêcie natychmiastowe z blêdem - wywo³ywane przy b³êdzie po³¹czenia z kontrolera.
	 * W przypadku zamykania okna przez u¿ytkownika, zostaje ta funkcja wywo³ana z parametrem state = 0,
	 * dane powinny byæ zapisane do pliku(narazie nie ma zapisu do plikow),
	 * zalogowane konto powinno zostaæ wylogowane,
	 * sokety powinny zostaæ zamkniête.
	 * @param state
	 */
	public void closeAll(int state) {
		if(state == -1){
			window.showMessage("Aplikacja nie mog³a po³¹czyæ siê z serwerem.");
			System.exit(state);
		}
		else if(state == -2) {
			window.showMessage("Wyst¹pi³ b³¹d serwera.");
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
