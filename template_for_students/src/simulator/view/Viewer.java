package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class Viewer extends JComponent implements SimulatorObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6393075307639932520L;
	// ...
	private int _centerX;
	private int _centerY;
	private double _scale;
	private List<Body> _bodies;
	private boolean _showHelp;
	private boolean _showVectors;
	
	Viewer(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	private void initGUI() {
        	// TODO add border with title
        	this.setSize(150, 100);
        	setLayout(new BorderLayout());
        	setBorder(BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(Color.black, 2),
        		"Viewer",
        		TitledBorder.LEFT, TitledBorder.TOP));
        	_bodies = new ArrayList<>();
        	_scale = 1.0;
        	_showHelp = true;
        	_showVectors = true;
        	addKeyListener(new KeyListener() {
        		// ...
        		@Override
        		public void keyPressed(KeyEvent e) {
        			switch (e.getKeyChar()) {
        				case '-':
        					_scale = _scale * 1.1;
        					repaint();
        					break;
        				case '+':
        					_scale = Math.max(1000.0, _scale / 1.1);
        					repaint();
        					break;
        				case '=':
        					autoScale();
        					repaint();
        					break;
        				case 'h':
        					_showHelp = !_showHelp;
        					repaint();
        					break;
        				case 'v':
        					_showVectors = !_showVectors;
        					repaint();
        					break;
        				default:
        		}
        			
        		setVisible(true);
        	}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	});
		addMouseListener(new MouseListener() {
		// ...
		@Override
		public void mouseEntered(MouseEvent e) {
		requestFocus();
		}
	
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
	
		}
	
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		});
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
	super.paintComponent(g);
        	// use �gr� to draw not �g� --- it gives nicer results
        	Graphics2D gr = (Graphics2D) g;
        	
        	gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        	RenderingHints.VALUE_ANTIALIAS_ON);
        	gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        	// calculate the center
        	_centerX = getWidth() / 2;
        	_centerY = getHeight() / 2;
        	
        	//draw a cross at center
        	gr.setColor(Color.blue);
        	gr.drawLine(_centerX, _centerY - 5, _centerX, _centerY + 5);
        	gr.drawLine(_centerX + 5, _centerY, _centerX - 5, _centerY);
        	
        	// TODO draw bodies (with vectors if _showVectors is true)
        	
        	for(Body bd : _bodies) {
        	    gr.setColor(Color.blue);
        	    int bd_X = _centerX + (int) (bd.getPosition().getX()/_scale);
		    int bd_Y = _centerY - (int) (bd.getPosition().getY()/ _scale);
        	    gr.fillOval(bd_X, bd_Y, 5, 5);
        	    gr.drawString(bd.getId(), bd_X, bd_Y);
        	    if(_showVectors) {
        		int v_X = bd_X - (int) (bd.getVelocity().getX()/ 1000);
        		int v_Y = bd_Y - (int) (bd.getVelocity().getY()/ 1000);

        		int f_X =  bd_X - (int) (bd.getForce().getX() / 10E26);
        		int f_Y = bd_Y - (int) (bd.getForce().getY() / 10E26);
        		//Dibujammos los vectores de la fuerza y la velocidad
        		this.drawLineWithArrow(gr, bd_X, bd_Y,v_X, v_Y, 4, 4, Color.GREEN, Color.GREEN);
        		this.drawLineWithArrow(gr, bd_X, bd_Y,f_X, f_Y, 4, 4, Color.RED, Color.RED);
        	    }
        	}
        	// TODO draw help if _showHelp is true
        	if(_showHelp) {
        	    String helpMsg = "h:toggle help, v: toggle vectors, +: zoom-in, -: zoom-out,"
        	    	+ "=: fit ";
        	    String scaling = "Scaling ratio: " + _scale;
        		
        	    gr.setColor(Color.red);
        	    gr.drawString(helpMsg, 25, 30);
        	    gr.drawString(scaling, 25, 45);
        	}
        	
        	this.setBackground(Color.white);
        	this.setVisible(true);
	}
	// other private/protected methods
	// ...
	private void autoScale() {
        	double max = 1.0;
        	for (Body b : _bodies) {
                	Vector2D p = b.getPosition();
                	max = Math.max(max, Math.abs(p.getX()));
                	max = Math.max(max, Math.abs(p.getY()));
        	}
        	double size = Math.max(1.0, Math.min(getWidth(), getHeight()));
		_scale = max > size ? 4.0 * max / size : 1.0;
	}
	
	// This method draws a line from (x1,y1) to (x2,y2) with an arrow.
	// The arrow is of height h and width w.
	// The last two arguments are the colors of the arrow and the line
	private void drawLineWithArrow(//
        	Graphics g, //
        	int x1, int y1, //
        	int x2, int y2, //
        	int w, int h, //
        	Color lineColor, Color arrowColor) {
        	int dx = x2 - x1, dy = y2 - y1;
        	double D = Math.sqrt(dx * dx + dy * dy);
        	double xm = D - w, xn = xm, ym = h, yn = -h, x;
        	double sin = dy / D, cos = dx / D;
        	x = xm * cos - ym * sin + x1;
        	ym = xm * sin + ym * cos + y1;
        	xm = x;
        	x = xn * cos - yn * sin + x1;
        	yn = xn * sin + yn * cos + y1;
        	xn = x;
        	int[] xpoints = { x2, (int) xm, (int) xn };
        	int[] ypoints = { y2, (int) ym, (int) yn };
        	g.setColor(lineColor);
        	g.drawLine(x1, y1, x2, y2);
        	g.setColor(arrowColor);
        	g.fillPolygon(xpoints, ypoints, 3);
	}
	// SimulatorObserver methods
	// ...
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
	    	this._bodies = bodies;
	    	this.autoScale();
	    	this.repaint();
		
	}
	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		// TODO Auto-generated method stub
	    	this._bodies = bodies;
	    	this.autoScale();
	    	this.repaint();
		
	}
	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		// TODO Auto-generated method stub
	    	this._bodies = bodies;
	    	this.autoScale();
	    	this.repaint();
		
	}
	@Override
	public void onAdvance(List<Body> bodies, double time) {
		// TODO Auto-generated method stub
		this._bodies = bodies;
	    	this.repaint();
	}
	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		// TODO Auto-generated method stub
		
	}}
