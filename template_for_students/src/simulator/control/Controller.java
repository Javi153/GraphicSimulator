package simulator.control;

import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.factories.Factory;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import java.io.PrintStream;
import java.util.List;

public class Controller {
	
	private PhysicsSimulator simulator;	//Representa el simulador físico
	private Factory<Body> constructorBodies;	//Constructor de los cuerpos
	private Factory<ForceLaws> constructorForceLaws;
	
	/*Constructor*/
	
	public Controller(PhysicsSimulator simulator, Factory<Body> constructorBodies, Factory<ForceLaws> constructorForceLaws) {
		this.constructorBodies = constructorBodies;
		this.simulator = simulator;
		this.constructorForceLaws = constructorForceLaws;
	}
	
	
	/*Métodos*/
	
	/**
	 * Carga los cuerpos del flujo in en el simulador
	 * @param in Flujo de entrada de donde se cargan los cuerpos
	 */
	public void loadBodies(InputStream in) {

		JSONObject jsonInput = new JSONObject(new JSONTokener(in));	//Cargamos la información en un JSONObject

		int length = jsonInput.getJSONArray("bodies").length();		//Recorremos el JSONObject para crear y anadir los distintos cuerpos al simulador
		for (int i = 0; i < length; ++i) {
			JSONObject jBd = jsonInput.getJSONArray("bodies").getJSONObject(i);
			Body newBody = constructorBodies.createInstance(jBd);
			simulator.addBody(newBody);
		}
	}
	
	/**
	 * Lleva a cabo los pasos indicados por n, sacando los datos por out y comparando con expOut(a traves de cmp) en caso de que se proporcione como parametro
	 * @param n Numero de pasos que se van a llevar a cabo
	 * @param out	Flujo de salida
	 * @param expOut	Flujo de entrada de la salida esperada
	 * @param cmp	Comparador que vamos a utilizar
	 * @throws NonEqualStatesException	Si los estados no son iguales según el comparador utilizado salta una excepción
	 */
	public void run(int n, OutputStream out, InputStream expOut, StateComparator cmp) throws NonEqualStatesException {
		
		JSONArray jStates = null;
		if (expOut != null) {
			JSONObject jsonInput = new JSONObject(new JSONTokener(expOut));	//Cargamos la expected output en un JSONObject
			jStates = jsonInput.getJSONArray("states");
		}

		PrintStream p = new PrintStream(out);	//Creamos un flujo de salida a a partir del proporcionado
		p.println("{");
		p.println("\"states\": [");

		JSONObject aux = simulator.getState();	//Sacamos el primer estado
		if (expOut != null) {
			if (!cmp.equal(aux, jStates.getJSONObject(0))) {	//Comparamos con la salida esperada
				throw new NonEqualStatesException(aux, jStates.getJSONObject(0), 0);	//Salta excepción si no son iguales por el cmp
			}
		}
		p.println(aux);

		for (int i = 1; i <= n; ++i) {	//Sacamos los sucesivos estados
			simulator.advance();	//Avanzamos un paso en la simulación
			aux = simulator.getState();
			if (expOut != null) {
				if (!cmp.equal(aux, jStates.getJSONObject(i))) {	//Comparamos con la salida esperada
					throw new NonEqualStatesException(aux, jStates.getJSONObject(i), i);	//Salta excepción si no son iguales por el cmp
				}
			}
			p.println("," + aux);
		}

		p.println("]");
		p.println("}");
	}
	
	public void reset() {
	    this.simulator.reset();
	}
	
	public void setDeltaTime(double dt) {
	    this.simulator.setDeltaTime(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
	    this.simulator.addObserver(o);
	}
	
	public void run(int n) {
	    for(int i = 0; i < n; ++i) {
		simulator.advance();
	    }
	}
	
	//TO DO: Actualizar para que las cosas de la GUI vayan
	public List<JSONObject> getForceLawsInfo(){
	    /*Aqui vienen cosas de GUI que todavia no entiendo*/
	    return constructorForceLaws.getInfo();
	}
	
	public void setForceLaws(JSONObject info) {
		ForceLaws aux = constructorForceLaws.createInstance(info);
	    simulator.setForceLaws(aux);
	}
	
}
