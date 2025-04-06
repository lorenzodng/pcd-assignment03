package threads.model;

public class Barrier {

	private int numberOfArrived;
	private final int numberTotal;
	private int phase= 0;

	public Barrier(int numberTotal) {
		this.numberOfArrived= 0;
		this.numberTotal = numberTotal;
	}

	public synchronized void hitAndWaitAll() throws InterruptedException {
		int currentPhase= phase;
		numberOfArrived++;
		if(numberOfArrived == numberTotal){
			numberOfArrived= 0;
			phase++;
			notifyAll();
		} else {
			while (currentPhase == phase) {
				wait();
			}
		}
	}
}
