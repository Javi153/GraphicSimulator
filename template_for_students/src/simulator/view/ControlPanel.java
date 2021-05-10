package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1696025726243841820L;
	// ...
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
				JFileChooser fileChooser = new JFileChooser("resources/examples");
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
					catch(FileNotFoundException ex) {
						System.out.println(ex.getMessage());
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

				Frame parent = (Frame) SwingUtilities.getWindowAncestor(ControlPanel.this);

				List<JSONObject> forces = _ctrl.getForceLawsInfo();

				ChooseForceDialog chooseForce = new ChooseForceDialog(parent, forces);


				int status = chooseForce.open();

				JSONObject info = null;
				if(status == 0) {
					JOptionPane.showMessageDialog(ControlPanel.this, "You have selected cancel or an error has occurred");
				}
				else {
					info = chooseForce.getJSON();
					_ctrl.setForceLaws(info);
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
				_ctrl.setDeltaTime(Double.parseDouble(_d_time.getText()));//Da 0 y no se por que
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
		_steps = new JSpinner(new SpinnerNumberModel(10000, 1, 1000000, 1));
		JLabel timeLabel = new JLabel("Delta-Time:");
		_d_time = new JTextField("2500.0");



		add(openButton);
		add(Box.createRigidArea(new Dimension(20, 0)));
		add(physicsButton);
		add(Box.createRigidArea(new Dimension(20, 0)));
		add(runButton);
		add(stopButton);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(stepsLabel);
		_steps.setMaximumSize(new Dimension(300, 50));
		add(_steps);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(timeLabel);
		_d_time.setPreferredSize(new Dimension(100, 50));
		_d_time.setMaximumSize(new Dimension(150, 50));
		add(_d_time);
		add(Box.createHorizontalGlue());
		add(exitButton);
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
				System.out.println(e.getMessage());		    

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
			//Activamos los botones(podriamos cambiarlo a un metodo aparte)
			openButton.setEnabled(true);
			physicsButton.setEnabled(true);
			runButton.setEnabled(true);
			exitButton.setEnabled(true);
		}
	}
	// SimulatorObserver methods
	// ...

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		Double aux = dt;		
		_d_time = new JTextField(aux.toString());
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		Double aux = dt;
		_d_time = new JTextField(aux.toString());
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
		_d_time = new JTextField(aux.toString());
	}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		// TODO Auto-generated method stub

	}
}