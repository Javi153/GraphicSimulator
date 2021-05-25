package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.*;

public class PhysicsSimulator {

	private double dt;
	private ForceLaws law;
	private ArrayList<Body> bodies;
	private List<SimulatorObserver> observers;

	private double time = 0.0;

	//Constructor
	public PhysicsSimulator(double dt, ForceLaws law) {
		this.dt = dt;
		this.law = law;
		bodies = new ArrayList<Body>();
		//Cambiar a solo lectura
		observers = new ArrayList<SimulatorObserver>();
	}

	//Metodos
	//Realiza un paso del simulador
	public void advance() {
		List<Body> bd = Collections.unmodifiableList(bodies);
		for (Body body : bodies) { //Reinicia la fuerza sobre los cuerpos
			body.resetForce();
		}
		law.apply(bodies); //Aplica la fuerza sobre los cuerpos
		for(Body body : bodies) { //Mueve cada cuerpo en un tiempo equivalente a un paso
			body.move(dt);
		}
		time += dt; //Aumenta el contador del simulador
		for(SimulatorObserver o : observers) {
			o.onAdvance(bd, time);
		}
	}

	//A�ade un cuerpo a la lista de cuerpos
	public void addBody(Body b) {
		List<Body> bd = Collections.unmodifiableList(bodies);
		if(bodies.contains(b)){//Si el cuerpo ya esta a�adido se lanza una excepcion
				throw new IllegalArgumentException();
		}
		bodies.add(b);
		for(SimulatorObserver o : observers) {
			o.onBodyAdded(bd, b);
		}
	}
	
	
	public void reset() {
	    time = 0.0;
	    bodies.clear();
	    List<Body> bd = Collections.unmodifiableList(bodies);
	    for(SimulatorObserver o : observers) {
	    	o.onReset(bd, time, dt, law.toString());
	    }
	}
	
	public void setDeltaTime(double dt) throws IllegalArgumentException{
	    if(dt > 0) {
	    	this.dt = dt;
	    }
	    else {
	    	throw new IllegalArgumentException("The DeltaTime is not greater than 0");
	    }
	    for(SimulatorObserver o : observers) {
	    	o.onDeltaTimeChanged(dt);
	    }
	}
	
	public void setForceLaws(ForceLaws forceLaws) {
	    if(!forceLaws.equals(null)) {
	    	this.law = forceLaws;
	    }
	    else {
	    	throw new IllegalArgumentException("The force laws are equal to null");
	    }
	    for(SimulatorObserver o : observers) {
	    	o.onForceLawsChanged(law.toString());
	    }
	}
	
	

	//Devuelve el estado del simulador en formato JSONObject
	public JSONObject getState() {
		JSONObject simulator = new JSONObject();
		simulator.put("time", time);
		ArrayList<JSONObject> jBodies = new ArrayList<JSONObject>();
		for (Body bd : bodies) {
			JSONObject jBody = bd.getState(); //Devuelve el estado de cada cuerpo y lo a�ade al JSONObject
			jBodies.add(jBody);
		}
		simulator.put("bodies", jBodies);

		return simulator;
	}

	//Devuelve el estado en forma de String
	public String toString() {
		return getState().toString();
	}

	public void addObserver(SimulatorObserver o) {
		List<Body> bd = Collections.unmodifiableList(bodies);
		observers.add(o);
		o.onRegister(bd, time, dt, law.toString());
		
	}

}
