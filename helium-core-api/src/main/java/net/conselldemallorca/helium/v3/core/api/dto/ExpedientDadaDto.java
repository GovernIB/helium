/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;


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
	private boolean campMultiple;
	private boolean campOcult;
	private boolean required;
	private int ordre;
	private String jbpmAction;
	private String observacions;

	private String text;
	private List<ExpedientDadaDto> multipleDades;
	private List<ExpedientDadaDto> registreDades;
	private List<ValidacioDto> validacions;

	private String error;
	
	private Long agrupacioId;

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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
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
	public List<ValidacioDto> getValidacions() {
		return validacions;
	}
	public void setValidacions(List<ValidacioDto> validacions) {
		this.validacions = validacions;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public Object[] getMultipleValor() {
		if (isCampMultiple())
			return (Object[])getVarValor();
		else
			return null;
	}

	public List<ExpedientDadaDto> getDadesRegistrePerTaula() {
		if (isCampMultiple()) {
			if (getMultipleDades().size() == 0) {
				List<ExpedientDadaDto> filaBuida = new ArrayList<ExpedientDadaDto>();
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
				filaBuida.add(dada);
				return filaBuida;
			} else 
				return getMultipleDades();
		}
		
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
	
	public String getTextMultiple() {
		if (isCampMultiple()) {
			String[] textos = new String[multipleDades.size()];
			int i = 0;
			for (ExpedientDadaDto dada: multipleDades)
				textos[i++] = dada.getText();
			return Arrays.toString(textos);
			//return Arrays.toString(getMultipleValor());
		} else {
			return text;
		}
	}
	
	public  Class<?> getJavaClass() {
		if (CampTipusDto.STRING.equals(campTipus)) {
			return String.class;
		} else if (CampTipusDto.INTEGER.equals(campTipus)) {
			return Long.class;
		} else if (CampTipusDto.FLOAT.equals(campTipus)) {
			return Double.class;
		} else if (CampTipusDto.BOOLEAN.equals(campTipus)) {
			return Boolean.class;
		} else if (CampTipusDto.TEXTAREA.equals(campTipus)) {
			return String.class;
		} else if (CampTipusDto.DATE.equals(campTipus)) {
			return Date.class;
		} else if (CampTipusDto.PRICE.equals(campTipus)) {
			return BigDecimal.class;
		} else if (CampTipusDto.TERMINI.equals(campTipus)) {
			return TerminiDto.class;
		} else if (CampTipusDto.REGISTRE.equals(campTipus)) {
			return Object[].class;
		} else {
			return String.class;
		}
	}
}
