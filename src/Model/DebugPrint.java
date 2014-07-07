package Model;

public class DebugPrint {
	private static final Boolean show = Boolean.FALSE;
	private static final Boolean show1 = Boolean.TRUE;
	public static void print (String str) {
		try{
			if(show)
				System.out.println(str);
		} catch(Exception e) {
			DebugPrint.print("w princie blad");
		}
	}
	public static void print (String str, int i) {
		try{
			if(show1)
				System.out.println(str);
		}
		catch(Exception e){}
	}

}
