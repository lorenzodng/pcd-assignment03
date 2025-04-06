package threads.main;

import threads.controller.BoidController;
import threads.model.BoidManager;
import threads.model.Flag;
import threads.view.BoidView;

public class Simulation {

	static final double SEPARATION_WEIGHT = 1.0;
    static final double ALIGNMENT_WEIGHT = 1.0;
    static final double COHESION_WEIGHT = 1.0;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;
	static final int SCREEN_WIDTH = 900;
	static final int SCREEN_HEIGHT = 700;
	public static final int FRAMERATE = 60;

	public static void main(String[] args) {

    	var manager = new BoidManager(SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT, MAX_SPEED, PERCEPTION_RADIUS, AVOID_RADIUS);
		Flag flag= new Flag();
		BoidController controller = new BoidController(flag);
		BoidView view= new BoidView(manager, controller, SCREEN_WIDTH, SCREEN_HEIGHT);
		view.display();
    }
}
