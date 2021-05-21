package simulator.model;

import java.util.List;

public interface ForceLaws {
	//Aplica la fuerza a una lista de cuerpos
	public void apply(List<Body> bodies);
}
