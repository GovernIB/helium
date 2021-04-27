/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



/**
 * DTO amb informació d'una dada de d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaDadaDto {

	private String varCodi;
	private Object varValor;

	private Long campId;
	private CampTipusDto campTipus;
	private String campEtiqueta;
	private boolean campMultiple;
	private boolean campOcult;
	private String[] campParams;

	private String jbpmAction;
	private String observacions;
	private String definicioProcesKey;
	
	private String dominiId;
	private String dominiParams;
	private String dominiCampValor;
	private String dominiCampText;

	private String text;
	private List<TascaDadaDto> multipleDades;
	private List<TascaDadaDto> registreDades;
	private List<ValidacioDto> validacions = new ArrayList<ValidacioDto>();

	private String error;

	private boolean readOnly;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private int ampleCols;
	private int buitCols;

	private boolean llistar;  // Si s'ha de llistar dins camp tipus registre
	
	CampAgrupacioDto agrupacio;
	

	public TascaDadaDto(String codi, CampTipusDto tipus, String etiqueta) {
		this.varCodi = codi;
		this.campTipus = tipus;
		this.campEtiqueta = etiqueta;
	}

	public TascaDadaDto() {
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
	public boolean isCampMultiple() {
		return campMultiple;
	}
	public void setCampMultiple(boolean campMultiple) {
		this.campMultiple = campMultiple;
	}
	public boolean isCampOcult() {
		return isReadOnly() ? false : campOcult;
	}
	public void setCampOcult(boolean campOcult) {
		this.campOcult = campOcult;
	}
	public String[] getCampParams() {
		return campParams;
	}
	public void setCampParams(String[] campParams) {
		this.campParams = campParams;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isReadFrom() {
		return readFrom;
	}
	public void setReadFrom(boolean readFrom) {
		this.readFrom = readFrom;
	}
	public boolean isWriteTo() {
		return writeTo;
	}
	public void setWriteTo(boolean writeTo) {
		this.writeTo = writeTo;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public int getAmpleCols() {
		return ampleCols;
	}

	public void setAmpleCols(int ampleCols) {
		this.ampleCols = ampleCols;
	}

	public int getBuitCols() {
		return buitCols;
	}

	public void setBuitCols(int buitCols) {
		this.buitCols = buitCols;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<TascaDadaDto> getMultipleDades() {
		return multipleDades;
	}
	public void setMultipleDades(List<TascaDadaDto> multipleDades) {
		this.multipleDades = multipleDades;
	}
	public List<TascaDadaDto> getRegistreDades() {
		return registreDades;
	}
	public void setRegistreDades(List<TascaDadaDto> registreDades) {
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
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}
	
	public CampAgrupacioDto getAgrupacio() {
		return agrupacio;
	}

	public void setAgrupacio(CampAgrupacioDto agrupacio) {
		this.agrupacio = agrupacio;
	}

	public Object[] getMultipleValor() {
		if (isCampMultiple())
			return (Object[])getVarValor();
		else
			return null;
	}

	public List<TascaDadaDto> getDadesRegistrePerTaula() {
		if (isCampMultiple())
			return getMultipleDades();
		List<TascaDadaDto> resposta = new ArrayList<TascaDadaDto>();
		TascaDadaDto dada = new TascaDadaDto();
		dada.setCampId(campId);
		dada.setVarCodi(varCodi);
		dada.setVarValor(varValor);
		dada.setCampTipus(campTipus);
		dada.setCampEtiqueta(campEtiqueta);
		dada.setCampMultiple(campMultiple);
		dada.setCampOcult(campOcult);
		dada.setReadOnly(readOnly);
		dada.setReadFrom(readFrom);
		dada.setWriteTo(writeTo);
		dada.setRequired(required);
		dada.setRegistreDades(registreDades);
		dada.setAmpleCols(ampleCols);
		dada.setBuitCols(buitCols);
		resposta.add(dada);
		return resposta;
	}

	public String getTextMultiple() {
		return getMultipleComText(false);
	}

	public String getPlantillaText() {
		return (varValor != null) ? getText() : null;
	}
	public String getPlantillaTextMultiple() {
		return getMultipleComText(true);
	}

	public String getJavaClassMultiple() {
		if (isCampMultiple()) {
			String[] textos = new String[multipleDades.size()];
			int i = 0;
			for (TascaDadaDto dadaMultiple: multipleDades) {
				if (isCampTipusRegistre()) {
					String[] regs = new String[dadaMultiple.getRegistreDades().size()];
					int j = 0;
					for (TascaDadaDto dadaRegistre: dadaMultiple.getRegistreDades()) {
						regs[j++] = getJavaClassFromVar(dadaRegistre);
					}
					textos[i++] = Arrays.toString(regs);
				} else {
					textos[i++] = getJavaClassFromVar(dadaMultiple);
				}
			}
			return Arrays.toString(textos);
		} else if (isCampTipusRegistre()) {
			String[] regs = new String[registreDades.size()];
			int i = 0;
			for (TascaDadaDto dadaRegistre: registreDades) {
				regs[i++] = getJavaClassFromVar(dadaRegistre);
			}
			return Arrays.toString(regs);
		} else {
			return getJavaClassFromVar(this);
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
			return String[].class;
		} else if (CampTipusDto.REGISTRE.equals(campTipus)) {
			return Object[].class;
		} else {
			return String.class;
		}
	}

	public boolean isFontExterna() {
		return CampTipusDto.SELECCIO.equals(campTipus) || CampTipusDto.SUGGEST.equals(campTipus);
	}
	public boolean isCampTipusRegistre() {
		return CampTipusDto.REGISTRE.equals(campTipus);
	}

	public String getDefinicioProcesKey() {
		return definicioProcesKey;
	}

	public void setDefinicioProcesKey(String definicioProcesKey) {
		this.definicioProcesKey = definicioProcesKey;
	}

	public String getDominiId() {
		return dominiId;
	}

	public void setDominiId(String dominiId) {
		this.dominiId = dominiId;
	}

	public String getDominiParams() {
		return dominiParams;
	}

	public void setDominiParams(String dominiParams) {
		this.dominiParams = dominiParams;
	}

	public String getDominiCampValor() {
		return dominiCampValor;
	}

	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
	}

	public String getDominiCampText() {
		return dominiCampText;
	}

	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}

	public String getCampParamsConcatenats() {
		if (campParams == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < campParams.length; i++) {
			sb.append(campParams[i]);
			if (i < campParams.length - 1)
				sb.append(",");
		}
		return sb.toString();
	}



	private String getMultipleComText(boolean plantilla) {
		if (isCampMultiple()) {
			String[] textos = new String[multipleDades.size()];
			int i = 0;
			for (TascaDadaDto dadaMultiple: multipleDades) {
				if (isCampTipusRegistre()) {
					String[] regs = new String[dadaMultiple.getRegistreDades().size()];
					int j = 0;
					for (TascaDadaDto dadaRegistre: dadaMultiple.getRegistreDades()) {
						if (plantilla)
							regs[j++] = dadaRegistre.getPlantillaText();
						else
							regs[j++] = dadaRegistre.getText();
					}
					textos[i++] = Arrays.toString(regs);
				} else {
					if (plantilla)
						textos[i++] = dadaMultiple.getPlantillaText();
					else
						textos[i++] = dadaMultiple.getText();
				}
			}
			return Arrays.toString(textos);
		} else if (isCampTipusRegistre()) {
			String[] regs = new String[registreDades.size()];
			int i = 0;
			for (TascaDadaDto dadaRegistre: registreDades) {
				if (plantilla)
					regs[i++] = dadaRegistre.getPlantillaText();
				else
					regs[i++] = dadaRegistre.getText();
			}
			return Arrays.toString(regs);
		} else {
			return text;
		}
	}

	private String getJavaClassFromVar(TascaDadaDto tascaDada) {
		if (tascaDada.getVarValor() == null) {
			return null;
		}
		return tascaDada.getVarValor().getClass().getName();
	}

}
