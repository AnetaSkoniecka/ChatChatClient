import ClientView.*;

import ClientControler.*;
import Model.*;

/**
 * Klasa Client tworzy okno g³owne, model i kontroler. 
 * Ustawia port i hostname na ktorym aplikacja bêdzie dzia³aæ. 
 * Startowany jest w¹tek g³ownego kontrolera, który zarz¹dza ca³ym systemem.
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
