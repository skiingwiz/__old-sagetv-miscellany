package db;

public class DatabaseProcessingException extends Exception {
	private static final long serialVersionUID = -761138714271716923L;

	public DatabaseProcessingException(Throwable arg0) {
		super(arg0);
	}

	public DatabaseProcessingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
