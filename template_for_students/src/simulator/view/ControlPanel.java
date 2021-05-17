package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1696025726243841820L;
	//Botones del juego
	private JButton openButton;
	private JButton physicsButton;
	private JButton runButton;
	private JButton stopButton;
	private JButton exitButton;


	private JTextField _d_time;
	private JSpinner _steps;
	private Controller _ctrl;
	private boolean _stopped;

	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_stopped = true;
		initGUI();
		_ctrl.addObserver(this);
	}


	private void initGUI() {
		this.setPreferredSize(new Dimension(150, 60));
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		String path = "resources/icons/";
		//Open Button
		ImageIcon open = new ImageIcon(path + "open.png");
		openButton = new JButton(open);
		openButton.setToolTipText("Open a file");
		//Anadimos funcionalidad al botón
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    //Creamos el JFileChooser para elegir el archivo desde el que cargamos los datos
				JFileChooser fileChooser = new JFileChooser("resources/examples");
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "JOSN Files", "json");
				fileChooser.setFileFilter(filter);
				int ret = fileChooser.showOpenDialog(ControlPanel.this);
				if(ret == JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(ControlPanel.this,  "You have selected to open this file: "
							+ fileChooser.getSelectedFile().getName());
					FileInputStream in;
					try {
						in = new FileInputStream(fileChooser.getSelectedFile());
						_ctrl.reset();
						_ctrl.loadBodies(in);
					}
					catch(Exception ex) {
					    JOptionPane.showMessageDialog(ControlPanel.this, "There was an error parsing this file",
						    "There was an error parsing this file", JOptionPane.ERROR_MESSAGE); 
					}
				}
				else {
					JOptionPane.showMessageDialog(ControlPanel.this, "You have selected cancel or an error has occurred");
				}
			}
		});

		//Physics Button
		ImageIcon physics = new ImageIcon(path + "physics.png");
		physicsButton = new JButton(physics);
		physicsButton.setToolTipText("Select the forces");
		physicsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	//Elegimos la fuerza que se va a aplicar
				Frame parent = (Frame) SwingUtilities.getWindowAncestor(ControlPanel.this);
				//Obtenemos los JSON de las posibles fuerzas
				List<JSONObject> forces = _ctrl.getForceLawsInfo();
				//Creamos el cuadro de diálogo correspondiente a la tabla para que se elijan las fuerzas
				ChooseForceDialog chooseForce = new ChooseForceDialog(parent, forces);

				//Guardamos la forma de salir del cuadro anterioir
				int status = chooseForce.open();
				//Se muestra una ventana en caso de error, en otro caso actualizamos la fuerza
				JSONObject info = null;
				if(status == 0) {
					JOptionPane.showMessageDialog(ControlPanel.this, "You have selected cancel or an error has occurred");
				}
				else {
					info = chooseForce.getJSON();
					try {
					    _ctrl.setForceLaws(info);
					}
					catch(IllegalArgumentException ex) {
					    JOptionPane.showMessageDialog(ControlPanel.this,
						    "Durante el parseo ha ocurrido una " + ex.getMessage() ,
						    "There was a problem", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		});
		//Run Button
		ImageIcon run = new ImageIcon(path + "run.png");
		runButton = new JButton(run);
		runButton.setToolTipText("Run the simulator");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openButton.setEnabled(false);
				physicsButton.setEnabled(false);
				runButton.setEnabled(false);
				exitButton.setEnabled(false);
				_stopped = false;
				try {
				_ctrl.setDeltaTime(Double.parseDouble(_d_time.getText()));
				}
				catch(NumberFormatException ex) {
				    JOptionPane.showMessageDialog(ControlPanel.this, "There was a problem parsing the deltaTime",
					    "There was a problem parsing the deltaTime", JOptionPane.ERROR_MESSAGE);
				    }
				int n = (int)_steps.getValue();

				ControlPanel.this.run_sim(n);
			}
		});

		//StopButton
		ImageIcon stop = new ImageIcon(path + "stop.png");
		stopButton = new JButton(stop);
		stopButton.setToolTipText("Stop the simulator");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_stopped = true;
			}
		});

		//Exit Button
		ImageIcon exit = new ImageIcon(path + "exit.png");
		exitButton = new JButton(exit);
		exitButton.setToolTipText("Exit Simulator");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int answ = JOptionPane.showOptionDialog(ControlPanel.this,
						"Are you sure you want to exit?", "Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);
				if(answ == 0 ) {
					System.exit(0);
				}
			}
		});


		JLabel stepsLabel = new JLabel("Steps:");
		_steps = new JSpinner(new SpinnerNumberModel(150, 1, 1000000, 1));
		JLabel timeLabel = new JLabel("Delta-Time:");
		_d_time = new JTextField("2500.0");

		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
		toolBar.add(openButton);
		toolBar.addSeparator(new Dimension(20, 10));
		toolBar.add(physicsButton);
		toolBar.addSeparator(new Dimension(20, 10));
		toolBar.add(runButton);
		toolBar.add(stopButton);
		toolBar.addSeparator(new Dimension(20, 10));
		toolBar.add(stepsLabel);
		_steps.setMaximumSize(new Dimension(300, 50));
		toolBar.add(_steps);
		toolBar.addSeparator(new Dimension(20, 10));
		toolBar.add(timeLabel);
		_d_time.setPreferredSize(new Dimension(100, 50));
		_d_time.setMaximumSize(new Dimension(150, 50));
		toolBar.add(_d_time);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(exitButton);
		add(toolBar);
		this.setVisible(true);
	}

	// other private/protected methods
	// ...
	private void run_sim(int n) {
		if ( n>0 && !_stopped ) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				// TODO show the error in a dialog box
			    JOptionPane.showMessageDialog(ControlPanel.this, "There was a problem", e.getMessage(), JOptionPane.ERROR_MESSAGE);

				//Activamos los botones(podriamos cambiarlo a un metodo aparte)
				openButton.setEnabled(true);
				physicsButton.setEnabled(true);
				runButton.setEnabled(true);
				exitButton.setEnabled(true);
				_stopped = true;
				return;
			}
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					run_sim(n-1);
				}
			});
		} else {
			_stopped = true;
			//Activamos los botones
			openButton.setEnabled(true);
			physicsButton.setEnabled(true);
			runButton.setEnabled(true);
			exitButton.setEnabled(true);
		}
	}

	// SimulatorObserver methods

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		Double aux = dt;		
		_d_time.setText(aux.toString());
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		Double aux = dt;
		_d_time.setText(aux.toString());
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
		Double aux = dt;
		_d_time.setText(aux.toString());
	}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		// TODO Auto-generated method stub

	}
}