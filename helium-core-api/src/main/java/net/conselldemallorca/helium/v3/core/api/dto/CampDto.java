/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private CampTipusDto tipus;
	private String etiqueta;
	private String observacions;
	
	private Long dominiId;
	private boolean multiple;
	
	private DefinicioProcesDto definicioProces;
	private List<ValidacioDto> validacions = new ArrayList<ValidacioDto>();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public CampTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(CampTipusDto tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public DefinicioProcesDto getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProcesDto definicioProces) {
		this.definicioProces = definicioProces;
	}

	@SuppressWarnings("rawtypes")
	public Class getJavaClass() {
		if (CampTipusDto.STRING.equals(tipus)) {
			return String.class;
		} else if (CampTipusDto.INTEGER.equals(tipus)) {
			return Long.class;
		} else if (CampTipusDto.FLOAT.equals(tipus)) {
			return Double.class;
		} else if (CampTipusDto.BOOLEAN.equals(tipus)) {
			return Boolean.class;
		} else if (CampTipusDto.TEXTAREA.equals(tipus)) {
			return String.class;
		} else if (CampTipusDto.DATE.equals(tipus)) {
			return Date.class;
		} else if (CampTipusDto.PRICE.equals(tipus)) {
			return BigDecimal.class;
		} else if (CampTipusDto.TERMINI.equals(tipus)) {
			return TerminiDto.class;
		} else if (CampTipusDto.REGISTRE.equals(tipus)) {
			return Object[].class;
		} else {
			return String.class;
		}
	}

	public static String getComText(
			CampTipusDto tipus,
			Object valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (tipus.equals(CampTipusDto.INTEGER)) {
				text = new DecimalFormat("#").format((Long)valor);
			} else if (tipus.equals(CampTipusDto.FLOAT)) {
				text = new DecimalFormat("#.##########").format((Double)valor);
			} else if (tipus.equals(CampTipusDto.PRICE)) {
				text = new DecimalFormat("#,##0.00").format((BigDecimal)valor);
			} else if (tipus.equals(CampTipusDto.DATE)) {
				text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
			} else if (tipus.equals(CampTipusDto.BOOLEAN)) {
				text = (((Boolean)valor).booleanValue()) ? "Si" : "No";
			} else if (tipus.equals(CampTipusDto.SELECCIO)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusDto.SUGGEST)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusDto.TERMINI)) {
				TerminiDto termini = ((TerminiDto)valor);
				text = termini.getAnys()+"/"+termini.getMesos()+"/"+termini.getDies();
			} else {
				text = valor.toString();
			}
			return text;
		} catch (Exception ex) {
			return valor.toString();
		}
	}
	
	public static Object getComObject(
			CampTipusDto tipus,
			String text) {
		if (text == null)
			return null;
		try {
			Object obj = null;
			if (tipus.equals(CampTipusDto.INTEGER)) {
				obj = new Long(text);
			} else if (tipus.equals(CampTipusDto.FLOAT)) {
				obj = new Double(text);
			} else if (tipus.equals(CampTipusDto.PRICE)) {
				obj = new BigDecimal(text);
			} else if (tipus.equals(CampTipusDto.DATE)) {
				obj = new SimpleDateFormat("dd/MM/yyyy").parse(text);
			} else if (tipus.equals(CampTipusDto.BOOLEAN)) {
				obj = new Boolean("S".equals(text));
			} else if (tipus.equals(CampTipusDto.SELECCIO)) {
				obj = text;
			} else if (tipus.equals(CampTipusDto.SUGGEST)) {
				obj = text;
			} else if (tipus.equals(CampTipusDto.TERMINI)) {
				String[] parts = text.split("/");
				TerminiDto termini = new TerminiDto();
				if (parts.length == 3) {
					termini.setAnys(new Integer(parts[0]));
					termini.setMesos(new Integer(parts[1]));
					termini.setDies(new Integer(parts[2]));
				}
				obj = termini;
			} else {
				obj = text;
			}
			return obj;
		} catch (Exception ex) {
			return text;
		}
	}
	public List<ValidacioDto> getValidacions() {
		return validacions;
	}
	public void setValidacions(List<ValidacioDto> validacions) {
		this.validacions = validacions;
	}
	public Long getDominiId() {
		return dominiId;
	}
	public void setDominiId(Long dominiId) {
		this.dominiId = dominiId;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
}
