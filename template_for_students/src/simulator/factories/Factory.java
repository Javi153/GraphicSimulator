package simulator.factories;

import java.util.List;

import org.json.JSONObject;

public interface Factory<T> {
	//Crea una instancia de T a partir de un JSONObjetc
	public T createInstance(JSONObject info);

	//Crea una lista de plantillas de los objetos en formato de JSONObject
	public List<JSONObject> getInfo();
}
