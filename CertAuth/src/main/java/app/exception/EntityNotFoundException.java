package app.exception;

public class EntityNotFoundException extends Exception {

	private static final long serialVersionUID = 6453288448769014604L;
	
	public EntityNotFoundException(String message) {
        super(message);
    }

}
