package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws{
	private final static double DefaultG = 6.67E-11;
	private double G;
	
	//Constructor por defecto
	public NewtonUniversalGravitation() {
		G = DefaultG;
	}
	
	//Constructor con valor especifico de G
	public NewtonUniversalGravitation(double G) {
		this.G = G;
	}
	
	//Calcula la fuerza de newton ejercida por b2 sobre b1
	public Vector2D NewtonForce(Body b1, Body b2) {
		Vector2D difPosic = new Vector2D(b2.getPosition().minus(b1.getPosition())); 
		double distancia = b2.getPosition().distanceTo(b1.getPosition());//Calculas la distancia
		if(distancia != 0.0) { //Y si no es nula aplicas la formula de Newton
			double fij = (G * b1.getMass() * b2.getMass())/(distancia*distancia);
			return difPosic.direction().scale(fij);
		}
		else { //Sino la fuerza es nula
			return new Vector2D(0.0, 0.0);
		}
	}
	
	//Se aplica la fuerza a los cuerpos
	@Override
	public void apply(List<Body> bs) {
		for(int i = 0; i < bs.size(); ++i) {
			for(int j = 0; j < bs.size(); ++j) {
				if(i != j) { //Evitamos calcular la fuerza de un cuerpo sobre si mismo
					bs.get(i).addForce(NewtonForce(bs.get(i), bs.get(j))); //Sumas la fuerza de cada cuerpo sobre bi
				}
			}
		}
	}
	
	
	public String toString() {
	    return "Newton's Universal Gravitation with G = "+ G;
	}

}
