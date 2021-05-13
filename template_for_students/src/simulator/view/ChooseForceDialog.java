package simulator.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONObject;

import java.util.List;

public class ChooseForceDialog extends JDialog{    	
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //Indica el estado del cuadro de diálogo
    private int _status;
    //Indica el tipo de fuerza con la que estamos trabajando actualmente
    private int forceType = 0;
    List<JSONObject> _fJSON;
    //Atributos para crear el JComboBox
    private JComboBox<String> _forces;
    private DefaultComboBoxModel<String> _forcesModel;
    
    //Atributos para la tabla modificable
    private JsonTableModel _dataTableModel;
    
    
    	// Este modelo de tabla guarda internamente los datos introducidos. Usar
 	// getData() para obtener la información en formato JSON
 	//
 	private class JsonTableModel extends AbstractTableModel {

 		/**
 		 * 
 		 */
 		private static final long serialVersionUID = 1L;
 		
 		private final static int numRow = 5;
 		private final static int numCol = 3;

 		private String[] _header = { "Key", "Value", "Description" };
 		String[][] _data;

 		JsonTableModel() {
 			_data = new String[numRow][numCol];
 			clear();
 		}

 		public void clear() {
 			for (int i = 0; i < numRow; i++)
 				for (int j = 0; j < numCol; j++)
 					_data[i][j] = "";
 			fireTableStructureChanged();
 		}

 		@Override
 		public String getColumnName(int column) {
 			return _header[column];
 		}

 		@Override
 		public int getRowCount() {
 			return _data.length;
 		}

 		@Override
 		public int getColumnCount() {
 			return _header.length;
 		}

 		@Override
 		public boolean isCellEditable(int rowIndex, int columnIndex) {
 			return columnIndex == 1;
 		}

 		@Override
 		public Object getValueAt(int rowIndex, int columnIndex) {
 			return _data[rowIndex][columnIndex];
 		}

 		@Override
 		public void setValueAt(Object o, int rowIndex, int columnIndex) {
 			_data[rowIndex][columnIndex] = o.toString();
 		}

 		// El método getData()devuelve un JSON con 
 		// la columna nº 1 como key y la 2ª como value


 		public JSONObject getData() {
 			StringBuilder s = new StringBuilder();
 			s.append('{');
 			for (int i = 0; i < _data.length; i++) {
 				if (!_data[i][0].isEmpty() && !_data[i][1].isEmpty()) {
 					s.append('"');
 					s.append(_data[i][0]);
 					s.append('"');
 					s.append(':');
 					s.append(_data[i][1]);
 					s.append(',');
 				}
 			}

 			if (s.length() > 1)
 				s.deleteCharAt(s.length() - 1);
 			s.append('}');
 			
 			return new JSONObject(s.toString());
 		}
 	}
    
    public ChooseForceDialog(Frame parent, List<JSONObject> forces) {
	super(parent, true);
	//Guardamos los JSON para acceder posteriormente a su información
	_fJSON = forces;
	//Guardamos las descripciones de los JSON
	_forcesModel = new DefaultComboBoxModel<>();
	for(JSONObject j : forces)
	    _forcesModel.addElement(j.getString("desc"));
	
	initGUI();
    }
    
    private void initGUI() {
	
	_status = 0;
	
	//Fijamos los distintos parámetros del cuadro de diálogo
	setTitle("Force Laws Selection");
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	setContentPane(mainPanel);   
	
	//Creamos el mensaje de ayuda
	JLabel helpMsg = new JLabel("Select a force law and provide values for the parametres in the Value column"
		+ "(default values are used for parametres with no value)");
	
	//Aqui es donde creamos la tabla con los datos
	
	_dataTableModel = new JsonTableModel();
	JTable dataTable = new JTable(_dataTableModel) {
	    
	 /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

		// we override prepareRenderer to resized rows to fit to content
        	 @Override
        	 public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        	 	Component component = super.prepareRenderer(renderer, row, column);
        	 	component.setBackground(Color.WHITE);
        	 	int rendererWidth = component.getPreferredSize().width;
        	 	TableColumn tableColumn = getColumnModel().getColumn(column);
        	 	tableColumn.setPreferredWidth(
        	 		Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
        	 	return component;
        	 }
	};
	JScrollPane tableScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	//Mantenemos actualizada la tabla
	
	
	//Aqui es donde añadimos el Jcombo
	JPanel viewsPanel = new JPanel();
	viewsPanel.setAlignmentX(CENTER_ALIGNMENT);
	
	//Aqui añadimos los botones
	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
	
	_forces = new JComboBox<>(_forcesModel);
	
	//Mantenemos actualizada la tabla al cambiar la opcion del JComboBox
	_forces.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		//Modificamos la tabla
		ChooseForceDialog.this._dataTableModel.clear();

		switch(ChooseForceDialog.this._forces.getSelectedIndex()) {
		case(0): //Newton
		    	ChooseForceDialog.this.forceType = 0;
			ChooseForceDialog.this._dataTableModel.setValueAt("G", 0, 0);
			ChooseForceDialog.this._dataTableModel.setValueAt(_fJSON.get(forceType).getJSONObject("data").get("G"), 0, 2);
		    break;
		case(1): //No force
		  	ChooseForceDialog.this.forceType = 1;
		    break;
		case(2): //Moving fixedPoint
		    	ChooseForceDialog.this.forceType = 2;
        		ChooseForceDialog.this._dataTableModel.setValueAt("c", 0, 0);
        		ChooseForceDialog.this._dataTableModel.setValueAt(_fJSON.get(forceType).getJSONObject("data").get("c"), 0, 2);
        		ChooseForceDialog.this._dataTableModel.setValueAt("g", 1, 0);
        		ChooseForceDialog.this._dataTableModel.setValueAt(_fJSON.get(forceType).getJSONObject("data").get("g"), 1, 2);
		    
		    break;
		}
		_dataTableModel.fireTableDataChanged();
	    }
	});
	
	viewsPanel.add(_forces);
	
	
	//Boton de cancelar
	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			_status = 0;
			ChooseForceDialog.this.setVisible(false);
		}
	});
	buttonsPanel.add(cancelButton);

	//Boton de OK
	JButton okButton = new JButton("OK");
	okButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (_forcesModel.getSelectedItem() != null) {
				_status = 1;
				ChooseForceDialog.this.setVisible(false);
			}
		}
	});
	buttonsPanel.add(okButton);
	
	
	
	//Colocamos los componentes
	mainPanel.add(helpMsg);
	mainPanel.add(tableScroll);
	mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	mainPanel.add(viewsPanel);
	mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	mainPanel.add(buttonsPanel);
	
	//Ajustamos la ventana
	
	setPreferredSize(new Dimension(800, 400));
	pack();
	setResizable(false);
	setVisible(false);
    }
    
    
    public int open() {
	if (getParent() != null)
		setLocation(//
				getParent().getLocation().x + getParent().getWidth() / 2 - getWidth() / 2, //
				getParent().getLocation().y + getParent().getHeight() / 2 - getHeight() / 2);
	setResizable(true);
	setVisible(true);
	
	return _status;
    }
    
    /**
     * 
     * @return Un JSONObject que representa la fuerza que el usuario ha elegido
     */
    public JSONObject getJSON() {
	JSONObject j = new JSONObject();
	String type = null;
	switch(this.forceType) {
	case(0):
	    type = "nlug";
	break;
	case(1):
	    type = "nf";
	break;
	case(2):
	    type = "mtfp";
	}
	j.put("type", type);
	j.put("data", _dataTableModel.getData());
	return j;
    }
}
