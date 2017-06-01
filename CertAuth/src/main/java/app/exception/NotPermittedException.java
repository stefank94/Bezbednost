package app.exception;

public class NotPermittedException extends Exception {

	private static final long serialVersionUID = -6097488452138745700L;
	
	public NotPermittedException(String message) {
		super(message);
	}

}
