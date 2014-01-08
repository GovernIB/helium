/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * DTO amb informació d'una dada de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDadaDto {

	private String varCodi;
	private Object varValor;
	private Long campId;
	private CampTipusDto campTipus;
	private String campEtiqueta;
	private boolean campOcult;
	private boolean campMultiple;
	private Long agrupacioId;
	private String jbpmAction;
	private String observacions;

	private String text;
	private List<ExpedientDadaDto> multipleDades;
	private List<ExpedientDadaDto> registreDades;


	private List<String> outcomes;
	private List<DocumentTascaDto> documents;
	private List<ValidacioDto> validacions;
	private List<CampTascaDto> camps;
	private List<FirmaTascaDto> signatures;

	private Map<String, Object> variables;
	private Map<String, DocumentDto> varsDocuments;
	private Map<String, DocumentDto> varsDocumentsPerSignar;
	private Map<String, ParellaCodiValorDto> valorsDomini;
	private Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini;
	private Map<String, Object> varsComText;

	private Long tascaId;
	private boolean agafada;


	public List<String> getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}
	public List<DocumentTascaDto> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentTascaDto> documents) {
		this.documents = documents;
	}
	public List<ValidacioDto> getValidacions() {
		return validacions;
	}
	public void setValidacions(List<ValidacioDto> validacions) {
		this.validacions = validacions;
	}
	public List<CampTascaDto> getCamps() {
		return camps;
	}
	public void setCamps(List<CampTascaDto> camps) {
		this.camps = camps;
	}
	public List<FirmaTascaDto> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<FirmaTascaDto> signatures) {
		this.signatures = signatures;
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
	public Map<String, Object> getVarsComText() {
		return varsComText;
	}
	public void setVarsComText(Map<String, Object> varsComText) {
		this.varsComText = varsComText;
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
	private String error;

	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public String getVarCodi() {
		return varCodi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
	}
	public Object getVarValor() {
		return varValor;
	}
	public void setVarValor(Object varValor) {
		this.varValor = varValor;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public CampTipusDto getCampTipus() {
		return campTipus;
	}
	public void setCampTipus(CampTipusDto campTipus) {
		this.campTipus = campTipus;
	}
	public String getCampEtiqueta() {
		return campEtiqueta;
	}
	public void setCampEtiqueta(String campEtiqueta) {
		this.campEtiqueta = campEtiqueta;
	}
	public boolean isCampOcult() {
		return campOcult;
	}
	public void setCampOcult(boolean campOcult) {
		this.campOcult = campOcult;
	}
	public boolean isCampMultiple() {
		return campMultiple;
	}
	public void setCampMultiple(boolean campMultiple) {
		this.campMultiple = campMultiple;
	}
	public Long getAgrupacioId() {
		return agrupacioId;
	}
	public void setAgrupacioId(Long agrupacioId) {
		this.agrupacioId = agrupacioId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<ExpedientDadaDto> getMultipleDades() {
		return multipleDades;
	}
	public void setMultipleDades(List<ExpedientDadaDto> multipleDades) {
		this.multipleDades = multipleDades;
	}
	public List<ExpedientDadaDto> getRegistreDades() {
		return registreDades;
	}
	public void setRegistreDades(List<ExpedientDadaDto> registreDades) {
		this.registreDades = registreDades;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public Object[] getMultipleValor() {
		if (isCampMultiple())
			return (Object[])getVarValor();
		else
			return null;
	}

	public List<ExpedientDadaDto> getDadesRegistrePerTaula() {
		if (isCampMultiple())
			return getMultipleDades();
		List<ExpedientDadaDto> resposta = new ArrayList<ExpedientDadaDto>();
		ExpedientDadaDto dada = new ExpedientDadaDto();
		dada.setCampId(campId);
		dada.setVarCodi(varCodi);
		dada.setVarValor(varValor);
		dada.setCampId(campId);
		dada.setCampTipus(campTipus);
		dada.setCampEtiqueta(campEtiqueta);
		dada.setCampOcult(campOcult);
		dada.setCampMultiple(campMultiple);
		dada.setAgrupacioId(agrupacioId);
		dada.setRegistreDades(registreDades);
		resposta.add(dada);
		return resposta;
	}

	public boolean isFontExterna() {
		return CampTipusDto.SELECCIO.equals(campTipus) || CampTipusDto.SUGGEST.equals(campTipus);
	}
	public boolean isCampTipusRegistre() {
		return CampTipusDto.REGISTRE.equals(campTipus);
	}

}
