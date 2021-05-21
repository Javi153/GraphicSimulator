package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import simulator.control.Controller;

public class MainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8720525028556625831L;
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) { //Constructor
		super("Physics Simulator");
		_ctrl = ctrl;
		initGUI(); //Inicializamos la interfaz gráfica
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		setIconImage(new ImageIcon("resources/icons/physics.png").getImage()); //Hemos utilizado esa imagen para el icono
		mainPanel.add(new ControlPanel(_ctrl), BorderLayout.PAGE_START); //Añadimos el panel de control
		mainPanel.add(new StatusBar(_ctrl), BorderLayout.PAGE_END); //Añadimos la barra de estado
		JPanel secondPanel = new JPanel(); //Creamos un nuevo panel donde añadimos la tabla de cuerpos
		secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.Y_AXIS));
		BodiesTable bt = new BodiesTable(_ctrl);
		bt.setPreferredSize(new Dimension(getWidth(), getHeight()/2));
		secondPanel.add(bt);
		Viewer viewer = new Viewer(_ctrl);
		viewer.requestFocusInWindow();
		secondPanel.add(viewer); //También añadimos el viewer al segundo panel
		mainPanel.add(secondPanel, BorderLayout.CENTER);//Y este segundo panel lo incluimos en el principal
        	
		this.pack();
		this.setMinimumSize(new Dimension(800, 600)); //Ponemos un tamaño minimo a la ventana para que no sea demasiado pequeña
		this.setVisible(true); //La mostramos
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); //Y terminamos el programa al salir
	}
}
