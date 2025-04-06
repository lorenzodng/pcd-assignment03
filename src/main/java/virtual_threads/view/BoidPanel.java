package virtual_threads.view;

import virtual_threads.model.Boid;
import virtual_threads.model.BoidManager;
import javax.swing.*;
import java.awt.*;

public class BoidPanel extends JPanel {

	private BoidView view;
	private BoidManager model;
    private int framerate;

    public BoidPanel(BoidView view, BoidManager model) {
    	this.model = model;
    	this.view = view;
    }

    public void setFrameRate(int framerate) {
    	this.framerate = framerate;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        var w = view.getWidth();
        var h = view.getHeight();
        var envWidth = model.getWidth();
        var xScale = w/envWidth;
        var boids = model.getBoids();
        g.setColor(Color.BLUE);
        for (Boid boid : boids) {
        	var x = boid.getPos().x();
        	var y = boid.getPos().y();
        	int px = (int)(w/2 + (x * xScale));
        	int py = (int)(h/2 - (y * xScale));
            g.fillOval(px,py, 5, 5);
        }
        g.setColor(Color.BLACK);
        g.drawString("Framerate: " + framerate, 10, 25);
   }
}
