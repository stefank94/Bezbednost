package app.exception;

public class ActionNotPossibleException extends Exception {

	private static final long serialVersionUID = -6950043572527399500L;
	
	public ActionNotPossibleException(String message){
		super(message);
	}

	public ActionNotPossibleException(Exception e){
		super(e);
	}

}
