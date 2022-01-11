package Profils;

public class ExceptionProfil extends Exception {
	String message;
	public ExceptionProfil(String message) {
		this.message=message;
	}
}
