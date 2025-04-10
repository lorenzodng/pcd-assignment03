package tasks.model;

public class Flag {

	private boolean flag;
	
	public void reset() {
		flag = false;
	}
	
	public void set() {
		flag = true;
	}
	
	public boolean isSet() {
		return flag;
	}
}
