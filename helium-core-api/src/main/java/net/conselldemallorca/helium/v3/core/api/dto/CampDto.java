/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampDto extends HeretableDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String codi;
	private CampTipusDto tipus;
	private String etiqueta;
	private String observacions;

	private CampAgrupacioDto agrupacio;
	
	private boolean multiple;
	private boolean ocult;
	private boolean ignored;
	
	private DefinicioProcesDto definicioProces;
	private ExpedientTipusDto expedientTipus;
	
	// Dades consulta
	private EnumeracioDto enumeracio;
	private DominiDto domini;
	private ConsultaDto consulta;
	boolean dominiIntern;
	
	// Paràmetres del domini
	private String dominiIdentificador;
	private String dominiParams;
	private String dominiCampValor;
	private String dominiCampText;
	
	// Paràmetres de la consulta
	private String consultaParams;
	private String consultaCampText;
	private String consultaCampValor;
	
	// Dades de la acció
	private String defprocJbpmKey;
	private String jbpmAction;
	
	boolean dominiCacheText;

	/** Ordre dins la agrupació. */
	private Integer ordre;

	/** Per mostrar el número de validacions a la taula de variables. */
	private int validacioCount = 0;

	/** Per mostrar el número de membres de les variables de tipus registre la taula de variables. */
	private int campRegistreCount = 0;
	
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

	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
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
	public CampAgrupacioDto getAgrupacio() {
		return agrupacio;
	}
	public void setAgrupacio(CampAgrupacioDto agrupacio) {
		this.agrupacio = agrupacio;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	public EnumeracioDto getEnumeracio() {
		return enumeracio;
	}
	public void setEnumeracio(EnumeracioDto enumeracio) {
		this.enumeracio = enumeracio;
	}
	public DominiDto getDomini() {
		return domini;
	}
	public void setDomini(DominiDto domini) {
		this.domini = domini;
	}
	public ConsultaDto getConsulta() {
		return consulta;
	}
	public void setConsulta(ConsultaDto consulta) {
		this.consulta = consulta;
	}
	public boolean isDominiIntern() {
		return dominiIntern;
	}
	public void setDominiIntern(boolean dominiIntern) {
		this.dominiIntern = dominiIntern;
	}
	public String getDominiIdentificador() {
		return dominiIdentificador;
	}
	public void setDominiIdentificador(String dominiIdentificador) {
		this.dominiIdentificador = dominiIdentificador;
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
	public String getConsultaParams() {
		return consultaParams;
	}
	public void setConsultaParams(String consultaParams) {
		this.consultaParams = consultaParams;
	}
	public String getConsultaCampText() {
		return consultaCampText;
	}
	public void setConsultaCampText(String consultaCampText) {
		this.consultaCampText = consultaCampText;
	}
	public String getConsultaCampValor() {
		return consultaCampValor;
	}
	public void setConsultaCampValor(String consultaCampValor) {
		this.consultaCampValor = consultaCampValor;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}
	public boolean isDominiCacheText() {
		return dominiCacheText;
	}
	public void setDominiCacheText(boolean dominiCacheText) {
		this.dominiCacheText = dominiCacheText;
	}
	public int getValidacioCount() {
		return validacioCount;
	}
	public void setValidacioCount(int validacioCount) {
		this.validacioCount = validacioCount;
	}
	public int getCampRegistreCount() {
		return campRegistreCount;
	}
	public void setCampRegistreCount(int campRegistreCount) {
		this.campRegistreCount = campRegistreCount;
	}
	public Integer getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}
}
