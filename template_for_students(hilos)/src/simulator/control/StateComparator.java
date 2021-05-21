package simulator.control;

import org.json.JSONObject;

public interface StateComparator {
	/**
	 * Interfaz que representa un comparador de estados con un unico metodo abstracto,
	 * que devuelve si dos estados son iguales
	 * @param s1	Primer estado
	 * @param s2	Segundo estado
	 * @return	Devuelve true si los estados son iguales
	 */
	boolean equal(JSONObject s1, JSONObject s2);
}
