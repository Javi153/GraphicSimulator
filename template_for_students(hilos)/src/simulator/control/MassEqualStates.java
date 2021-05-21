package simulator.control;

import org.json.*;

public class MassEqualStates implements StateComparator{
	
	
	/**
	 * Devuelve true si los estados s1 y s2 son iguales por masa, es decir
	 * los parametros de tiempo de ambos estados han de ser iguales, asi como
	 * los id y masa de cada cuerpo.
	 */
	public boolean equal(JSONObject s1, JSONObject s2) {
		
		if(s1.getDouble("time") != s2.getDouble("time")) {	return false;	}
		
		JSONArray jBodies1 = s1.getJSONArray("bodies");
		JSONArray jBodies2 = s2.getJSONArray("bodies");		
		
		int length = jBodies1.length();
		
		if(length != jBodies2.length()) {return false;	}
		
		for(int i = 0; i < length; ++i) {
			JSONObject bd1 = jBodies1.getJSONObject(i);
			JSONObject bd2 = jBodies2.getJSONObject(i);
			if(!bd1.getString("id").equals(bd2.getString("id"))) {	return false;	} //COmparamos identificadores
			if(bd1.getJSONObject("data").getDouble("m") != bd2.getJSONObject("data").getDouble("m")) {	return false;	}//Comparamos masas
		}
		return true;
	}

}
