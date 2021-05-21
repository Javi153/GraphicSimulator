package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder<ForceLaws>{
	
	//Constructor por defecto
	public NoForceBuilder() {
		_typeTag = "nf";
		_desc = "no force is applied";
	}
	@Override
	//Devuelve una instancia de NoForce
	public ForceLaws createTheInstance(JSONObject info) throws IllegalArgumentException{
		return new NoForce();
	}

	@Override
	//Devuelve una instancia de NoForce en formato JSONObject con valores por defecto
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.append("type", _typeTag);
		jo.append("data", new JSONObject());
		jo.append("desc", "No force");
		return jo;
	}
	
}
