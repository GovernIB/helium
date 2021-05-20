/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'una dada de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ExpedientDadaDto {

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

	private String text;
	private List<ExpedientDadaDto> multipleDades;
	private List<ExpedientDadaDto> registreDades;
	private List<ValidacioDto> validacions;

	private String error;

	private boolean required; // Si es obligatori a dins camp tipus registre
	private boolean llistar;  // Si s'ha de llistar dins camp tipus registre
	private int ordre;
	private Long agrupacioId;


	//getter per a obtenir nom de la classe del valor de la dada
	public String getVarValorClass() {
		return varValor.getClass().getName();
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
		return getMultipleComText(false);
	}

	public String getPlantillaText() {
		return (varValor != null) ? getText() : null;
	}
	public String getPlantillaTextMultiple() {
		return getMultipleComText(true);
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

	private String getMultipleComText(boolean plantilla) {
		if (isCampMultiple()) {
			String[] textos = new String[multipleDades.size()];
			int i = 0;
			for (ExpedientDadaDto dadaMultiple: multipleDades) {
				if (isCampTipusRegistre()) {
					String[] regs = new String[dadaMultiple.getRegistreDades().size()];
					int j = 0;
					for (ExpedientDadaDto dadaRegistre: dadaMultiple.getRegistreDades()) {
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
			for (ExpedientDadaDto dadaRegistre: registreDades) {
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

}
