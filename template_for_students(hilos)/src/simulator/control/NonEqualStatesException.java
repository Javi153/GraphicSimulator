package simulator.control;

import org.json.JSONObject;

public class NonEqualStatesException extends Exception{

	/**
	 * Exceción que muestra cuando dos estados son distintos
	 */
	private static final long serialVersionUID = 1L;
	
	/*Constructores*/
	
	public NonEqualStatesException() {
		super("Los estados no coinciden");
	}
	
	public NonEqualStatesException(JSONObject s1, JSONObject s2, int n) {
		super("Los estados :" + s1.toString() + " y " + s2.toString() + " no coinciden, estos "
				+ "estados se corresponden con el estado numero:" + n);
	}
	
	public NonEqualStatesException(String cadena) {
		super(cadena);
	}
	
	public NonEqualStatesException(String cadena, Throwable cause) {
		super(cadena, cause);
	}
}
