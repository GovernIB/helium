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

import es.caib.distribucio.ws.backofficeintegracio.FirmaPerfil;
import es.caib.distribucio.ws.backofficeintegracio.FirmaTipus;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.distribucio.ws.backofficeintegracio.SicresTipoDocumento;
import es.caib.distribucio.ws.backofficeintegracio.SicresValidezDocumento;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;

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
	private FirmaPerfil firmaPerfil;
	@Column(name = "firma_tamany")
	private long firmaTamany;
	@Enumerated(EnumType.STRING)
	@Column(name = "firma_tipus", length = 4)
	private FirmaTipus firmaTipus;
	@Column(name = "firma_nom", length = 80)
	private String firmaNom;
	@Column(name = "nom", length = 80, nullable = false)
	private String nom;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "nti_fecha_captura", nullable = false)
	private Date ntiFechaCaptura;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_origen", length = 20, nullable = false)
	private NtiOrigen ntiOrigen;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_tipo_doc", length = 20, nullable = false)
	private NtiTipoDocumento ntiTipoDocumental;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Enumerated(EnumType.STRING)
	@Column(name = "sicres_tipo_doc", length = 20 , nullable = false)
	private SicresTipoDocumento sicresTipoDocumento;
	@Enumerated(EnumType.STRING)
	@Column(name = "sicres_validez_doc", length = 30)
	private SicresValidezDocumento sicresValidezDocumento;
	@Enumerated(EnumType.STRING)
	@Column(name = "nti_estado_elaboracio", length = 50, nullable = false)
	private NtiEstadoElaboracion ntiEstadoElaboracion;
	@Column(name = "tamany", nullable = false)
	private long tamany;
	@Column(name = "tipus_mime", length = 30)
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
			 NtiOrigen ntiOrigen,
			 NtiTipoDocumento ntiTipoDocumental,
			 SicresTipoDocumento sicresTipoDocumento,
			 String titol,
			 Anotacio anotacio,
			 NtiEstadoElaboracion ntiEstadoElaboracion) {
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
				 NtiOrigen ntiOrigen,
				 NtiTipoDocumento ntiTipoDocumental,
				 SicresTipoDocumento sicresTipoDocumento,
				 String titol,
				 Anotacio anotacio,
				 NtiEstadoElaboracion ntiEstadoElaboracion) {
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
		public Builder ntiTipoDocumental(NtiTipoDocumento ntiTipoDocumental) {
			built.ntiTipoDocumental = ntiTipoDocumental;
			return this;
		}
		public Builder sicresTipoDocumento(SicresTipoDocumento sicresTipoDocumento) {
			built.sicresTipoDocumento = sicresTipoDocumento;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder sicresValidezDocumento(SicresValidezDocumento sicresValidezDocumento) {
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
	public void setFirmaPerfil(FirmaPerfil firmaPerfil) {
		this.firmaPerfil = firmaPerfil;
	}
	public void setFirmaTamany(long firmaTamany) {
		this.firmaTamany = firmaTamany;
	}
	public void setFirmaTipus(FirmaTipus firmaTipus) {
		this.firmaTipus = firmaTipus;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setNtiFechaCaptura(Date ntiFechaCaptura) {
		this.ntiFechaCaptura = ntiFechaCaptura;
	}
	public void setNtiOrigen(NtiOrigen ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public void setNtiTipoDocumental(NtiTipoDocumento ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public void setSicresTipoDocumento(SicresTipoDocumento sicresTipoDocumento) {
		this.sicresTipoDocumento = sicresTipoDocumento;
	}
	public void setSicresValidezDocumento(SicresValidezDocumento sicresValidezDocumento) {
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
	public FirmaPerfil getFirmaPerfil() {
		return firmaPerfil;
	}
	public long getFirmaTamany() {
		return firmaTamany;
	}
	public FirmaTipus getFirmaTipus() {
		return firmaTipus;
	}
	public String getNom() {
		return nom;
	}
	public Date getNtiFechaCaptura() {
		return ntiFechaCaptura;
	}
	public NtiOrigen getNtiOrigen() {
		return ntiOrigen;
	}
	public NtiTipoDocumento getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public String getObservacions() {
		return observacions;
	}
	public SicresTipoDocumento getSicresTipoDocumento() {
		return sicresTipoDocumento;
	}
	public SicresValidezDocumento getSicresValidezDocumento() {
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
	public NtiEstadoElaboracion getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(NtiEstadoElaboracion ntiEstadoElaboracion) {
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
