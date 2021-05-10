package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;

public class MainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8720525028556625831L;
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("Physics Simulator");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		//TODO complete this method to build the GUI
		setIconImage(new ImageIcon("resources/icons/physics.png").getImage());
		mainPanel.add(new ControlPanel(_ctrl), BorderLayout.PAGE_START);
		mainPanel.add(new StatusBar(_ctrl), BorderLayout.PAGE_END);
		JPanel secondPanel = new JPanel();
		secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.Y_AXIS));
		BodiesTable bt = new BodiesTable(_ctrl);
		bt.setPreferredSize(new Dimension(getWidth(), getHeight()/2));
		secondPanel.add(bt);
		Viewer viewer = new Viewer(_ctrl);
		viewer.requestFocusInWindow();
		secondPanel.add(viewer);
		mainPanel.add(secondPanel, BorderLayout.CENTER);
        	
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
