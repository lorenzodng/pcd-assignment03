package sequenziale;

import java.util.Optional;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    private static final int FRAMERATE = 60;
    private int framerate;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) { //per sempre, durante l'esecuzione...
            var t0 = System.currentTimeMillis(); //ricavo il tempo in ms
    		var boids = model.getBoids();

            //di volta in volta, vede chi c'è attorno, e configuro tutti a una stessa velocità
    		for (Boid boid : boids) {
                boid.updateVelocity(model);
            }

    		//poi aggiorno la posizione di tutti i boid
    		for (Boid boid : boids) {
                boid.updatePos(model);
            }

            var t1 = System.currentTimeMillis(); //ricavo il tempo in ms
            var dtElapsed = t1 - t0; //calcolo il tempo trascorso tra i due tempi, e quindi il tempo trascorso per compiere le operazioni di aggiornamento dei boid

    		if (view.isPresent()) {
            	view.get().update(framerate); //aggiorno il framerate

                var frameratePeriod = 1000/FRAMERATE; //trasformo il framerate in ms
                
                if (dtElapsed < frameratePeriod) { //se il tempo trascorso è minore del framerate in ms (buone prestazioni)...
                	try {
                		Thread.sleep(frameratePeriod - dtElapsed); //sospendo l'unico thread in esecuzione in modo da imporre il cap del framerate all'esecuzione
                	} catch (Exception ex) {}
                	framerate = FRAMERATE; //mantengo il framerate fisso sullo schermo
                } else {
                	framerate = (int) (1000/dtElapsed); //altrimenti aggiorno il framerate
                }
    		}
    	}
    }
}
