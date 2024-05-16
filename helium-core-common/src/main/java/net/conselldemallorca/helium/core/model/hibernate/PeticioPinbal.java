package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

@Entity
@Table(name="HEL_PETICIO_PINBAL")
public class PeticioPinbal implements Serializable, GenericEntity<Long> {

	private static final long serialVersionUID = -470902611338301228L;
	
	private Long id;
	@NotNull
	private Entorn entorn;
	@NotNull
	private ExpedientTipus tipus;
	@NotNull
	private Expedient expedient;
	private Document document;
	@MaxLength(32)
	private String procediment;
	@MaxLength(64)
	private String usuari;
	@NotNull
	private Date dataPeticio;
	private boolean asincrona=false;
	private String estat;
	@MaxLength(4000)
	private String errorMsg;
	@MaxLength(64)
	private String pinbalId;
	private Date dataPrevista;
	private Date dataDarreraConsulta;
	@MaxLength(32)
	private String transicioOk;
	@MaxLength(32)
	private String transicioKo;
	private Date dataProcessamentPrimer;
	private Date dataProcessamentDarrer;
	@MaxLength(4000)
	private String errorProcessament;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_pinbal")
	@TableGenerator(name="gen_pinbal", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
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

	public String getEstat() {
		return estat;
	}

	public void setEstat(String estat) {
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

	public String getTransicioOk() {
		return transicioOk;
	}

	public void setTransicioOk(String transicioOk) {
		this.transicioOk = transicioOk;
	}

	public String getTransicioKo() {
		return transicioKo;
	}

	public void setTransicioKo(String transicioKo) {
		this.transicioKo = transicioKo;
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
}