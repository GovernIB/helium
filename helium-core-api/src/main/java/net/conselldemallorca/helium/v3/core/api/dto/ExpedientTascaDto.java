/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * DTO amb informació d'una tasca de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTascaDto implements Comparable<ExpedientTascaDto> {

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
	public enum TipusTasca {
		ESTAT,
		FORM,
		SIGNATURA
	}
	
	public int getPrioritatOrdinal() {
		return prioritat.ordinal();
	}
	
	private String id;
	private String nom;
	private String missatgeInfo;
	private String missatgeWarn;
	private TipusTasca tipus;
	private String jbpmName;
	private String description;
	private String assignee;
	private Set<String> pooledActors;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;
	private String recursForm;
	private String formExtern;

	private boolean delegable;
	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private boolean validada;
	private boolean documentsComplet;
	private boolean signaturesComplet;

	private DefinicioProcesDto definicioProces;

	private Map<String, PersonaDto> personesMap;

	private List<String> outcomes;

	private Long tascaId;
	private boolean agafada;
	
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
	private boolean tramitacioMassiva;
	private List<String> transicions;
	
	private Long expedientId;
	private String expedientIdentificador;
	private String expedientTipusNom;

	private String processInstanceId;
	
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
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}
	public TipusTasca getTipus() {
		return tipus;
	}
	public void setTipus(TipusTasca tipus) {
		this.tipus = tipus;
	}
	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public Set<String> getPooledActors() {
		return pooledActors;
	}
	public void setPooledActors(Set<String> pooledActors) {
		this.pooledActors = pooledActors;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getRecursForm() {
		return recursForm;
	}
	public void setRecursForm(String recursForm) {
		this.recursForm = recursForm;
	}
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}
	public boolean isDelegable() {
		return delegable;
	}
	public void setDelegable(boolean delegable) {
		this.delegable = delegable;
	}
	public boolean isDelegada() {
		return delegada;
	}
	public void setDelegada(boolean delegada) {
		this.delegada = delegada;
	}
	public boolean isDelegacioOriginal() {
		return delegacioOriginal;
	}
	public void setDelegacioOriginal(boolean delegacioOriginal) {
		this.delegacioOriginal = delegacioOriginal;
	}
	public Date getDelegacioData() {
		return delegacioData;
	}
	public void setDelegacioData(Date delegacioData) {
		this.delegacioData = delegacioData;
	}
	public String getDelegacioComentari() {
		return delegacioComentari;
	}
	public void setDelegacioComentari(String delegacioComentari) {
		this.delegacioComentari = delegacioComentari;
	}
	public boolean isDelegacioSupervisada() {
		return delegacioSupervisada;
	}
	public void setDelegacioSupervisada(boolean delegacioSupervisada) {
		this.delegacioSupervisada = delegacioSupervisada;
	}
	public PersonaDto getDelegacioPersona() {
		return delegacioPersona;
	}
	public void setDelegacioPersona(PersonaDto delegacioPersona) {
		this.delegacioPersona = delegacioPersona;
	}
	public boolean isValidada() {
		return validada;
	}
	public void setValidada(boolean validada) {
		this.validada = validada;
	}
	public boolean isDocumentsComplet() {
		return documentsComplet;
	}
	public void setDocumentsComplet(boolean documentsComplet) {
		this.documentsComplet = documentsComplet;
	}
	public boolean isSignaturesComplet() {
		return signaturesComplet;
	}
	public void setSignaturesComplet(boolean signaturesComplet) {
		this.signaturesComplet = signaturesComplet;
	}
	public Map<String, PersonaDto> getPersonesMap() {
		return personesMap;
	}
	public void setPersonesMap(Map<String, PersonaDto> personesMap) {
		this.personesMap = personesMap;
	}
	public List<String> getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}

	public DefinicioProcesDto getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProcesDto definicioProces) {
		this.definicioProces = definicioProces;
	}
	public String getNomLimitat() {
		if (nom.length() > 100)
			return nom.substring(0, 100) + " (...)";
		else
			return nom;
	}

	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}
	public boolean isAgafada() {
		return agafada;
	}
	public void setAgafada(boolean agafada) {
		this.agafada = agafada;
	}
	
	@Override
	public int compareTo(ExpedientTascaDto aThat) {
	    if (this == aThat) return 0;
    	return this.getCreateTime().compareTo(aThat.getCreateTime());
	}
	
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}

}
