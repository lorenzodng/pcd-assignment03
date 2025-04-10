package sequenziale;

import java.util.ArrayList;
import java.util.List;

public class Boid {

    private P2d pos;
    private V2d vel;

    public Boid(P2d pos, V2d vel) {
    	this.pos = pos;
    	this.vel = vel;
    }
    
    public P2d getPos() {
    	return pos;
    }

    public V2d getVel() {
    	return vel;
    }

    public void updateVelocity(BoidsModel model) {
    	List<Boid> nearbyBoids = getNearbyBoids(model); //ricavo i boid che, distribuiti sullo spazio in modo casuale, sono vicini tra loro

        //calcolo i parametri di velocità sui boid vicini tra loro
    	V2d separation = calculateSeparation(nearbyBoids, model);
    	V2d alignment = calculateAlignment(nearbyBoids, model);
    	V2d cohesion = calculateCohesion(nearbyBoids, model);

        //ricavo un vettore di velocità media di tutti i boid vicini
    	vel = vel.sum(alignment.mul(model.getAlignmentWeight()))
    			.sum(separation.mul(model.getSeparationWeight()))
    			.sum(cohesion.mul(model.getCohesionWeight())); //
        

        double speed = vel.abs();

        //se la velocità supera la massima velocità impostata, allora riporto la velocità al valore massimo
        if (speed > model.getMaxSpeed()) {
            vel = vel.getNormalized().mul(model.getMaxSpeed());
        }
    }

    public void updatePos(BoidsModel model) {
        pos = pos.sum(vel); //ricavo un vettore di posizione media di tutti i boid vicini

        if (pos.x() < model.getMinX()) //se i boid escono dal lato sinistro della finestra
            pos = pos.sum(new V2d(model.getWidth(), 0)); //vengono riportati sul lato destro

        if (pos.x() >= model.getMaxX()) //se i boid escono dal lato destro della finestra
            pos = pos.sum(new V2d(-model.getWidth(), 0)); //vengono riportati sul lato sinistro

        if (pos.y() < model.getMinY()) //se i boid escono dal lato inferiore della finestra
            pos = pos.sum(new V2d(0, model.getHeight())); //vengono riportati sul lato superiore

        if (pos.y() >= model.getMaxY()) //se i boid escono dal lato superiore della finestra
            pos = pos.sum(new V2d(0, -model.getHeight())); //vengono riportati sul lato inferiore
    }

    //il metodo restituisce una lista di boid che si trovano entro una certa distanza dal boid corrente
    private List<Boid> getNearbyBoids(BoidsModel model) {
    	var list = new ArrayList<Boid>();
        for (Boid other : model.getBoids()) { //per ogni boid della lista...
        	if (other != this) { //che non sia quello corrente
        		P2d otherPos = other.getPos(); //ricavo la posizione del boid
        		double distance = pos.distance(otherPos); //calcolo la distanza del boid rispetto a quello corrente
        		if (distance < model.getPerceptionRadius()) { //se rientra in un certo raggio di distanza...
        			list.add(other); //lo aggiungo alla lista dei boid vicini
        		}
        	}
        }
        return list;
    }

    //il metodo calcola il valore medio di velocità tenendo conto dei boid tra di loro vicini
    private V2d calculateAlignment(List<Boid> nearbyBoids, BoidsModel model) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) { //se ci sono boid vicini...
	        for (Boid other : nearbyBoids) { //per ogni boid vicino...
	        	V2d otherVel = other.getVel(); //ricavo la velocità
	            avgVx += otherVel.x(); //calcolo la velocità totale sull'asse x
	            avgVy += otherVel.y(); //calcolo la velocità totale sull'asse y
	        }	        
	        avgVx /= nearbyBoids.size(); //calcolo la media delle velocità sull'asse x
	        avgVy /= nearbyBoids.size(); //calcolo la media delle velocità sull'asse y
	        return new V2d(avgVx - vel.x(), avgVy - vel.y()).getNormalized(); //restituisco un nuovo vettore proporzionato
        } else {
        	return new V2d(0, 0); //altrimenti ritorno un vettore nullo
        }
    }

    //il metodo calcola il valore medio di coesione (unione) tenendo conto dei boid tra di loro vicini
    private V2d calculateCohesion(List<Boid> nearbyBoids, BoidsModel model) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) { //se ci sono boid vicini...
	        for (Boid other: nearbyBoids) { //per ogni boid vicino...
	        	P2d otherPos = other.getPos(); //ricavo la posizione
	            centerX += otherPos.x(); //calcolo la posizione totale sull'asse x
	            centerY += otherPos.y(); //calcolo la posizione totale sull'asse y
	        }
            centerX /= nearbyBoids.size(); //calcolo la media della posizione sull'asse x
            centerY /= nearbyBoids.size(); //calcolo la media della posizione sull'asse y
            return new V2d(centerX - pos.x(), centerY - pos.y()).getNormalized(); //restituisco un nuovo vettore proporizionato
        } else {
        	return new V2d(0, 0); //altrimenti ritorno un vettore nullo
        }
    }

    //il metodo calcola il valore medio di sparpagliamento tenendo conto dei boid tra di loro vicini
    private V2d calculateSeparation(List<Boid> nearbyBoids, BoidsModel model) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (Boid other: nearbyBoids) { //per ogni boid vicino...
        	P2d otherPos = other.getPos(); //ricavo la posizione
    	    double distance = pos.distance(otherPos); //calcolo la distanza tra il boid e quello attuale
    	    if (distance < model.getAvoidRadius()) { //se la distanza è inferiore al raggio di evitamento...
    	    	dx += pos.x() - otherPos.x(); //memorizzo la differenza sull'asse x tra la posizione del boid attuale e quella dell'altro boid
    	    	dy += pos.y() - otherPos.y(); //memorizzo la differenza sull'asse y tra la posizione del boid attuale e quella dell'altro boid
    	    	count++; //incremento il numero di boid dentro il raggio di evitamento
    	    }
    	}
        if (count > 0) { //se sono stati trovati boid nelle vicinanze...
            dx /= count; //calcolo la media delle differenze tra le posizioni sull'asse x
            dy /= count; //calcolo la media delle differenze tra le posizioni sull'asse y
            return new V2d(dx, dy).getNormalized(); //restituisco un nuovo vettore proporzionato
        } else {
        	return new V2d(0, 0); //altrimenti ritorno un vettore vuoto
        }
    }
}
