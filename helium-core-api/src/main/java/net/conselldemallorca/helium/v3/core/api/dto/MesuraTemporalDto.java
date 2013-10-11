/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.LinkedList;


/**
 * DTO amb informació d'una mesura temporal per a revisar
 * el rendiment de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MesuraTemporalDto implements Comparable<MesuraTemporalDto> {

	private String clau;
	private String tipusExpedient;
	private String tasca;
	private String detall;
	private long darrera;
	private double mitja;
	private long minima;
	private long maxima;
	private long numMesures;
	private double periode;
	
	private LinkedList<IntervalEventDto> events;

	public String getClau() {
		return clau;
	}
	public void setClau(String clau) {
		this.clau = clau;
	}
	public String getTipusExpedient() {
		return tipusExpedient;
	}
	public void setTipusExpedient(String tipusExpedient) {
		this.tipusExpedient = tipusExpedient;
	}
	public String getTasca() {
		return tasca;
	}
	public void setTasca(String tasca) {
		this.tasca = tasca;
	}
	public String getDetall() {
		return detall;
	}
	public void setDetall(String detall) {
		this.detall = detall;
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
	public long getMinima() {
		return minima;
	}
	public void setMinima(long minima) {
		this.minima = minima;
	}
	public long getMaxima() {
		return maxima;
	}
	public void setMaxima(long maxima) {
		this.maxima = maxima;
	}
	public long getNumMesures() {
		return numMesures;
	}
	public void setNumMesures(long numMesures) {
		this.numMesures = numMesures;
	}
	public double getPeriode() {
		return periode;
	}
	public void setPeriode(double periode) {
		this.periode = periode;
	}
	public LinkedList<IntervalEventDto> getEvents() {
		return events;
	}
	public void setEvents(LinkedList<IntervalEventDto> events) {
		this.events = events;
	}
	
	public String getNom() {
		return (tipusExpedient == null ? "" : tipusExpedient + " - ") + 
				(tasca == null ? "" : tasca + " - ") + 
				clau + 
				(detall == null ? "" : " (" + detall + ")");
	}
	public String getNomTE() {
		return (tipusExpedient == null ? "" : tipusExpedient + " - ") + 
				clau + 
				(detall == null ? "" : " (" + detall + ")");
	}
	public int compareTo(MesuraTemporalDto o) {
		return Double.compare(o.mitja, this.mitja);
	}
	
}
