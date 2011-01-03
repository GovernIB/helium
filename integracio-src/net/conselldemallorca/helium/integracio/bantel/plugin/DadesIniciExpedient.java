/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dades necessàries per iniciar un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DadesIniciExpedient {

	private String entornCodi;
	private String tipusCodi;
	private String titol;
	private String numero;
	private String registreNumero;
	private Date registreData;
	private boolean avisosHabilitats = false;
	private String avisosEmail;
	private String avisosMobil;
	private boolean notificacioTelematicaHabilitada = false;
	private String transitionName;

	private Map<String, Object> dadesInicials;
	private Map<String, DadesDocument> documentsInicials;
	private List<DadesDocument> documentsAdjunts;



	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public String getTipusCodi() {
		return tipusCodi;
	}
	public void setTipusCodi(String tipusCodi) {
		this.tipusCodi = tipusCodi;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public boolean isAvisosHabilitats() {
		return avisosHabilitats;
	}
	public void setAvisosHabilitats(boolean avisosHabilitats) {
		this.avisosHabilitats = avisosHabilitats;
	}
	public String getAvisosEmail() {
		return avisosEmail;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}
	public String getAvisosMobil() {
		return avisosMobil;
	}
	public void setAvisosMobil(String avisosMobil) {
		this.avisosMobil = avisosMobil;
	}
	public boolean isNotificacioTelematicaHabilitada() {
		return notificacioTelematicaHabilitada;
	}
	public void setNotificacioTelematicaHabilitada(
			boolean notificacioTelematicaHabilitada) {
		this.notificacioTelematicaHabilitada = notificacioTelematicaHabilitada;
	}
	public String getTransitionName() {
		return transitionName;
	}
	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}
	public Map<String, Object> getDadesInicials() {
		return dadesInicials;
	}
	public void setDadesInicials(Map<String, Object> dadesInicials) {
		this.dadesInicials = dadesInicials;
	}
	public Map<String, DadesDocument> getDocumentsInicials() {
		return documentsInicials;
	}
	public void setDocumentsInicials(Map<String, DadesDocument> documentsInicials) {
		this.documentsInicials = documentsInicials;
	}
	public List<DadesDocument> getDocumentsAdjunts() {
		return documentsAdjunts;
	}
	public void setDocumentsAdjunts(List<DadesDocument> documentsAdjunts) {
		this.documentsAdjunts = documentsAdjunts;
	}

}
