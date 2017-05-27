package syma.exceptions;

public class EnvironmentException extends SymaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnvironmentException(String message) {
		super("Environment Initialization Error: " + message);
	}
	
}
