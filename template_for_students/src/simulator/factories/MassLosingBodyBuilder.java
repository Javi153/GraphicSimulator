package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.MassLossingBody;

public class MassLosingBodyBuilder extends Builder<Body>{
	
	//Constructor por defecto
	public MassLosingBodyBuilder() {
		_typeTag = "mlb";
		_desc = "a body that loses mass";
	}
	
	//Crea una instancia de MassLosingBody a partir de un JSONObject si los datos son validos
	public MassLossingBody createTheInstance(JSONObject info) throws IllegalArgumentException{
		try {
			String id = info.getString("id"); //Accedemos al identificador
			JSONArray jArr = info.getJSONArray("p"); //Accedemos a la posicion y creamos el vector
			Vector2D position = new Vector2D(jArr.getDouble(0), jArr.getDouble(1));
			jArr = info.getJSONArray("v"); //Accedemos a la velocidad y creamos el vector
			Vector2D velocity = new Vector2D(jArr.getDouble(0), jArr.getDouble(1));
			double mass = info.getDouble("m"); //Accedemos a la masa
			if(mass < 0) {	throw new IllegalArgumentException("The body has negative mass.");	}
			double freq = info.getDouble("freq"); //Accedemos a la frecuencia
			if(freq < 0) {	throw new IllegalArgumentException("The body has negative frequency.");	}
			double factor = info.getDouble("factor"); //Accedemos al factor de perdida de masa
			if(factor < 0 || factor > 1) {	throw new IllegalArgumentException("The body factor is not between 0 and 1.");	}
			return new MassLossingBody(id, velocity, position, mass, factor, freq); //Devolvemos una instancia de MassLossingBody con los datos proporcionados
		} catch(JSONException j) {
			throw new IllegalArgumentException(j); //Si alguno de los datos no es valido lanzamos una excepcion
		}
	}

	@Override
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.put("id", "b1");
		jo.put("p", new Vector2D(0.0E00, 0.0E00).asJSONArray());
		jo.put("v", new Vector2D(0.05E04, 0.0E00).asJSONArray());
		jo.put("m", 5.97E24);
		jo.put("freq", 1E3);
		jo.put("factor", 1E-3);
		return jo;
	}
}
