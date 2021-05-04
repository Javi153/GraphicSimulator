package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;

public class EpsilonEqualStates implements StateComparator{
	
	private double eps;	//Epsilon
	
	/*Constructor*/
	
	public EpsilonEqualStates(double eps) {
		this.eps = eps;
	}
	
	/*Métodos*/
	
	/**
	 * Devuelve si a y b son "iguales" con respecto eps(su diferencia en valos abs es menor que eps)
	 * @param a	Primer valor(double) a comparar
	 * @param b	Segundo valor(double) a comparar
	 * @return	Devuelve true si son iguales
	 */
	
	private boolean equals(double a, double b) {
		return Math.abs(a-b) <= eps;
	}
	
	/**
	 * Devuelve si jV1 y jV2 son iguales, con respecto al eps(su diferencia en valos abs es menor que eps)
	 * @param jV1	Primer JSONArray a comparar
	 * @param jV2	Segundo JSONArray a comparar
	 * @return	Devuelve true si jV1 y jV2 son "iguales"
	 */
	
	private boolean equals(JSONArray jV1, JSONArray jV2) {
		Vector2D v1 = new Vector2D(jV1.getDouble(0), jV1.getDouble(1));
		Vector2D v2 = new Vector2D(jV2.getDouble(0), jV2.getDouble(1));
		return v1.distanceTo(v2) <= eps;
	}
	
	/**
	 * Devuelve true si s1 y s2 son iguales modulo eps, es decir si sus tiempos son
	 * iguales y para cada cuerpos se cumple que sus id son iguales y los valores m, p, v y f
	 * son iguales modulo epsilon.
	 */
	public boolean equal(JSONObject s1, JSONObject s2) {
		
		if(s1.getDouble("time") != s2.getDouble("time")) {	return false;	}
		
		JSONArray jBodies1 = s1.getJSONArray("bodies");
		JSONArray jBodies2 = s2.getJSONArray("bodies");	
		
		int length = jBodies1.length();
				
		if(length != jBodies2.length()) {	return false;	}
		
		for(int i = 0; i < length; ++i) {
			JSONObject bd1 = jBodies1.getJSONObject(i);
			JSONObject bd2 = jBodies2.getJSONObject(i);
			if(!bd1.getString("id").equals(bd2.getString("id"))) {	return false;	} //Comparamos identificadores
			if(!this.equals(bd1.getDouble("m"),bd2.getDouble("m"))) {	return false;	}//Comparamos masas
			if(!this.equals(bd1.getJSONArray("p"),bd2.getJSONArray("p"))) {	return false;	}//Comparamos posiciones
			if(!this.equals(bd1.getJSONArray("v"),bd2.getJSONArray("v"))) {	return false;	}//Comparamos velocidades
			if(!this.equals(bd1.getJSONArray("f"),bd2.getJSONArray("f"))) {	return false;	}//Comparamos fuerzas
		}

		return true;
	}

}
