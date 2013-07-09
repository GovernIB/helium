/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * DTO amb informació d'una mesura temporal per a revisar
 * el rendiment de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MesuraTemporalDto {

	private String clau;
	private long darrera;
	private double mitja;
	private int numMesures;

	public String getClau() {
		return clau;
	}
	public void setClau(String clau) {
		this.clau = clau;
	}
	public long getDarrera() {
		return darrera;
	}
	public void setDarrera(long darrera) {
		this.darrera = darrera;
	}
	public double getMitja() {
		return mitja;
	}
	public void setMitja(double mitja) {
		this.mitja = mitja;
	}
	public int getNumMesures() {
		return numMesures;
	}
	public void setNumMesures(int numMesures) {
		this.numMesures = numMesures;
	}

}
