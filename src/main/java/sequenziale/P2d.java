package sequenziale;

/**
 *
 * 2-dimensional point
 * objects are completely state-less
 *
 */
//rappresenta un punto in uno spazio bidimensionale
public record P2d(double x, double y) { //"record" indica che le variabili non possono essere modificate una volta definite

    //calcola un nuovo punto
    public P2d sum(V2d v){
        return new P2d(x + v.x(),y + v.y()); //restituisce un nuovo punto sommando le coordinate correnti con quelle di un vettore V2d
    }

    //calcola la distanza tra due punti nello spazio
    public double distance(P2d p) {
    	double dx = p.x - x;
    	double dy = p.y - y;
    	return Math.sqrt(dx*dx + dy*dy);
    }
    
    public String toString(){
        return "P2d("+x+","+y+")";
    }

}
