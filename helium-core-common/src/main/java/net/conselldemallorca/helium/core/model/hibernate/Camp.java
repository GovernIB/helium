/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.core.common.ExpedientCamps;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_camp",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id", "expedient_tipus_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_camp",
		indexes = {
				@Index(name = "hel_camp_defproc_i", columnNames = {"definicio_proces_id"}),
				@Index(name = "hel_camp_exptip_i", columnNames = {"expedient_tipus_id"}),
				@Index(name = "hel_camp_agrup_i", columnNames = {"camp_agrupacio_id"}),
				@Index(name = "hel_camp_domini_i", columnNames = {"domini_id"}),
				@Index(name = "hel_camp_enum_i", columnNames = {"enumeracio_id"})})
public class Camp implements Serializable, GenericEntity<Long> {

	public enum TipusCamp {
		STRING,
		INTEGER,
		FLOAT,
		BOOLEAN,
		TEXTAREA,
		DATE,
		PRICE,
		TERMINI,
		SELECCIO,
		SUGGEST,
		REGISTRE,
		ACCIO
	}

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotNull
	private TipusCamp tipus;
	@NotBlank
	@MaxLength(255)
	private String etiqueta;
	@MaxLength(255)
	private String observacions;
	@MaxLength(255)
	private String dominiId;
	@MaxLength(255)
	private String dominiParams;
	@MaxLength(64)
	private String dominiCampText;
	@MaxLength(64)
	private String dominiCampValor;
	@MaxLength(255)
	private String consultaParams;
	@MaxLength(64)
	private String consultaCampText;
	@MaxLength(64)
	private String consultaCampValor;
	
	
	private boolean dominiCacheText;
	private boolean dominiIntern;
	@MaxLength(255)
	private String jbpmAction;
	private boolean multiple;
	private boolean ocult;
	private boolean isIgnored;


	private Consulta consulta;
	private Domini domini;
	private Enumeracio enumeracio;
	private DefinicioProces definicioProces;
	private ExpedientTipus expedientTipus;
	private CampAgrupacio agrupacio;

	private Set<CampTasca> campsTasca = new HashSet<CampTasca>();
	private Set<Document> documentDates = new HashSet<Document>();
	private List<Validacio> validacions = new ArrayList<Validacio>();

	private Set<CampRegistre> registrePares = new HashSet<CampRegistre>();
	private List<CampRegistre> registreMembres = new ArrayList<CampRegistre>();

	private Integer ordre;


