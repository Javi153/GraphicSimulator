package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel
implements SimulatorObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ...
	private List<Body> _bodies;

	private String[] _colNames = { "Id", "Mass", "Position", "Velocity", "Force" };
	
	BodiesTableModel(Controller ctrl) {
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}
	
	@Override
	public int getRowCount() {
	// TODO complete
		return _bodies == null ? 0 : _bodies.size(); //Si la lista de cuerpos es distinta de null el numero de filas coincide con el numero de cuerpos
	}
	
	@Override
	public int getColumnCount() {
	// TODO complete
		return _colNames.length; //Hemos definido las columnas asi que coinciden con la longitud de nuestro array de String
	}
	
	@Override
	public String getColumnName(int column) {
	// TODO complete
		return _colNames[column]; //Devuelve el nombre de una columna especifica
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	// TODO complete
		Object s = null; //Si los datos introoducidos no son validos se devolvera null
		switch(columnIndex) { //Devolvemos un atributo distinto de body dependiendo de la columna
		case 0:				  //Siempre se toma el body correspondiente a la fila dada
			s = _bodies.get(rowIndex).getId();
			break;
		case 1:
			s = _bodies.get(rowIndex).getMass();
			break;
		case 2:
			s = _bodies.get(rowIndex).getPosition();
			break;
		case 3:
			s = _bodies.get(rowIndex).getVelocity();
			break;
		case 4:
			s = _bodies.get(rowIndex).getForce();
			break;
		}
		return s;
	}
	
	// SimulatorObserver methods
	// ...
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) { //En los metodos del Observer solo actualizaremos _bodies cuando sea necesario
		// TODO Auto-generated method stub												  //Y posteriormente actualizaremos la tabla
		_bodies = bodies;
		fireTableDataChanged();
	}
	
	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		_bodies = bodies;
		fireTableDataChanged();
	}
	
	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		// TODO Auto-generated method stub
		_bodies = bodies;
		fireTableDataChanged();
	}
	
	@Override
	public void onAdvance(List<Body> bodies, double time) {
		// TODO Auto-generated method stub
		_bodies = bodies;
		fireTableDataChanged();
	}
	
	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		// TODO Auto-generated method stub
		
	}

}