package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PinbalServeiDocPermesEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.TitularDto.ScspTipoDocumentacion;

@Entity
@Table(	name="hel_interessat")//ADAPTAT A SICRES 4
public class Interessat implements Serializable, GenericEntity<Long> {

	
	private InteressatTipusEnumDto tipus;
	
	private Long id;
	@NotBlank
	private String codi;
	@Column(name = "dir3codi", length = 21)
	private String dir3Codi;
	@NotBlank
	private String nom;
	private String llinatge1;  
	private String llinatge2;  
	private String email;  
	private String telefon; 

	private boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private boolean entregaDeh;
	private boolean entregaDehObligat;
	
	private String tipusDocIdent;
	
	@Column(name="codidire", length=21)
	private String codiDire;
	@Column(name="direccio", length=160)
	private String direccio;
	@Column(name="documentident", length=256)
	private String documentIdent;
	@Column(name="raosocial", length=256)
	private String raoSocial;
	@Column(name="es_representant")
    private boolean es_representant;
	@Column(name="observacions", length=256)
	private String observacions;
	@Column(name = "pais", length = 4)
	protected String pais;
	@Column(name = "provincia", length = 2)
	protected String provincia;
	@Column(name = "municipi", length = 5)
	protected String municipi;
	@Column(name="canalnotif")
	protected String canalNotif;
	
    private List<Interessat> representats; //només existeix quan es_representant=true
    private Interessat representant; //només existeix quan es_representant=false
	private Expedient expedient;

	public Interessat() {
		super();
	}
	public Interessat(
			Long id, 
			String codi,
			String nom, 
			String documentIdent, 
			String dir3codi,
			String llinatge1, 
			String llinatge2, 
			InteressatTipusEnumDto tipus,
			String email, 
			String telefon,
			Expedient expedient,
			boolean entregaPostal,
			EntregaPostalTipus entregaTipus,
			String linia1,
			String linia2,
			String codiPostal,
			boolean entregaDeh,
			boolean entregaDehObligat,
			String tipusDocIdent,
			String direccio,
			String observacions,
			boolean es_representant,
			String raoSocial,
			String pais,
			String provincia,
			String municipi,
			String canalNotif,
			String codiDire
			) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
		this.documentIdent = documentIdent;
		this.dir3Codi = dir3codi;
		this.llinatge1 = llinatge1;
		this.llinatge2 = llinatge2;
		this.tipus = tipus;
		this.email = email;
		this.telefon = telefon;
		this.expedient = expedient;
		this.entregaPostal = entregaPostal;
		this.entregaTipus = entregaTipus;
		this.setLinia1(linia1);
		this.setLinia2(linia2);
		this.codiPostal = codiPostal;
		this.entregaDeh = entregaDeh;
		this.entregaDehObligat = entregaDehObligat;
		this.tipusDocIdent=tipusDocIdent;
		this.direccio=direccio;
		this.observacions=observacions;
		this.es_representant=es_representant;
		this.raoSocial=raoSocial;
		this.pais=pais;
		this.provincia=provincia;
		this.municipi=municipi;
		this.canalNotif=canalNotif;
		this.codiDire=codiDire;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_tasca")
	@TableGenerator(name="gen_tasca", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(name="EXPEDIENT_ID")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	@OneToMany(
			mappedBy = "representant",
			fetch = FetchType.LAZY)	
	public List<Interessat> getRepresentats() {
		return representats;
	}
	public void setRepresentats(List<Interessat> representats) {
		this.representats = representats;
	}
	
	
	@ManyToOne(optional=true)
	@JoinColumn(name="REPRESENTANT_ID")
	public Interessat getRepresentant() {
		return representant;
	}
	
	public void setRepresentant(Interessat representant) {
		this.representant = representant;
	}

	public InteressatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
//	public String getNif() {
//		return nif;
//	}
//	public void setNif(String nif) {
//		this.nif = nif;
//	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public boolean isEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaPostalTipus getEntregaTipus() {
		return entregaTipus;
	}
	public void setEntregaTipus(EntregaPostalTipus entregaTipus) {
		this.entregaTipus = entregaTipus;
	}
	public String getLinia1() {
		return linia1;
	}
	public void setLinia1(String linia1) {
		this.linia1 = linia1 != null ? 	linia1.substring(0, Math.min(linia1.length(), 50)) : null;
	}
	public String getLinia2() {
		return linia2;
	}
	public void setLinia2(String linia2) {
		this.linia2 = linia2 != null ? 	linia2.substring(0, Math.min(linia2.length(), 50)) : null;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public boolean isEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public boolean isEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	
	public String getDireccio() {
		return direccio;
	}
	public void setDireccio(String direccio) {
		this.direccio = direccio;
	}
	
	public String getCodiDire() {
		return codiDire;
	}
	public void setCodiDire(String codiDire) {
		this.codiDire = codiDire;
	}

	public String getDocumentIdent() {
		return documentIdent;
	}
	public String getTipusDocIdent() {
		return tipusDocIdent;
	}
	public void setTipusDocIdent(String tipusDocIdent) {
		this.tipusDocIdent = tipusDocIdent;
	}
	public void setDocumentIdent(String documentIdent) {
		this.documentIdent = documentIdent;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}

	public boolean isEs_representant() {
		return es_representant;
	}
	public void setEs_representant(boolean es_representant) {
		this.es_representant = es_representant;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public String getCanalNotif() {
		return canalNotif;
	}
	public void setCanalNotif(String canalNotif) {
		this.canalNotif = canalNotif;
	}

	//Retorna el tipus de document a enviar en una petició PINBAL, null en cas de no trobar coincidencia
	//enum ScspTipoDocumentacion { CIF, CSV, DNI, NIE, NIF, Pasaporte, NumeroIdentificacion, Otros}
	//enum TipusDocIdentSICRES { NIF ("N"), CIF ("C"), PASSAPORT ("P"), DOCUMENT_IDENTIFICATIU_ESTRANGERS ("E"), ALTRES_DE_PERSONA_FISICA ("X"), CODI_ORIGEN ("O"); }
	public ScspTipoDocumentacion tipusInteressatCompatible(ServeiPinbalEntity serveiPinbal) {
		if (this.tipus!=null) {
			List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos = serveiPinbal.getPinbalServeiDocsPermesos();
			//Persona física
			if (this.tipus.equals(InteressatTipusEnumDto.FISICA)) {
				if ("N".equals(this.tipusDocIdent)) {
					if (pinbalServeiDocsPermesos.isEmpty() || !pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI)) {
						return ScspTipoDocumentacion.DNI;
					} else if (serveiPinbal.isPinbalServeiDocPermesDni()) {
						return ScspTipoDocumentacion.NIF;
					}
				} else if ("P".equals(this.tipusDocIdent) && serveiPinbal.isPinbalServeiDocPermesPas()) {
					return ScspTipoDocumentacion.Pasaporte;
				} else if ("E".equals(this.tipusDocIdent) && serveiPinbal.isPinbalServeiDocPermesNie()) {
					return ScspTipoDocumentacion.NIE;
				}
			} else {
				//Persona jurídica o administració
				if ("C".equals(this.tipusDocIdent) && serveiPinbal.isPinbalServeiDocPermesCif()) {
					return ScspTipoDocumentacion.CIF;
				} else if ("N".equals(this.tipusDocIdent) && serveiPinbal.isPinbalServeiDocPermesNif()) {
					return ScspTipoDocumentacion.NIF;
				}
			}
		}
		return null;
	}

	private static final long serialVersionUID = 1L;
}