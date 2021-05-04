package simulator.factories;

import org.json.JSONObject;

public abstract class Builder<T> {
	protected String _typeTag;
	protected String _desc;
	
	/*Metodos*/
	
	//Crea una instancia del tipo del campo type del JSONObject
	public T createInstance(JSONObject info) throws IllegalArgumentException{ 
		if(info.has("type")) {
			if(_typeTag.equals(info.get("type"))) { //Comprobamos que existe el campo type y que es igual al typeTag del builder concreto
				try {
				return createTheInstance(info.getJSONObject("data")); //Si coincide se llama al constructor del tipo concreto
				} catch(IllegalArgumentException iae) {
					throw new IllegalArgumentException(iae);
				}
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	public abstract T createTheInstance(JSONObject info) throws IllegalArgumentException; //Cada builder debe tener su constructor de instancias concreto
	
	public abstract JSONObject createData(); //Cada builder debe tener una plantilla de creacion del objeto con atributos predefinidos
	
	//Crea un JSONObject por defecto con su typetag, su descripcion y su data (mediante el createData de cada objeto)
	public JSONObject getBuilderInfo() { 
		JSONObject jo = new JSONObject();
		jo.put("type", _typeTag);
		jo.put("data", createData()); //Llamamos al builder del objeto
		jo.put("desc", _desc);
		return jo;
	}
}
