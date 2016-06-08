package net.conselldemallorca.helium.jbpm3.handlers.tipus;

public class TerminiInfo {

	private int anys = 0;
	private int mesos = 0;
	private int dies = 0;

	
	public TerminiInfo(int anys, int mesos, int dies) {
		super();
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
	}
	
	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}
	
}
