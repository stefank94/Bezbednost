package app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import app.exception.ActionNotPossibleException;
import app.exception.EntityAlreadyExistsException;
import app.exception.EntityNotFoundException;
import app.exception.NotPermittedException;

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

}
