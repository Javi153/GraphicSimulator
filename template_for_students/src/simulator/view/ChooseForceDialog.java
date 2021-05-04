package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
	private MyListModel<String> _forceLawsListModel;
	
	private JList<String> _forceLawsList;
	private JComboBox<JSONObject> _forceLaws;
	private DefaultComboBoxModel<JSONObject> _forceLawsModel;
	private int _status;
	private JsonTableModel _dataTableModel;
	static final private char _clearSelectionKey = 'c';
	private Border _defaultBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
	
    	
    	public ChooseForceDialog(Frame frame, List<JSONObject> forces) {
    	    super(frame, true);
    	    
    	//Limpiamos por lo que puediera haber antes
    	    _forceLawsModel = new DefaultComboBoxModel<>();

    	
    	//Añadimos las fuerzas para luego ponerlas en el ComboBox
    	for (JSONObject v : forces)
			_forceLawsModel.addElement(v);
    	
    	
    	    initGUI();
    	}
    	

	private void initGUI() {
    	    
    	    _status = 0;
    	    
    	    //Creamos la que va a ser la estructura principal
    	    setTitle("Force Laws Selection");
    	    JPanel mainPanel = new JPanel(new BorderLayout());
    	    
    	    
    	    //Insertamos texto con instrucciones sobre como proceder
    	    JPanel infoPanel = new JPanel();
    	    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    	    
    	    infoPanel.add(new JLabel("Select a force law and provide values for the parametres in te Value column(default values are"
    	    	+ "used for paremetres with no value)."));
    	    
    	    mainPanel.add(infoPanel);
    	    
    	    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    	    
    	    //Tabla con los datos
    	    _dataTableModel = new JsonTableModel();
    	    JTable dataTable = new JTable(_dataTableModel) {
    	    
                    	private static final long serialVersionUID = 1L;
                
                	// we override prepareRenderer to resized rows to fit to content
                	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                		Component component = super.prepareRenderer(renderer, row, column);
                		int rendererWidth = component.getPreferredSize().width;
                		TableColumn tableColumn = getColumnModel().getColumn(column);
                		tableColumn.setPreferredWidth(
                				Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                		return component;
                	}
    	    
    	    //Creamos aquí la tabla
    	    JPanel contentPanel = new JPanel();
    	    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
    	    mainPanel.add(contentPanel, BorderLayout.CENTER);
    	    
    	    
    	    JPanel forcesPanel = new JPanel(new BorderLayout());
    	    contentPanel.add(forcesPanel);
    	    
    	    forcesPanel.setBorder(
    		    BorderFactory.createTitledBorder(_defaultBorder, "Forces", TitledBorder.LEFT,
    			    TitledBorder.TOP));
    	    forcesPanel.setMinimumSize(new Dimension(100, 100));
    	    
    	    _forceLawsListModel = new MyListModel<String>();
    	    
    	    _forceLawsList = new JList<>(_forceLawsListModel);
    	    
    	    addCleanSelectionListener(_forceLawsList);
    	    
    	    forcesPanel.add(new JScrollPane(_forceLawsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
    	    
    	    

    	    
    	    //Para elegir el tipo de fuerza
    	    JPanel selectionPanel = new JPanel();
    	    selectionPanel.setAlignmentX(CENTER_ALIGNMENT);
    	    mainPanel.add(selectionPanel);
    	    
    	    //Creamos un espacio intermedio para que sea más visual
    	    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    	    
    	    //Añadimos los botones
    	    JPanel buttonsPanel = new JPanel();
    	    buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
    	    mainPanel.add(buttonsPanel);
    	    
    	    //Creamos el ComboBox
    	    _forceLaws = new JComboBox<>(_forceLawsModel);
    	    
    	    //Lo añadimos al panel ya declarado
    	    selectionPanel.add(_forceLaws);
    	    
    	    //Añadimos los botones a su panel
            	//Botón de cancelar
            	JButton cancelButton = new JButton("Cancel");
        	cancelButton.addActionListener(new ActionListener() {
        
        		@Override
        		public void actionPerformed(ActionEvent e) {
        			_status = 0; 
        			ChooseForceDialog.this.setVisible(false);														}
        	});
        	buttonsPanel.add(cancelButton);
        
        	//Botón de OK
        	JButton okButton = new JButton("OK");
        	okButton.addActionListener(new ActionListener() {
        
        		@Override
        		public void actionPerformed(ActionEvent e) {
        			System.out.println("Has hecho clic en ok");
        			if (_forceLawsModel.getSelectedItem() != null) {
        				_status = 1;
        				ChooseForceDialog.this.setVisible(false);
        			}
        		}
        	});
        	buttonsPanel.add(okButton);
    	
        	//Añadimos los componentes
        	this.add(mainPanel);
        	setPreferredSize(new Dimension(500, 200));
        	pack();
        	setResizable(false);
        	setVisible(false);
    	}
    	
	private void addCleanSelectionListener(JList<?> list) {
		list.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == _clearSelectionKey) {
					list.clearSelection();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

	}
	
	public void setData(List<String> forces) {
	    _forceLawsListModel.setList(forces);
	}
    	
    	public String[] getSelectedForces() {
		int[] indices = _forceLawsList.getSelectedIndices();
		String[] items = new String[indices.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = _forceLawsListModel.getElementAt(indices[i]);
		}
		return items;
	}
    	
    	
    	public int open() {
     	
        	setLocation(getParent().getLocation().x + 50, getParent().getLocation().y + 50);
        	//pack();
        	setVisible(true);
        	return _status;
    	}
    	
    	
    	
    	
    	
}
