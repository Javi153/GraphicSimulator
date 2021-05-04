package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowardsFixedPointBuilder extends Builder<ForceLaws>{
	
	private static final double Defaultg = 9.81;
	private static final Vector2D Defaultc = new Vector2D();

	//Constructor por defecto
	public MovingTowardsFixedPointBuilder() {
		_typeTag = "mtfp";
		_desc = "a force that obligues to move towards a point";
	}
	
	
	@Override
	//devuelve una instancia de MovingTowardsFixedPoint a partir de un JSONObject si los datos son validos
	public ForceLaws createTheInstance(JSONObject info) throws IllegalArgumentException{
		double g;
		Vector2D c;
		try {
			if(info.has("g")) { //Comprobamos el valor de g. Si no se especifica, tomamos el valor por defecto
			g = info.getDouble("g");
			} 
			else {
				g = Defaultg;
			}
			if(info.has("c")) { //Comprobamos el valor de c (el centro). Si no se especifica, tomamos el valor por defecto
			JSONArray jArr = info.getJSONArray("c");
			c = new Vector2D(jArr.getDouble(0), jArr.getDouble(1));
			}
			else {
				c = Defaultc;
			}
			return new MovingTowardsFixedPoint(g, c);
		} catch(JSONException j) { //Si los datos no son validos lanzamos una excepcion
			throw new IllegalArgumentException(j);
		}
	}

	@Override
	//Se crea una instancia con valores por defecto en formato JSONObject
	public JSONObject createData() {
		JSONObject jo = new JSONObject();
		jo.append("type", _typeTag);
		JSONObject jd = new JSONObject();
		jd.append("c", "the point towards which bodies move");
		jd.append("g", "the length of the acceleration vector (a number)");
		jo.append("data", jd);
		jo.append("desc", _desc);
		return jo;
	}

}
