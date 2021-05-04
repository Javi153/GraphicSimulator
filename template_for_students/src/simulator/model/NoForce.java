package simulator.model;

import java.util.List;

public class NoForce implements ForceLaws{

	//Aplicamos una fuerza nula, por lo que no se hace nada
	@Override
	public void apply(List<Body> bs) {	}
	
	public String toString() {
	    return "No Force";
	}

}
