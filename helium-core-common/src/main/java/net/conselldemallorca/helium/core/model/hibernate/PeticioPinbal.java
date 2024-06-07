package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;

@Entity
@Table(name="HEL_PETICIO_PINBAL")
public class PeticioPinbal implements Serializable, GenericEntity<Long> {

	private static final long serialVersionUID = -470902611338301228L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_pinbal")
	@TableGenerator(name="gen_pinbal", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="ID")
	private Long id;
	@ManyToOne(optional=false)
	@JoinColumn(name="ENTORN_ID")
	private Entorn entorn;
	@ManyToOne(optional=false)
	@JoinColumn(name="TIPUS_ID")
	private ExpedientTipus tipus;
	@ManyToOne(optional=false)
	@JoinColumn(name="EXPEDIENT_ID")
	@ForeignKey(name="HEL_PETICIO_EXPEDIENT_FK")
	private Expedient expedient;
	@ManyToOne
	@JoinColumn(name="DOCUMENT_ID")
	private DocumentStore document;
	@Column(name="TOKEN_ID")
	private Long tokenId;
	@MaxLength(32)
	private String procediment;
	@MaxLength(64)
	private String usuari;
	@NotNull
	@Column(name="DATA_PETICIO")
	private Date dataPeticio;
	private boolean asincrona=false;
	@Enumerated(EnumType.STRING)
	private PeticioPinbalEstatEnum estat;
	@MaxLength(4000)
	@Column(name="ERROR_MSG")
	private String errorMsg;
	@MaxLength(64)
	@Column(name="PINBAL_ID")
	private String pinbalId;
	@Column(name="DATA_PREVISTA")
	private Date dataPrevista;
	@Column(name="DATA_DARRERA_CONSULTA")
	private Date dataDarreraConsulta;
	@MaxLength(32)
	@Column(name="TRANSICIO_OK")
	private String transicioOK;
	@MaxLength(32)
	@Column(name="TRANSICIO_KO")
	private String transicioKO;
	@Column(name="DATA_PROCESSAMENT_PRIMER")
	private Date dataProcessamentPrimer;
	@Column(name="DATA_PROCESSAMENT_DARRER")
	private Date dataProcessamentDarrer;
	@MaxLength(4000)
	@Column(name="ERROR_PROCESSAMENT")
	private String errorProcessament;
	
	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Entorn getEntorn() {
		return entorn;
	}

	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	public ExpedientTipus getTipus() {
		return tipus;
	}

	public void setTipus(ExpedientTipus tipus) {
		this.tipus = tipus;
	}

	public Expedient getExpedient() {
		return expedient;
	}

	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	public DocumentStore getDocument() {
		return document;
	}

	public void setDocument(DocumentStore document) {
		this.document = document;
	}

	public String getProcediment() {
		return procediment;
	}

	public void setProcediment(String procediment) {
		this.procediment = procediment;
	}

	public String getUsuari() {
		return usuari;
	}

	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	public Date getDataPeticio() {
		return dataPeticio;
	}

	public void setDataPeticio(Date dataPeticio) {
		this.dataPeticio = dataPeticio;
	}

	public boolean isAsincrona() {
		return asincrona;
	}

	public void setAsincrona(boolean asincrona) {
		this.asincrona = asincrona;
	}

	public PeticioPinbalEstatEnum getEstat() {
		return estat;
	}

	public void setEstat(PeticioPinbalEstatEnum estat) {
		this.estat = estat;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getPinbalId() {
		return pinbalId;
	}

	public void setPinbalId(String pinbalId) {
		this.pinbalId = pinbalId;
	}

	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}

	public Date getDataDarreraConsulta() {
		return dataDarreraConsulta;
	}

	public void setDataDarreraConsulta(Date dataDarreraConsulta) {
		this.dataDarreraConsulta = dataDarreraConsulta;
	}

	public String getTransicioOK() {
		return transicioOK;
	}

	public void setTransicioOK(String transicioOK) {
		this.transicioOK = transicioOK;
	}

	public String getTransicioKO() {
		return transicioKO;
	}

	public void setTransicioKO(String transicioKO) {
		this.transicioKO = transicioKO;
	}

	public Date getDataProcessamentPrimer() {
		return dataProcessamentPrimer;
	}

	public void setDataProcessamentPrimer(Date dataProcessamentPrimer) {
		this.dataProcessamentPrimer = dataProcessamentPrimer;
	}

	public Date getDataProcessamentDarrer() {
		return dataProcessamentDarrer;
	}

	public void setDataProcessamentDarrer(Date dataProcessamentDarrer) {
		this.dataProcessamentDarrer = dataProcessamentDarrer;
	}

	public String getErrorProcessament() {
		return errorProcessament;
	}

	public void setErrorProcessament(String errorProcessament) {
		this.errorProcessament = errorProcessament;
	}
	public Long getTokenId() {
		return tokenId;
	}
	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}
}