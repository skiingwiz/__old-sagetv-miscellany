package db;

public class DatabaseInitializationException extends Exception {
	private static final long serialVersionUID = 7966820179347828023L;

	public DatabaseInitializationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DatabaseInitializationException(String string) {
		super(string);
	}

}
