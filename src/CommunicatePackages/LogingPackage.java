package CommunicatePackages;

import Model.*;

public class LogingPackage extends MyPackage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4055022372913902940L;
	private Statement statement;
	private String password;
	public LogingPackage(Profile sender, Statement statement, String password) {
		super(MyPackage.TypePackage.LOGING, sender);
		this.statement = statement;
		this.password = password;
	}
	public LogingPackage(Profile sender, Statement statement, String password, 
			MyPackage.TypePackage type) {
		super(type, sender);
		this.statement = statement;
		this.password = password;
	}
	public Statement getStatement() {
		return statement;
	}
	public String getPassword() {
		return password;
	}

}
