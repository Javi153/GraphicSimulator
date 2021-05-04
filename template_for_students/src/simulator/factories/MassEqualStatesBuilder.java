package simulator.factories;

import org.json.JSONObject;

import simulator.control.MassEqualStates;
import simulator.control.StateComparator;

public class MassEqualStatesBuilder extends Builder<StateComparator>{

	//Constructor por defecto
	public MassEqualStatesBuilder() {
		_typeTag = "masseq";
		_desc = "mass comparator";
	}
	
	//Devuelve una instancia de MassEqualStates a partir de un JSONObject
	@Override
	public StateComparator createTheInstance(JSONObject info) throws IllegalArgumentException{
		return new MassEqualStates();
	}

	//Devuelve una instancia con valores por defecto en formato JSONObject
	@Override
	public JSONObject createData() {
		return new JSONObject();
	}

}
