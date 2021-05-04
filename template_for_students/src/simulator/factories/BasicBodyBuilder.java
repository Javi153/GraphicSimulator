package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body>{
	
	public BasicBodyBuilder() { //Constructor por defecto
		_typeTag = "basic";
		_desc = "a basic body";
	}
	
	//Dado un JSONObject devuelve una instancia de Body siempre que los datos sean validos, sino, lanza una excepcion
	@Override
	public Body createTheInstance(JSONObject info) throws IllegalArgumentException{
		try {
		String id = info.getString("id");
		
		JSONArray jP = info.getJSONArray("p");//Accedemos a posicion y creamos el vector
		Vector2D position = new Vector2D(jP.getDouble(0), jP.getDouble(1));
		
		JSONArray jV = info.getJSONArray("v");//Accedemos a la velocidad y creamos el vector
		Vector2D velocity = new Vector2D(jV.getDouble(0), jV.getDouble(1));
		
		double mass = info.getDouble("m");//Accedemos a la masa
		if(mass < 0) { throw new IllegalArgumentException("The body factor is not between 0 and 1.");	}
		
		return new Body(id, velocity, position, mass); //Devuelve una instancia de cuerpo con los atributos del JSONObject
		} catch(JSONException j) {
			throw new IllegalArgumentException(j);
		}
	}
	
	//Crea un JSONObject con datos predefinidos para que sirvan de plantilla
	@Override
	public JSONObject createData() { 
		JSONObject jo = new JSONObject();
		jo.put("id", "b1");
		jo.put("p", new Vector2D().asJSONArray());
		jo.put("v", new Vector2D(0.05E04, 0.0E00).asJSONArray());
		jo.put("m", 5.97E24);
		return jo;
	}

	
		
}
