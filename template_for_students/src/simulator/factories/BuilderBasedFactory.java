package simulator.factories;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuilderBasedFactory<T> implements Factory<T>{
	private List<Builder<T>> builders;
	private List<JSONObject> UnmodListJo;
	
	//Constructor con parametro de lista de builders
	public BuilderBasedFactory(List<Builder<T>> list){
		builders = list;
		List<JSONObject> listjo = new ArrayList<>();
		for(Builder<T> b : builders) {
			listjo.add(b.getBuilderInfo()); //Para esto llamamos al getInfo de cada builder
		}
		UnmodListJo = Collections.unmodifiableList(listjo); //Inicializamos la lista como no modificable
	}

	//Dado un JSONObject creamos una instancia que encaje con la informacion del mismo
	@Override
	public T createInstance(JSONObject info) throws IllegalArgumentException{ 
		T inst = null;
		for(Builder<T> b : builders) { //Recorre cada builder y busca el adecuado para el JSONObject dado
			inst = b.createInstance(info);
			if(inst != null) { //Si la instancia devuelta no es null es que hemos encontrado el tipo correspondiente
				return inst;
			}
		}
		throw new IllegalArgumentException(); //Si al salir no hemos creado ninguna instancia lanzamos una excepcion
	}

	//Crea una lista de instancias por defecto de cuerpos, leyes y comparadores en forma de JSONObjects
	@Override
	public List<JSONObject> getInfo() { 
		return UnmodListJo;
	}
}
