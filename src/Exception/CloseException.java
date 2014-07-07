package Exception;

/**
 * Wyj¹tek ten jest rzucany w przypadku wyst¹pienia sytuacji w której trzeba zamkn¹æ aplikacje i trzeba o tym powiadomiæ w¹tki/obiekty.
 * @author necia
 *
 */
public class CloseException extends Exception {

	public CloseException() {
		// TODO Auto-generated constructor stub
	}

	public CloseException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CloseException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CloseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public CloseException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
