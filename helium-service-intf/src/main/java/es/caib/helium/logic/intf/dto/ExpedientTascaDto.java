/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import org.apache.commons.text.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DTO amb informació d'una tasca de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTascaDto extends ControlPermisosDto implements Comparable<ExpedientTascaDto> {

	public enum TascaTipusDto {
		ESTAT,
		FORM,
		SIGNATURA
	}
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
	private String jbpmName;
	private String description;
	private String assignee;
	private Set<String> pooledActors;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;
	private List<String> outcomes;

	private Long tascaId;
	private String tascaNom;
	private TascaTipusDto tascaTipus;
	private String tascaMissatgeInfo;
	private String tascaMissatgeWarn;
	private String tascaRecursForm;
	private String tascaFormExternCodi;
	private boolean tascaDelegable;
	private Boolean tascaTramitacioMassiva;
	private boolean tascaFinalitzacioSegonPla;
	
	private Date marcadaFinalitzar;
	private Date iniciFinalitzacio;
	private String errorFinalitzacio;

	private boolean validada;
	private boolean documentsComplet;
	private boolean signaturesComplet;
	private boolean agafada;

	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private Long expedientId;
	private Long expedientTipusId;
	private String expedientIdentificador;
	private String expedientNumero;

	private String processInstanceId;
	private String expedientTipusNom;
	private Long definicioProcesId;

	private PersonaDto responsable;
	private List<PersonaDto> responsables;

	private boolean assignadaUsuariActual;
	private boolean ambRepro;
	private boolean mostrarAgrupacions;


	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public boolean isAmbRepro() {
		return ambRepro;
	}
	public void setAmbRepro(boolean ambRepro) {
		this.ambRepro = ambRepro;
	}
	public boolean isMostrarAgrupacions() {
		return mostrarAgrupacions;
	}
	public void setMostrarAgrupacions(boolean mostrarAgrupacions) {
		this.mostrarAgrupacions = mostrarAgrupacions;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
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
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
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
	public List<String> getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}
	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}
	public String getTascaNom() {
		return tascaNom;
	}
	public void setTascaNom(String tascaNom) {
		this.tascaNom = tascaNom;
	}
	public TascaTipusDto getTascaTipus() {
		return tascaTipus;
	}
	public void setTascaTipus(TascaTipusDto tascaTipus) {
		this.tascaTipus = tascaTipus;
	}
	public String getTascaMissatgeInfo() {
		return tascaMissatgeInfo;
	}
	public void setTascaMissatgeInfo(String tascaMissatgeInfo) {
		this.tascaMissatgeInfo = tascaMissatgeInfo;
	}
	public String getTascaMissatgeWarn() {
		return tascaMissatgeWarn;
	}
	public void setTascaMissatgeWarn(String tascaMissatgeWarn) {
		this.tascaMissatgeWarn = tascaMissatgeWarn;
	}
	public String getTascaRecursForm() {
		return tascaRecursForm;
	}
	public void setTascaRecursForm(String tascaRecursForm) {
		this.tascaRecursForm = tascaRecursForm;
	}
	public String getTascaFormExternCodi() {
		return tascaFormExternCodi;
	}
	public void setTascaFormExternCodi(String tascaFormExternCodi) {
		this.tascaFormExternCodi = tascaFormExternCodi;
	}
	public boolean isTascaDelegable() {
		return tascaDelegable;
	}
	public void setTascaDelegable(boolean tascaDelegable) {
		this.tascaDelegable = tascaDelegable;
	}
	public boolean isTascaTramitacioMassiva() {
		return tascaTramitacioMassiva != null ? tascaTramitacioMassiva.booleanValue() : false;
	}
	public void setTascaTramitacioMassiva(Boolean tascaTramitacioMassiva) {
		this.tascaTramitacioMassiva = tascaTramitacioMassiva;
	}
	public boolean isTascaFinalitzacioSegonPla() {
		return tascaFinalitzacioSegonPla;
	}
	public void setTascaFinalitzacioSegonPla(boolean tascaFinalitzacioSegonPla) {
		this.tascaFinalitzacioSegonPla = tascaFinalitzacioSegonPla;
	}
	public Date getMarcadaFinalitzar() {
		return marcadaFinalitzar;
	}
	public String getMarcadaFinalitzarFormat() {
		if (marcadaFinalitzar != null) {
			return formatter.format(marcadaFinalitzar).toString();
		} else {
			return "";
		}
	}
	public void setMarcadaFinalitzar(Date marcadaFinalitzar) {
		this.marcadaFinalitzar = marcadaFinalitzar;
	}
	public Date getIniciFinalitzacio() {
		return iniciFinalitzacio;
	}
	public String getIniciFinalitzacioFormat() {
		if (iniciFinalitzacio != null) {
			return formatter.format(iniciFinalitzacio).toString();
		} else {
			return "";
		}
	}
	public void setIniciFinalitzacio(Date iniciFinalitzacio) {
		this.iniciFinalitzacio = iniciFinalitzacio;
	}
	public String getErrorFinalitzacio() {
		return errorFinalitzacio;
	}
	public void setErrorFinalitzacio(String errorFinalitzacio) {
		this.errorFinalitzacio = errorFinalitzacio;
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
	public boolean isAgafada() {
		return agafada;
	}
	public void setAgafada(boolean agafada) {
		this.agafada = agafada;
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
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}
	public PersonaDto getResponsable() {
		return responsable;
	}
	public void setResponsable(PersonaDto responsable) {
		this.responsable = responsable;
	}
	public List<PersonaDto> getResponsables() {
		return responsables;
	}
	public void setResponsables(List<PersonaDto> responsables) {
		this.responsables = responsables;
	}
	
	public Set<String> getResponsablesString() {
		Set<String> responsablesString = new HashSet<String>();
		if (this.getResponsables() != null)
			for (PersonaDto responsable : this.getResponsables())
				responsablesString.add(responsable.toString());
		return responsablesString;
	}

	
	public boolean isAssignadaUsuariActual() {
		return assignadaUsuariActual;
	}
	public void setAssignadaUsuariActual(boolean assignadaUsuariActual) {
		this.assignadaUsuariActual = assignadaUsuariActual;
	}

	public TascaEstatDto getEstat() {
		if (cancelled) {
			return TascaEstatDto.CANCELADA;
		} else if (suspended) {
			return TascaEstatDto.SUSPESA;
		} else if (completed) {
			return TascaEstatDto.FINALITZADA;
		} else {
			return TascaEstatDto.PENDENT;
		}
	}

	public TascaPrioritatDto getPrioritat() {
		if (priority <= -2) {
			return TascaPrioritatDto.MOLT_BAIXA;
		} else if (priority == -1) {
			return TascaPrioritatDto.BAIXA;
		} else if (priority == 1) {
			return TascaPrioritatDto.ALTA;
		} else if (priority >= 2) {
			return TascaPrioritatDto.MOLT_ALTA;
		} else {
			return TascaPrioritatDto.NORMAL;
		}
	}

	public String getResponsableString() {
		if ((responsables == null || responsables.isEmpty()) || agafada)
			return responsable == null ? "" : responsable.toString();
		return responsables.toString().replace("[", "").replace("]", "").replaceAll(", $", "");
	}

	@Override
	public int compareTo(ExpedientTascaDto aThat) {
	    if (this == aThat) return 0;
    	return this.getCreateTime().compareTo(aThat.getCreateTime());
	}

	public String getTitolLimitat() {
		if (titol.length() > 100)
			return titol.substring(0, 100) + " (...)";
		else
			return titol;
	}
	public String getTitolEscaped() {
		return StringEscapeUtils.escapeEcmaScript(titol);
	}
	public String getExpedientIdentificadorEscaped() {
		return StringEscapeUtils.escapeEcmaScript(expedientIdentificador);
	}
	
	private static final String PREFIX_TASCA_INICIAL = "TIE_";
	public boolean isInicial() {
		return id.startsWith(PREFIX_TASCA_INICIAL);
	}

	public boolean isTransicioPerDefecte() {
		if (outcomes != null && !outcomes.isEmpty() && outcomes.size() == 1) {
			return outcomes.get(0) == null || outcomes.get(0).isEmpty();
		} else {
			return false;
		}
	}

	public boolean isFormExtern() {
		return (tascaFormExternCodi != null && !tascaFormExternCodi.isEmpty());
	}

	private static final long serialVersionUID = 127420079220181365L;
}
