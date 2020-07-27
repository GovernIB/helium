/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
//import es.caib.distribucio.ws.backofficeintegracio.FirmaPerfil;
//import es.caib.distribucio.ws.backofficeintegracio.FirmaTipus;
//import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
//import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
//import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
//import es.caib.distribucio.ws.backofficeintegracio.SicresTipoDocumento;
//import es.caib.distribucio.ws.backofficeintegracio.SicresValidezDocumento;

/**
 * Classe del model de dades que representa un document
 * d'una anotació de registre rebuda com a Backoffice 
 * de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "hel_anotacio_annex")
@org.hibernate.annotations.Table(
		appliesTo = "hel_anotacio_annex",
		indexes = {
				@Index(name = "hel_anotacio_annex_fk_i", columnNames = {"anotacio_id"})})
public class AnotacioAnnex implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_anot_annex")
	@TableGenerator(name="gen_anot_annex", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "contingut")
	private byte[] contingut;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "firma_contingut")
	private byte[] firmaContingut;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_perfil", length = 4)
	private ArxiuFirmaPerfilEnumDto firmaPerfil;
	@Column(name = "firma_tamany")
	private long firmaTamany;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_tipus", length = 4)
	private NtiTipoFirmaEnumDto firmaTipus;
	@Column(name = "firma_nom", length = 80)
	private String firmaNom;
	@Column(name = "nom", length = 80, nullable = false)
	private String nom;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "nti_fecha_captura", nullable = false)
	private Date ntiFechaCaptura;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_origen", length = 20, nullable = false)
	private NtiOrigenEnumDto ntiOrigen;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_tipo_doc", length = 20, nullable = false)
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "sicres_tipo_doc", length = 20)
	private String sicresTipoDocumento;
	@Column(name = "sicres_validez_doc", length = 30)
	private String sicresValidezDocumento;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_estado_elaboracio", length = 50, nullable = false)
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@Column(name = "tamany", nullable = false)
	private long tamany;
	@Column(name = "tipus_mime", length = 255)
	private String tipusMime;
	@Column(name = "titol", length = 200, nullable = false)
	private String titol;
	@Column(name = "uuid", length = 100)
	private String uuid;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 20, nullable = false)
	private AnotacioAnnexEstatEnumDto estat;
	@Column(name = "error", length = 4000)
	private String error;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "anotacio_id")
	@ForeignKey(name = "hel_interessat_anotacio_fk")
	private Anotacio anotacio;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
	public static Builder getBuilder(
			 String nom,
			 Date ntiFechaCaptura,
			 NtiOrigenEnumDto ntiOrigen,
			 NtiTipoDocumentalEnumDto ntiTipoDocumental,
			 String sicresTipoDocumento,
			 String titol,
			 Anotacio anotacio,
			 NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		return new Builder(
				 nom,
				 ntiFechaCaptura,
				 ntiOrigen,
				 ntiTipoDocumental,
				 sicresTipoDocumento,
				 titol,
				 anotacio,
				 ntiEstadoElaboracion);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		AnotacioAnnex built;

		Builder(
				 String nom,
				 Date ntiFechaCaptura,
				 NtiOrigenEnumDto ntiOrigen,
				 NtiTipoDocumentalEnumDto ntiTipoDocumental,
				 String sicresTipoDocumento,
				 String titol,
				 Anotacio anotacio,
				 NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
			built = new AnotacioAnnex();
			built.nom = nom;
			built.ntiFechaCaptura = ntiFechaCaptura;
			built.ntiOrigen = ntiOrigen;
			built.ntiTipoDocumental = ntiTipoDocumental;
			built.sicresTipoDocumento = sicresTipoDocumento;
			built.titol = titol;
			built.anotacio = anotacio;
			built.estat = AnotacioAnnexEstatEnumDto.CREAT;
			built.ntiEstadoElaboracion = ntiEstadoElaboracion;
		}
		
		public Builder contingut(byte[] contingut) {
			built.contingut = contingut;
			return this;
		}
		public Builder firmaContingut(byte[] firmaContingut) {
			built.firmaContingut = firmaContingut;
			return this;
		}
		public Builder ntiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
			built.ntiTipoDocumental = ntiTipoDocumental;
			return this;
		}
		public Builder sicresTipoDocumento(String sicresTipoDocumento) {
			built.sicresTipoDocumento = sicresTipoDocumento;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder sicresValidezDocumento(String sicresValidezDocumento) {
			built.sicresValidezDocumento = sicresValidezDocumento;
			return this;
		}
		public Builder tipusMime(String tipusMime) {
			built.tipusMime = tipusMime;
			return this;
		}
		public Builder uuid(String uuid) {
			built.uuid = uuid;
			return this;
		}
		public Builder firmaNom(String firmaNom) {
			built.firmaNom = firmaNom;
			return this;
		}
		public AnotacioAnnex build() {
			return built;
		}
	}
	
	
	public Anotacio getAnotacio() {
		return anotacio;
	}
	public void setAnotacio(Anotacio anotacio) {
		this.anotacio = anotacio;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public void setFirmaContingut(byte[] firmaContingut) {
		this.firmaContingut = firmaContingut;
	}
	public void setFirmaPerfil(ArxiuFirmaPerfilEnumDto firmaPerfil) {
		this.firmaPerfil = firmaPerfil;
	}
	public void setFirmaTamany(long firmaTamany) {
		this.firmaTamany = firmaTamany;
	}
	public void setFirmaTipus(NtiTipoFirmaEnumDto firmaTipus) {
		this.firmaTipus = firmaTipus;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setNtiFechaCaptura(Date ntiFechaCaptura) {
		this.ntiFechaCaptura = ntiFechaCaptura;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public void setSicresTipoDocumento(String sicresTipoDocumento) {
		this.sicresTipoDocumento = sicresTipoDocumento;
	}
	public void setSicresValidezDocumento(String sicresValidezDocumento) {
		this.sicresValidezDocumento = sicresValidezDocumento;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public byte[] getFirmaContingut() {
		return firmaContingut;
	}
	public ArxiuFirmaPerfilEnumDto getFirmaPerfil() {
		return firmaPerfil;
	}
	public long getFirmaTamany() {
		return firmaTamany;
	}
	public NtiTipoFirmaEnumDto getFirmaTipus() {
		return firmaTipus;
	}
	public String getNom() {
		return nom;
	}
	public Date getNtiFechaCaptura() {
		return ntiFechaCaptura;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public String getObservacions() {
		return observacions;
	}
	public String getSicresTipoDocumento() {
		return sicresTipoDocumento;
	}
	public String getSicresValidezDocumento() {
		return sicresValidezDocumento;
	}
	public long getTamany() {
		return tamany;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public String getTitol() {
		return titol;
	}
	public String getUuid() {
		return uuid;
	}
	public AnotacioAnnexEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(AnotacioAnnexEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = StringUtils.abbreviate(
				error,
				1000);
	}
	public NtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public String getFirmaNom() {
		return firmaNom;
	}
	public void setFirmaNom(String firmaNom) {
		this.firmaNom = firmaNom;
	}
	
	private static final long serialVersionUID = 1864495018202820415L;	
}
