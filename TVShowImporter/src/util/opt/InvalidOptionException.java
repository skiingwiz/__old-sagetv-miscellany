package util.opt;

public class InvalidOptionException extends Exception {
	private static final long serialVersionUID = 3944314261612382387L;

	public InvalidOptionException() {
	}

	public InvalidOptionException(String arg0) {
		super(arg0);
	}

	public InvalidOptionException(Throwable arg0) {
		super(arg0);
	}

	public InvalidOptionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
