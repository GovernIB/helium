/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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

	private boolean delegable;
	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private DefinicioProcesDto definicioProces;
	private boolean oberta;
	private boolean cancelada;
	private boolean suspesa;
	private boolean transicioPerDefecte;
	private boolean validada;
	private List<String> transicions;
	private String formExtern;
	private boolean documentsComplet;
	private boolean signaturesComplet;

	private Map<String, Object> variables;
	private Map<String, DocumentDto> varsDocuments;
	private Map<String, DocumentDto> varsDocumentsPerSignar;
	private Map<String, ParellaCodiValorDto> valorsDomini;
	private Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini;
	private Map<String, Object> varsComText;

	private List<String> outcomes;
	private List<ValidacioDto> validacions;

	private Long tascaId;
	private boolean agafada;
	
	public boolean isDocumentsNotReadOnly() {
		for (DocumentTascaDto document: documents) {
			if (!document.isReadOnly())
				return true;
		}
		return false;
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
	public DefinicioProcesDto getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProcesDto definicioProces) {
		this.definicioProces = definicioProces;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	public Map<String, DocumentDto> getVarsDocuments() {
		return varsDocuments;
	}
	public void setVarsDocuments(Map<String, DocumentDto> varsDocuments) {
		this.varsDocuments = varsDocuments;
	}
	public Map<String, DocumentDto> getVarsDocumentsPerSignar() {
		return varsDocumentsPerSignar;
	}
	public void setVarsDocumentsPerSignar(Map<String, DocumentDto> varsDocumentsPerSignar) {
		this.varsDocumentsPerSignar = varsDocumentsPerSignar;
	}
	private List<CampTascaDto> camps;
	private List<DocumentTascaDto> documents;
	private List<FirmaTascaDto> signatures;
	
	private Long expedientId;
	private String expedientIdentificador;

	private String processInstanceId;

	public List<String> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}

	public List<ValidacioDto> getValidacions() {
		return validacions;
	}

	public void setValidacions(List<ValidacioDto> validacions) {
		this.validacions = validacions;
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

	public List<DocumentTascaDto> getDocumentsOrdenatsPerMostrarTasca() {
		List<DocumentTascaDto> resposta = new ArrayList<DocumentTascaDto>();
		// Afegeix primer els documents que ja estan adjuntats a la tasca
		for (DocumentTascaDto dt: documents) {
			if (varsDocuments.get(dt.getDocument().getCodi()) != null) {
				resposta.add(dt);
			}
		}
		// Despres afegeix els altres documents
		for (DocumentTascaDto dt: documents) {
			if (varsDocuments.get(dt.getDocument().getCodi()) == null) {
				resposta.add(dt);
			}
		}
		return resposta;
	}
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}
	public boolean isValidada() {
		return validada;
	}
	public void setValidada(boolean validada) {
		this.validada = validada;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<CampTascaDto> getCamps() {
		return camps;
	}
	public void setCamps(List<CampTascaDto> camps) {
		this.camps = camps;
	}
	public List<DocumentTascaDto> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentTascaDto> documents) {
		this.documents = documents;
	}
	public List<FirmaTascaDto> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<FirmaTascaDto> signatures) {
		this.signatures = signatures;
	}
	public Map<String, ParellaCodiValorDto> getValorsDomini() {
		return valorsDomini;
	}
	public void setValorsDomini(Map<String, ParellaCodiValorDto> valorsDomini) {
		this.valorsDomini = valorsDomini;
	}
	public Map<String, List<ParellaCodiValorDto>> getValorsMultiplesDomini() {
		return valorsMultiplesDomini;
	}
	public void setValorsMultiplesDomini(Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
		this.valorsMultiplesDomini = valorsMultiplesDomini;
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
	public Map<String, Object> getVarsComText() {
		return varsComText;
	}
	public void setVarsComText(Map<String, Object> varsComText) {
		this.varsComText = varsComText;
	}

	public PersonaDto getDelegacioPersona() {
		return delegacioPersona;
	}

	public void setDelegacioPersona(PersonaDto delegacioPersona) {
		this.delegacioPersona = delegacioPersona;
	}

}
