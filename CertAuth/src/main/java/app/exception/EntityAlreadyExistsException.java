package app.exception;

public class EntityAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 9177082166687991689L;
	
	public EntityAlreadyExistsException(String message){
		super(message);
	}

}
