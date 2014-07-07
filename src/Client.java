import ClientView.*;

import ClientControler.*;
import Model.*;

/**
 * Klasa Client tworzy okno g�owne, model i kontroler. 
 * Ustawia port i hostname na ktorym aplikacja b�dzie dzia�a�. 
 * Startowany jest w�tek g�ownego kontrolera, kt�ry zarz�dza ca�ym systemem.
 * @author necia
 *
 */
public class Client {
	//private static MyWindow window;
	public final static void main(String[] args) {
		Model model = new Model();
		MyWindow window = new MyWindow(model);
		ClientControler clientControler = new ClientControler(model, window);
		clientControler.start();
	}

}
