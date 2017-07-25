/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.core.common.JbpmVars;

/**
 * Objecte de domini que representa una entrada al magatzem de documents
 * de la BBDD
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_document_store")
public class DocumentStore implements Serializable, GenericEntity<Long> {

	public enum DocumentFont {
		INTERNA,
		ALFRESCO
		}

	private Long id;
	@NotNull
	private DocumentFont font;
	@NotBlank
	@MaxLength(64)
	private String processInstanceId;
	@NotBlank
	@MaxLength(255)
	private String jbpmVariable;
	@NotNull
	private Date dataCreacio;
	private Date dataModificacio;
	@NotNull
	private Date dataDocument;
	@MaxLength(255)
	private String arxiuNom;
	private byte[] arxiuContingut;
	private boolean signat = false;

	@MaxLength(255)
	private String referenciaCustodia;
	@MaxLength(255)
	private String referenciaFont;
	@MaxLength(255)
	private String registreNumero;
	private Date registreData;
	@MaxLength(255)
	private String registreOrganCodi;
	@MaxLength(255)
	private String registreOficinaCodi;
	@MaxLength(255)
	private String registreOficinaNom;
	private boolean registreEntrada;

	private boolean adjunt = false;
	@MaxLength(255)
	private String adjuntTitol;



	public DocumentStore() {}
	public DocumentStore(
			DocumentFont font,
			String processInstanceId,
			String jbpmVariable,
			Date dataCreacio,
			Date dataDocument,
			String arxiuNom) {
		this.font = font;
		this.processInstanceId = processInstanceId;
		this.jbpmVariable = jbpmVariable;
		this.dataCreacio = dataCreacio;
		this.dataModificacio = dataCreacio;
		this.dataDocument = dataDocument;
		this.arxiuNom = arxiuNom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document_store")
	@TableGenerator(name="gen_document_store", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="font", nullable=false)
	public DocumentFont getFont() {
		return font;
	}
	public void setFont(DocumentFont font) {
		this.font = font;
	}

	@Column(name="process_instance_id", length=64, nullable=false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="jbpm_variable", length=255, nullable=false)
	public String getJbpmVariable() {
		return jbpmVariable;
	}
	public void setJbpmVariable(String jbpmVariable) {
		this.jbpmVariable = jbpmVariable;
	}

	@Column(name="data_creacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	@Column(name="data_modificacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataModificacio() {
		return dataModificacio;
	}
	public void setDataModificacio(Date dataModificacio) {
		this.dataModificacio = dataModificacio;
	}

	@Column(name="data_document", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getDataDocument() {
		return dataDocument;
	}
	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
	}

	@Column(name="arxiu_nom", length=255, nullable=false)
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	@Column(name="arxiu_contingut")
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}

	@Column(name="signat")
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}

	@Column(name="ref_custodia", length=255)
	public String getReferenciaCustodia() {
		return referenciaCustodia;
	}
	public void setReferenciaCustodia(String referenciaCustodia) {
		this.referenciaCustodia = referenciaCustodia;
	}

	@Column(name="ref_font", length=255)
	public String getReferenciaFont() {
		return referenciaFont;
	}
	public void setReferenciaFont(String referenciaFont) {
		this.referenciaFont = referenciaFont;
	}

	@Column(name="registre_num", length=255)
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}

	@Column(name="registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}

	@Column(name="registre_orgcodi", length=255)
	public String getRegistreOrganCodi() {
		return registreOrganCodi;
	}
	public void setRegistreOrganCodi(String registreOrganCodi) {
		this.registreOrganCodi = registreOrganCodi;
	}

	@Column(name="registre_ofcodi", length=255)
	public String getRegistreOficinaCodi() {
		return registreOficinaCodi;
	}
	public void setRegistreOficinaCodi(String registreOficinaCodi) {
		this.registreOficinaCodi = registreOficinaCodi;
	}

	@Column(name="registre_ofnom", length=255)
	public String getRegistreOficinaNom() {
		return registreOficinaNom;
	}
	public void setRegistreOficinaNom(String registreOficinaNom) {
		this.registreOficinaNom = registreOficinaNom;
	}

	@Column(name="registre_entrada")
	public boolean isRegistreEntrada() {
		return registreEntrada;
	}
	public void setRegistreEntrada(boolean registreEntrada) {
		this.registreEntrada = registreEntrada;
	}

	@Column(name="adjunt")
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}

	@Column(name="adjunt_titol", length=255)
	public String getAdjuntTitol() {
		return adjuntTitol;
	}
	public void setAdjuntTitol(String adjuntTitol) {
		this.adjuntTitol = adjuntTitol;
	}

	@Transient
	public String getCodiDocument() {
		if (getJbpmVariable() == null)
			return null;
		return getJbpmVariable().substring(JbpmVars.PREFIX_DOCUMENT.length());
	}
	@Transient
	public boolean isRegistrat() {
		return (registreNumero != null) || (registreData != null);
	}
	@Transient
	public boolean isRegistreSortida() {
		return !isRegistreEntrada();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DocumentStore other = (DocumentStore) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
