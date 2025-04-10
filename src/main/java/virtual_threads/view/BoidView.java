package virtual_threads.view;

import virtual_threads.controller.BoidController;
import virtual_threads.model.BoidManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

public class BoidView extends JFrame implements ChangeListener {

    private final BoidPanel boidsPanel;
	private final JSlider cohesionSlider, separationSlider, alignmentSlider;
	private final JSpinner boidsSpinner;
	private final BoidManager boidManager;
	private final JButton startButton, stopButton, resetButton;
	private final int width, height;
	private final BoidController controller;
	private final JFrame frame;

	public BoidView(BoidManager boidManager, BoidController controller, int width, int height) {
		this.boidManager = boidManager;
		this.controller= controller;
		this.width = width;
		this.height = height;
        frame = new JFrame("Boids Simulation");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);
        boidsPanel = new BoidPanel(this, boidManager);
		cp.add(BorderLayout.CENTER, boidsPanel);
        JPanel slidersPanel = new JPanel();
        cohesionSlider = makeSlider();
        separationSlider = makeSlider();
        alignmentSlider = makeSlider();
        slidersPanel.add(new JLabel("Separation"));
        slidersPanel.add(separationSlider);
        slidersPanel.add(new JLabel("Alignment"));
        slidersPanel.add(alignmentSlider);
        slidersPanel.add(new JLabel("Cohesion"));
        slidersPanel.add(cohesionSlider);
		cp.add(BorderLayout.SOUTH, slidersPanel);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		boidsSpinner = new JSpinner(new SpinnerNumberModel(1000, 1, 5000, 1));
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		spinnerPanel.add(new JLabel("Boids:"));
		spinnerPanel.add(boidsSpinner);
		mainPanel.add(spinnerPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		resetButton = new JButton("Reset");
		mainPanel.add(buttonPanel);
		cp.add(BorderLayout.NORTH, mainPanel);
		startButton.addActionListener(this::startSimulation);
		stopButton.addActionListener(this::stopSimulation);
		resetButton.addActionListener(this::resetSimulation);
		stopButton.setEnabled(false);
		resetButton.setEnabled(false);
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(resetButton);
		frame.setContentPane(cp);
	}

	private JSlider makeSlider() {
		var slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);        
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Hashtable labelTable = new Hashtable<>();
		labelTable.put(0, new JLabel("0"));
		labelTable.put(10, new JLabel("1"));
		labelTable.put(20, new JLabel("2"));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
        slider.addChangeListener(this);
		return slider;
	}
	
	public void update(int frameRate) {
		SwingUtilities.invokeLater(() -> {
			boidsPanel.setFrameRate(frameRate);
			boidsPanel.repaint();
		});
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == separationSlider) {
			var val = separationSlider.getValue();
			boidManager.setSeparationWeight(0.1*val);
		} else if (e.getSource() == cohesionSlider) {
			var val = cohesionSlider.getValue();
			boidManager.setCohesionWeight(0.1*val);
		} else {
			var val = alignmentSlider.getValue();
			boidManager.setAlignmentWeight(0.1*val);
		}
	}

	private void startSimulation(ActionEvent e) {
		int nBoids = (int) boidsSpinner.getValue();
		controller.start(this, nBoids);
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		resetButton.setEnabled(false);
		boidsSpinner.setEnabled(false);
	}

	private void stopSimulation(ActionEvent e) {
		controller.stop();
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		resetButton.setEnabled(true);
		boidsSpinner.setEnabled(false);
	}

	private void resetSimulation(ActionEvent e) {
		controller.reset(getBoidManager());
		boidsPanel.repaint();
		boidsSpinner.setEnabled(true);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			frame.setVisible(true);
		});
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BoidManager getBoidManager() {
		return boidManager;
	}
}
