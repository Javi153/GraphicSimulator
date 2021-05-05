package simulator.view;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel
implements SimulatorObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7183028210207114767L;
	// ...
	private JLabel _currTime; // for current time
	private JLabel _currLaws; // for gravity laws
	private JLabel _numOfBodies; // for number of bodies
	
	StatusBar(Controller ctrl) {
		ctrl.addObserver(this);
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		// TODO complete the code to build the tool bar
		add(_currTime);
		add(new JSeparator(JSeparator.VERTICAL));
		add(_numOfBodies);
		add(new JSeparator(JSeparator.VERTICAL));
		add(_currLaws);
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) { //En los metodos del Observer modificaremos las etiquetas cuando se pueda haber
		// TODO Auto-generated method stub												  //modificado un atributo del simulador
		_currTime = new JLabel("Time: " + time);
		_numOfBodies = new JLabel("Bodies: " + bodies.size());
		_currLaws = new JLabel("Laws: " + fLawsDesc);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
		_currTime = new JLabel("Time: " + time);
		_numOfBodies = new JLabel("Bodies: " + bodies.size());
		_currLaws = new JLabel("Laws: " + fLawsDesc);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		// TODO Auto-generated method stub
		_numOfBodies = new JLabel("Bodies: " + bodies.size());
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		// TODO Auto-generated method stub
		_currTime = new JLabel("Time: " + time);
		_numOfBodies = new JLabel("Bodies: " + bodies.size());
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		// TODO Auto-generated method stub
		_currLaws = new JLabel("Laws: " + fLawsDesc);
	}
}
