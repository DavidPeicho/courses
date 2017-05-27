package syma.exceptions;

public class SymaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SymaException(String message) {
		super("Syma: " + message);
	}
	

}
