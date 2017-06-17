package app.controller;

import app.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
	
	@ExceptionHandler(EntityAlreadyExistsException.class)
	public ResponseEntity<Exception> handleEntityAlreadyExistsException(EntityAlreadyExistsException e){
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Exception> handleEntityNotFoundException(EntityNotFoundException e){
		return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NotPermittedException.class)
	public ResponseEntity<Exception> handleNotPermittedException(NotPermittedException e){
		return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(ActionNotPossibleException.class)
	public ResponseEntity<Exception> handleActionNotPossibleException(ActionNotPossibleException e){
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(WrongFileTypeException.class)
	public ResponseEntity<Exception> handleWrongFileTypeException(WrongFileTypeException e){
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<Exception> handleInvalidDataException(InvalidDataException e){
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}

}
