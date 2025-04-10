/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package sequenziale;

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
//rappresenta un vettore in uno spazio bidimensionale
public record V2d(double x, double y) { //"record" indica che le variabili non possono essere modificate una volta definite

    //calcola un nuovo vettore
    public V2d sum(V2d v){
        return new V2d(x + v.x,y + v.y); //restituisce un nuovo vettore sommando le coordinate del vettore attuale con quelle di un nuovo vettore
    }

    //calcola la lunghezza del vettore intesa come distanza dall'origine
    public double abs(){
        return (double)Math.sqrt(x*x+y*y);
    }

    //calcola un vettore proporzionato (ovvero ridotto) in funzione dei valori originali
    public V2d getNormalized(){
        double module=(double)Math.sqrt(x*x+y*y);
        return new V2d(x/module,y/module);
    }

    //calcola un nuovo vettore
    public V2d mul(double fact){
        return new V2d(x*fact,y*fact); //restituisce un nuovo vettore moltiplicando le coordinate del vettore attuale con quelle di un nuovo vettore
    }

    public String toString(){
        return "V2d("+x+","+y+")";
    }

}
