package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class MovingTowardsFixedPoint implements ForceLaws{
	private final static double Defaultg = 9.81;
	private final static Vector2D Defaultc = new Vector2D();
	private double g;
	private Vector2D c;
	
	//Constructor por defecto
	public MovingTowardsFixedPoint() {
		g = Defaultg;
		c = Defaultc;
	}
	
	//Constructor con valores especificos para los atributos
	public MovingTowardsFixedPoint(double g, Vector2D c) {
		this.g = g;
		this.c = c;
	}

	//Aplica la fuerza a los cuerpos
	@Override
	public void apply(List<Body> bs) {
		
		for(Body bd : bs) {
			Vector2D direction = bd.getPosition().minus(c).direction(); //Calcula la direccion del centro al cuerpo
			
			Vector2D f = direction.scale(-g * bd.getMass()); //La fuerza es la direccion por g (en el sentido del cuerpo al centro)
			bd.addForce(f);
		}
	}
	
	public String toString() {
	    return "Moving towards " + c + " with constant acceleration " + g;
	}

}
