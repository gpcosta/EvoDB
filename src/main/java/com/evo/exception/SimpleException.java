package com.evo.exception;

public class SimpleException extends Exception {
	
	public SimpleException() {
		super();
	}
	
	public SimpleException(String message) {
		super(message);
	}
	
	public SimpleException(Throwable cause) {
		super(cause);
	}
	
	public SimpleException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
