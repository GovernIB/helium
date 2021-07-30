package net.conselldemallorca.helium.webapp.exportacio;

import java.util.Date;

public class TascaExportacio {

	private Long id;
	private Long expedientId;
	private String nom;
	private String titol;
	private boolean afagada;
	private boolean cancelada;
	private boolean suspesa;
	private boolean completada;
	private boolean assignada;
	private boolean marcadaFinalitzar;
	private boolean errorFinalitzacio;
	private Date dataFins;
	private Date dataFi;
	private Date iniciFinalitzacio;
	private Date dataCreacio;
	private String usuariAssignat;
	private String grupAssignat;
	//private List<ResponsableDto> responsables;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public boolean isAfagada() {
		return afagada;
	}
	public void setAfagada(boolean afagada) {
		this.afagada = afagada;
	}
	public boolean isCancelada() {
		return cancelada;
	}
	public void setCancelada(boolean cancelada) {
		this.cancelada = cancelada;
	}
	public boolean isSuspesa() {
		return suspesa;
	}
	public void setSuspesa(boolean suspesa) {
		this.suspesa = suspesa;
	}
	public boolean isCompletada() {
		return completada;
	}
	public void setCompletada(boolean completada) {
		this.completada = completada;
	}
	public boolean isAssignada() {
		return assignada;
	}
	public void setAssignada(boolean assignada) {
		this.assignada = assignada;
	}
	public boolean isMarcadaFinalitzar() {
		return marcadaFinalitzar;
	}
	public void setMarcadaFinalitzar(boolean marcadaFinalitzar) {
		this.marcadaFinalitzar = marcadaFinalitzar;
	}
	public boolean isErrorFinalitzacio() {
		return errorFinalitzacio;
	}
	public void setErrorFinalitzacio(boolean errorFinalitzacio) {
		this.errorFinalitzacio = errorFinalitzacio;
	}
	public Date getDataFins() {
		return dataFins;
	}
	public void setDataFins(Date dataFins) {
		this.dataFins = dataFins;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Date getIniciFinalitzacio() {
		return iniciFinalitzacio;
	}
	public void setIniciFinalitzacio(Date iniciFinalitzacio) {
		this.iniciFinalitzacio = iniciFinalitzacio;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public String getUsuariAssignat() {
		return usuariAssignat;
	}
	public void setUsuariAssignat(String usuariAssignat) {
		this.usuariAssignat = usuariAssignat;
	}
	public String getGrupAssignat() {
		return grupAssignat;
	}
	public void setGrupAssignat(String grupAssignat) {
		this.grupAssignat = grupAssignat;
	}
}
