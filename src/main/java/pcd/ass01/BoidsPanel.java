package pcd.ass01;

import javax.swing.*;
import java.awt.*;

public class BoidsPanel extends JPanel {

	private BoidsView view;
	private BoidsModel model;
    private int framerate;

    public BoidsPanel(BoidsView view, BoidsModel model) {
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
        var envWidth = model.getWidth(); //larghezza dell'ambiente in cui i boid si muovono all'interno della finestra
        var xScale = w/envWidth; //adatto l'ambiente alla finestra
        var boids = model.getBoids();

        g.setColor(Color.BLUE);
        for (Boid boid : boids) {
        	var x = boid.getPos().x(); //ricavo la posizione del boid sull'asse x
        	var y = boid.getPos().y(); //ricavo la posizione del boid sull'asse y
        	int px = (int)(w/2 + (x * xScale)); //adatto la posizione sull'asse x all'ambiente all'interno della finestra
        	int py = (int)(h/2 - (y * xScale)); //adatto la posizione sull'asse y all'ambiente all'interno della finestra
            g.fillOval(px,py, 5, 5); //disegno un piccolo cerchio per rappresentare il boid alla posizione calcolata

        }

        g.setColor(Color.BLACK);
        g.drawString("Num. Boids: " + boids.size(), 10, 25);
        g.drawString("Framerate: " + framerate, 10, 40);
   }
}
