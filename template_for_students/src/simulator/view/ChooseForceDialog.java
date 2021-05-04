package simulator.view;

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
    private int _status;
    
    //Atributos para crear el JComboBox
    private JComboBox<String> _forces;
    private DefaultComboBoxModel<String> _forcesModel;
    
    //Atributos para la tabla modificable
    private JsonTableModel _dataTableModel;
    
    
// This table model stores internally the content of the table. Use
 	// getData() to get the content as JSON.
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

 		// Method getData() returns a String corresponding to a JSON structure
 		// with column 1 as keys and column 2 as values.

 		// This method return the coIt is important to build it as a string, if
 		// we create a corresponding JSONObject and use put(key,value), all values
 		// will be added as string. This also means that if users want to add a
 		// string value they should add the quotes as well as part of the
 		// value (2nd column).
 		//
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
	
	//Guardamos las descripciones de los JSON
	_forcesModel = new DefaultComboBoxModel<>();
	for(JSONObject j : forces)
	    _forcesModel.addElement(j.getString("desc"));
	
	initGUI();
    }
    
    private void initGUI() {
	
	_status = 0;
	
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
		switch(_forces.getSelectedIndex()) {
		case(0): //Newton
		    	_dataTableModel.setValueAt("G", 0, 0);
		    	_dataTableModel.setValueAt("The gravitational constant(a number)", 0, 2);
		    break;
		case(1): //No force
			_dataTableModel.clear();
		    break;
		case(2): //Moving fixedPoint

        		_dataTableModel.setValueAt("c", 0, 0);
        		_dataTableModel.setValueAt("The point towards which bodies move(a json list of 2 numbers, e.g.[100.0, 50.0])", 0, 2);
        		_dataTableModel.setValueAt("g", 1, 0);
        		_dataTableModel.setValueAt("The lenght of the acceleration vector(a number)", 1, 2);
		    
		    break;
		}
	    }
	});
	
	viewsPanel.add(_forces);
	
	
	
	switch(_forces.getSelectedIndex()) {
	case(0): //Newton
	    	_dataTableModel.setValueAt("G", 0, 0);
	    	_dataTableModel.setValueAt("The gravitational constant(a number)", 0, 2);
	    break;
	case(1): //No force
		_dataTableModel.clear();
	    break;
	case(2): //Moving fixedPoint

		_dataTableModel.setValueAt("c", 0, 0);
		_dataTableModel.setValueAt("The point towards which bodies move(a json list of 2 numbers, e.g.[100.0, 50.0])", 0, 2);
		_dataTableModel.setValueAt("g", 1, 0);
		_dataTableModel.setValueAt("The lenght of the acceleration vector(a number)", 1, 2);
	    
	    break;
	}
	
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
    
    public JSONObject getJSON() {
	return _dataTableModel.getData();
    }
}
