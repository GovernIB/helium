package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampDto implements Serializable {

	private Long id;
	private String codi;
	private CampTipusDto tipus;
	private String etiqueta;
	private String observacions;
	private String dominiId;
	private String dominiParams;
	private String dominiCampText;
	private String dominiCampValor;
	private String consultaParams;
	private String consultaCampText;
	private String consultaCampValor;
	
	private boolean dominiCacheText;
	private boolean dominiIntern;
	private String jbpmAction;
	private boolean multiple;
	private boolean ocult;
	private boolean isIgnored;


	private ConsultaDto consulta;
	private DominiDto domini;
	private EnumeracioDto enumeracio;
	private DefinicioProcesDto definicioProces;
	private CampAgrupacioDto agrupacio;

	private Set<CampTascaDto> campsTasca = new HashSet<CampTascaDto>();
	private Set<DocumentDto> documentDates = new HashSet<DocumentDto>();
	private List<ValidacioDto> validacions = new ArrayList<ValidacioDto>();

	private Set<CampRegistreDto> registrePares = new HashSet<CampRegistreDto>();
	private List<CampRegistreDto> registreMembres = new ArrayList<CampRegistreDto>();

	private Integer ordre;


	public CampDto() {}
	public CampDto(DefinicioProcesDto definicioProces, String codi, CampTipusDto tipus, String etiqueta) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
	}
	
	public String getCodiEtiqueta() {
		if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
			return etiqueta;
		else
			return codi + "/" + etiqueta;
	}

	public String getCodiPerInforme() {
		if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
			return codi.replace('$', '%');
		else
			return definicioProces.getJbpmKey() + "/" + codi;
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
			CampDto camp,
			Object valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (camp.getTipus().equals(CampTipusDto.INTEGER)) {
				text = new DecimalFormat("#").format((Long)valor);
			} else if (camp.getTipus().equals(CampTipusDto.FLOAT)) {
				text = new DecimalFormat("#.#").format((Double)valor);
			} else if (camp.getTipus().equals(CampTipusDto.PRICE)) {
				text = new DecimalFormat("#,##0.00").format((BigDecimal)valor);
			} else if (camp.getTipus().equals(CampTipusDto.DATE)) {
				text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
			} else if (camp.getTipus().equals(CampTipusDto.BOOLEAN)) {
				text = (((Boolean)valor).booleanValue()) ? "Si" : "No";
			} else if (camp.getTipus().equals(CampTipusDto.SELECCIO)) {
				text = valorDomini;
			} else if (camp.getTipus().equals(CampTipusDto.SUGGEST)) {
				text = valorDomini;
			} else if (camp.getTipus().equals(CampTipusDto.TERMINI)) {
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampDto other = (CampDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (definicioProces == null) {
			if (other.definicioProces != null)
				return false;
		} else if (!definicioProces.equals(other.definicioProces))
			return false;
		return true;
	}

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
	public String getDominiCampText() {
		return dominiCampText;
	}
	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}
	public String getDominiCampValor() {
		return dominiCampValor;
	}
	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
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
	public boolean isDominiCacheText() {
		return dominiCacheText;
	}
	public void setDominiCacheText(boolean dominiCacheText) {
		this.dominiCacheText = dominiCacheText;
	}
	public boolean isDominiIntern() {
		return dominiIntern;
	}
	public void setDominiIntern(boolean dominiIntern) {
		this.dominiIntern = dominiIntern;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
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
		return isIgnored;
	}
	public void setIgnored(boolean isIgnored) {
		this.isIgnored = isIgnored;
	}
	public ConsultaDto getConsulta() {
		return consulta;
	}
	public void setConsulta(ConsultaDto consulta) {
		this.consulta = consulta;
	}
	public DominiDto getDomini() {
		return domini;
	}
	public void setDomini(DominiDto domini) {
		this.domini = domini;
	}
	public EnumeracioDto getEnumeracio() {
		return enumeracio;
	}
	public void setEnumeracio(EnumeracioDto enumeracio) {
		this.enumeracio = enumeracio;
	}
	public DefinicioProcesDto getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProcesDto definicioProces) {
		this.definicioProces = definicioProces;
	}
	public CampAgrupacioDto getAgrupacio() {
		return agrupacio;
	}
	public void setAgrupacio(CampAgrupacioDto agrupacio) {
		this.agrupacio = agrupacio;
	}
	public Set<CampTascaDto> getCampsTasca() {
		return campsTasca;
	}
	public void setCampsTasca(Set<CampTascaDto> campsTasca) {
		this.campsTasca = campsTasca;
	}
	public Set<DocumentDto> getDocumentDates() {
		return documentDates;
	}
	public void setDocumentDates(Set<DocumentDto> documentDates) {
		this.documentDates = documentDates;
	}
	public List<ValidacioDto> getValidacions() {
		return validacions;
	}
	public void setValidacions(List<ValidacioDto> validacions) {
		this.validacions = validacions;
	}
	public Set<CampRegistreDto> getRegistrePares() {
		return registrePares;
	}
	public void setRegistrePares(Set<CampRegistreDto> registrePares) {
		this.registrePares = registrePares;
	}
	public List<CampRegistreDto> getRegistreMembres() {
		return registreMembres;
	}
	public void setRegistreMembres(List<CampRegistreDto> registreMembres) {
		this.registreMembres = registreMembres;
	}
	public Integer getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	private static final long serialVersionUID = 1L;

}
