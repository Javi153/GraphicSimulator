package simulator.model;

import simulator.misc.Vector2D;
import org.json.*;

public class Body { //Representa una entidad física
	protected String id;
	protected Vector2D velocity;
	protected Vector2D force;
	protected Vector2D position;
	protected double mass;
	
	//Constructor por defecto
	public Body() {
		this.id = "";
		this.velocity = new Vector2D();
		this.position = new Vector2D();
		this.force = new Vector2D();
		this.mass = 0.0;
	}
	
	//Constructor con parametros para inicializar todos sus atributos
	public Body(String id, Vector2D velocity, Vector2D position, double mass) {
		this.id = id;
		this.velocity = velocity;
		this.position = position;
		this.force = new Vector2D();
		this.mass = mass;
	}
	
	
	/*Getters y setters*/
	
	public String getId() { //Devuelve el identificador
		return id;
	}
	public Vector2D getVelocity() { //Devuelve la velocidad
		return velocity;
	}
	public Vector2D getForce() { //Devuelve la fuerza
		return force;
	}
	public Vector2D getPosition() { //Devuelve la posicion
		return position;
	}
	public double getMass() { //Devuelve la masa
		return mass;
	}
	
	/*Metodos*/
	
	//Suma al vector de fuerza el vector f con las operaciones de Vector2D
	void addForce(Vector2D f) {
		force = force.plus(f);
	}
	
	//Pone el vector de fuerza a (0,0)
	void resetForce() {
		force = new Vector2D();
	}
	
	//Metodo move del BasicBody, varia la velocidad y la posicion del cuerpo atendiendo a la masa y la fuerza
	void move(double t) {
		Vector2D acceleration = new Vector2D(0,0);
		if(mass != 0) { //Si la masa es 0 el vector aceleracion es nulo
			acceleration = new Vector2D(force.scale(1/mass));
		}
		
		position = position.plus((velocity.scale(t)).plus(acceleration.scale(t*t*0.5))); //Variamos velocidad y posicion dependiendo de la aceleracion
		velocity = velocity.plus(acceleration.scale(t));
	}
	
	//Devuelve el estado del cuerpo, es decir, el valor de sus atributos, en forma de JSONObject
	public JSONObject getState() {
		JSONObject state = new JSONObject();
		state.put("id", id);
		state.put("m", mass);
		state.put("p", position.asJSONArray());
		state.put("v", velocity.asJSONArray());
		state.put("f", force.asJSONArray());
		return state;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Body other = (Body) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String toString() {
		return getState().toString();
	}
	
}
