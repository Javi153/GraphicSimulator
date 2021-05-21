package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.control.EpsilonEqualStates;
import simulator.control.StateComparator;

public class EpsilonEqualStatesBuilder extends Builder<StateComparator>{
	
	private static final double DefaultEps = 0.0;

	//Constructor por defecto
	public EpsilonEqualStatesBuilder() {
		_typeTag = "epseq";
		_desc = "epsilon comparator";
	}
	
	//Crea una instancia de EpsilonEqualStates a partir de un JSONObject si los datos son validos
	@Override
	public StateComparator createTheInstance(JSONObject info) throws IllegalArgumentException{
		try {
			double eps;
			if(info.has("eps")) { //Comprueba si hay un valor para epsilon y sino usa el valor por defecto
				eps = info.getDouble("eps");
			}
			else {
				eps = DefaultEps;
			}
			return new EpsilonEqualStates(eps); //Devuelve la instancia de EpsilonEqualStates
		} catch(JSONException j) { //Si los datos no son validos lanza una excepcion
			throw new IllegalArgumentException(j);
		}
	}

	//Crea una instancia con valores por defecto en formato JSONObject
	@Override
	public JSONObject createData() {
		double eps = DefaultEps;
		JSONObject jo = new JSONObject();
		jo.put("eps", eps);
		return jo;
	}
	
}