	public Camp() {}
	public Camp(String codi, TipusCamp tipus, String etiqueta) {
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
	}
	public Camp(DefinicioProces definicioProces, String codi, TipusCamp tipus, String etiqueta) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
	}
	public Camp(ExpedientTipus expedientTipus, String codi, TipusCamp tipus, String etiqueta) {
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp")
	@TableGenerator(name="gen_camp", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="tipus", length=255, nullable=false)
	public TipusCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusCamp tipus) {
		this.tipus = tipus;
	}

	@Column(name="etiqueta", length=255, nullable=false)
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name="observacions", length=255)
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	@Column(name="domini_paramid", length=255)
	public String getDominiId() {
		return dominiId;
	}
	public void setDominiId(String dominiId) {
		this.dominiId = dominiId;
	}

	@Column(name="domini_params", length=255)
	public String getDominiParams() {
		return dominiParams;
	}
	public void setDominiParams(String dominiParams) {
		this.dominiParams = dominiParams;
	}

	@Column(name="domini_camp_text", length=64)
	public String getDominiCampText() {
		return dominiCampText;
	}
	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}

	@Column(name="domini_camp_valor", length=64)
	public String getDominiCampValor() {
		return dominiCampValor;
	}
	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
	}

	@Column(name="domini_cache_text")
	public boolean isDominiCacheText() {
		return dominiCacheText;
	}
	public void setDominiCacheText(boolean dominiCacheText) {
		this.dominiCacheText = dominiCacheText;
	}

	@Column(name="jbpm_action", length=255)
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}

	@Column(name="multiple")
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@Column(name="ocult")
	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}
	
	@Column(name="ignored")
	public boolean isIgnored() {
		return isIgnored;
	}
	public void setIgnored(boolean isIgnored) {
		this.isIgnored = isIgnored;
	}
	

	@ManyToOne(optional=true)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_camp_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_camp_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="camp_agrupacio_id")
	@ForeignKey(name="hel_campagrup_camp_fk")
	public CampAgrupacio getAgrupacio() {
		return agrupacio;
	}
	public void setAgrupacio(CampAgrupacio agrupacio) {
		this.agrupacio = agrupacio;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="domini_id")
	@ForeignKey(name="hel_domini_camp_fk")
	public Domini getDomini() {
		return domini;
	}
	public void setDomini(Domini domini) {
		this.domini = domini;
	}
	public void removeDomini(Domini domini) {
		setDomini(null);
		setDominiId(null);
		setDominiParams(null);
		setDominiCampText(null);
		setDominiCampValor(null);
		setDominiCacheText(false);
		setDominiIntern(false);
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="enumeracio_id")
	@ForeignKey(name="hel_enumeracio_camp_fk")
	public Enumeracio getEnumeracio() {
		return enumeracio;
	}
	public void setEnumeracio(Enumeracio enumeracio) {
		this.enumeracio = enumeracio;
	}

	@OneToMany(mappedBy="camp", cascade={CascadeType.ALL})
	public Set<CampTasca> getCampsTasca() {
		return this.campsTasca;
	}
	public void setCampsTasca(Set<CampTasca> campsTasca) {
		this.campsTasca = campsTasca;
	}
	public void addCampTasca(CampTasca campTasca) {
		getCampsTasca().add(campTasca);
	}
	public void removeCampTasca(CampTasca campTasca) {
		getCampsTasca().remove(campTasca);
	}

	@OneToMany(mappedBy="campData")
	public Set<Document> getDocumentDates() {
		return documentDates;
	}
	public void setDocumentDates(Set<Document> documentDates) {
		this.documentDates = documentDates;
	}
	public void addDocumentData(Document documentData) {
		getDocumentDates().add(documentData);
	}
	public void removeDocumentData(Document documentData) {
		getDocumentDates().remove(documentData);
	}

	@OneToMany(mappedBy="camp", cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	public List<Validacio> getValidacions() {
		return this.validacions;
	}
	public void setValidacions(List<Validacio> validacions) {
		this.validacions = validacions;
	}
	public void addValidacio(Validacio validacio) {
		getValidacions().add(validacio);
	}
	public void removeValidacio(Validacio validacio) {
		getValidacions().remove(validacio);
	}

	@OneToMany(mappedBy="membre")
	public Set<CampRegistre> getRegistrePares() {
		return this.registrePares;
	}
	public void setRegistrePares(Set<CampRegistre> registrePares) {
		this.registrePares = registrePares;
	}
	public void addRegistrePare(CampRegistre registrePare) {
		getRegistrePares().add(registrePare);
	}
	public void removeRegistrePare(CampRegistre registrePare) {
		getRegistrePares().remove(registrePare);
	}

	@OneToMany(mappedBy="registre", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@OrderBy("ordre asc")
	@Fetch(value = FetchMode.SUBSELECT)
	public List<CampRegistre> getRegistreMembres() {
		return this.registreMembres;
	}
	public void setRegistreMembres(List<CampRegistre> registreMembres) {
		this.registreMembres = registreMembres;
	}
	public void addRegistreMembre(CampRegistre registreMembre) {
		getRegistreMembres().add(registreMembre);
	}
	public void removeRegistreMembre(CampRegistre registreMembre) {
		getRegistreMembres().remove(registreMembre);
	}
	
	@Column(name="ordre")
	public Integer getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	@Transient
	public String getCodiEtiqueta() {
		if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
			return etiqueta;
		else
			return codi + "/" + etiqueta;
	}

	@Transient
	public String getCodiPerInforme() {
		if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
			return codi.replace('$', '%');
		else {
			try {
				return definicioProces.getJbpmKey() + "/" + codi;
			} catch (Exception ex) {
				return null;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Transient
	public Class getJavaClass() {
		if (TipusCamp.STRING.equals(tipus)) {
			return String.class;
		} else if (TipusCamp.INTEGER.equals(tipus)) {
			return Long.class;
		} else if (TipusCamp.FLOAT.equals(tipus)) {
			return Double.class;
		} else if (TipusCamp.BOOLEAN.equals(tipus)) {
			return Boolean.class;
		} else if (TipusCamp.TEXTAREA.equals(tipus)) {
			return String.class;
		} else if (TipusCamp.DATE.equals(tipus)) {
			return Date.class;
		} else if (TipusCamp.PRICE.equals(tipus)) {
			return BigDecimal.class;
		} else if (TipusCamp.TERMINI.equals(tipus)) {
			return String.class;
		} else if (TipusCamp.REGISTRE.equals(tipus)) {
			return Object[].class;
		} else {
			return String.class;
		}
	}

	public static String getComText(
			TipusCamp tipus,
			Object valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (tipus.equals(TipusCamp.INTEGER)) {
				text = new DecimalFormat("#").format((Long)valor);
			} else if (tipus.equals(TipusCamp.FLOAT)) {
				text = new DecimalFormat("#.##########").format((Double)valor);
			} else if (tipus.equals(TipusCamp.PRICE)) {
				text = new DecimalFormat("#,##0.00").format((BigDecimal)valor);
			} else if (tipus.equals(TipusCamp.DATE)) {
				text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
			} else if (tipus.equals(TipusCamp.BOOLEAN)) {
				text = (((Boolean)valor).booleanValue()) ? "Si" : "No";
			} else if (tipus.equals(TipusCamp.SELECCIO)) {
				text = valorDomini;
			} else if (tipus.equals(TipusCamp.SUGGEST)) {
				text = valorDomini;
			} else if (tipus.equals(TipusCamp.TERMINI)) {
				if (valor instanceof Termini) {
					text = ((Termini)valor).toString();
				} else {
					String termtxt = (String)valor;
					String[] parts = termtxt.split("/");
					Termini t = new Termini();
					t.setAnys((parts.length >= 0) ? new Integer(parts[0]).intValue() : 0);
					t.setMesos((parts.length >= 1) ? new Integer(parts[1]).intValue() : 0);
					t.setDies((parts.length >= 2) ? new Integer(parts[2]).intValue() : 0);
					text = t.toString();
				}
			} else {
				text = valor.toString();
			}
			return text;
		} catch (Exception ex) {
			return valor.toString();
		}
	}

	public static Object getComObject(
			TipusCamp tipus,
			String text) {
		if (text == null)
			return null;
		try {
			Object obj = null;
			if (tipus.equals(TipusCamp.INTEGER)) {
				obj = new Long(text);
			} else if (tipus.equals(TipusCamp.FLOAT)) {
				obj = new Double(text);
			} else if (tipus.equals(TipusCamp.PRICE)) {
				obj = new BigDecimal(text);
			} else if (tipus.equals(TipusCamp.DATE)) {
				obj = new SimpleDateFormat("dd/MM/yyyy").parse(text);
			} else if (tipus.equals(TipusCamp.BOOLEAN)) {
				obj = new Boolean("S".equals(text));
			} else if (tipus.equals(TipusCamp.SELECCIO)) {
				obj = text;
			} else if (tipus.equals(TipusCamp.SUGGEST)) {
				obj = text;
			} else if (tipus.equals(TipusCamp.TERMINI)) {
				String[] parts = text.split("/");
				Termini termini = new Termini();
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
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());		
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
		Camp other = (Camp) obj;
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
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		return true;
	}

	@Column(name="consulta_params", length=255)
	public String getConsultaParams() {
		return consultaParams;
	}
	
	public void setConsultaParams(String consultaParams) {
		this.consultaParams = consultaParams;
	}

	@Column(name="consulta_camp_text", length=64)
	public String getConsultaCampText() {
		return consultaCampText;
	}
	
	public void setConsultaCampText(String consultaCampText) {
		this.consultaCampText = consultaCampText;
	}

	@Column(name="consulta_camp_valor", length=64)
	public String getConsultaCampValor() {
		return consultaCampValor;
	}
	
	public void setConsultaCampValor(String consultaCampValor) {
		this.consultaCampValor = consultaCampValor;
	}

	@Column(name="domini_intern")
	public boolean isDominiIntern() {
		return dominiIntern;
	}
	public void setDominiIntern(boolean dominiIntern) {
		this.dominiIntern = dominiIntern;
	}
	
	@ManyToOne(optional=true)
    @JoinColumn(name="consulta_id")
    @ForeignKey(name="hel_consulta_camp_fk")
    public Consulta getConsulta() {
        return consulta;
    }
    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

	private static final long serialVersionUID = 1L;

}
