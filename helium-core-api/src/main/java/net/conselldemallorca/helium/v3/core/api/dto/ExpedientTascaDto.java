/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'una tasca de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTascaDto {

	public enum TascaEstatDto {
		PENDENT,
		PDT_DADES,
		PDT_DOCUMENTS,
		PDT_SIGNATURES,
		FINALITZADA,
		CANCELADA,
		SUSPESA
	}

	public enum TascaPrioritatDto {
		MOLT_ALTA,
		ALTA,
		NORMAL,
		BAIXA,
		MOLT_BAIXA
	}

	private String id;
	private String titol;
	private String descripcio;
	private TascaEstatDto estat;
	private Date dataLimit;
	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private PersonaDto responsable;
	private String responsableCodi;
	private List<PersonaDto> responsables;
	private TascaPrioritatDto prioritat;
	private boolean oberta;
	private boolean cancelada;
	private boolean suspesa;
	private boolean transicioPerDefecte;
	private List<String> transicions;

	private Long expedientId;
	private String expedientIdentificador;

	private String processInstanceId;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public TascaEstatDto getEstat() {
		return estat;
	}
	public void setEstat(TascaEstatDto estat) {
		this.estat = estat;
	}
	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public PersonaDto getResponsable() {
		return responsable;
	}
	public void setResponsable(PersonaDto responsable) {
		this.responsable = responsable;
	}
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public List<PersonaDto> getResponsables() {
		return responsables;
	}
	public void setResponsables(List<PersonaDto> responsables) {
		this.responsables = responsables;
	}
	public TascaPrioritatDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(TascaPrioritatDto prioritat) {
		this.prioritat = prioritat;
	}
	public boolean isOberta() {
		return oberta;
	}
	public void setOberta(boolean oberta) {
		this.oberta = oberta;
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
	public boolean isTransicioPerDefecte() {
		return transicioPerDefecte;
	}
	public void setTransicioPerDefecte(boolean transicioPerDefecte) {
		this.transicioPerDefecte = transicioPerDefecte;
	}
	public List<String> getTransicions() {
		return transicions;
	}
	public void setTransicions(List<String> transicions) {
		this.transicions = transicions;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getExpedientIdentificador() {
		return expedientIdentificador;
	}
	public void setExpedientIdentificador(String expedientIdentificador) {
		this.expedientIdentificador = expedientIdentificador;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public boolean isCompletada() {
		return !cancelada && dataFi != null;
	}

	public boolean isAssignadaPersonaAmbCodi(String personaCodi) {
		boolean trobada = false;
		if (getResponsable() != null)
			trobada = personaCodi.equals(getResponsable().getCodi());
		if (!trobada && getResponsables() != null) {
			for (PersonaDto resp: getResponsables()) {
				if (personaCodi.equals(resp.getCodi())) {
					trobada = true;
					break;
				}
			}
		}
		return trobada;
	}

}
