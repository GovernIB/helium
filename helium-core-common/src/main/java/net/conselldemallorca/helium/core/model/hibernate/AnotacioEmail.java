/**
 * 
 */
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

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.EmailTipusEnumDto;

/**
 * Classe del model de dades que representa una anotació al registre rebuda com a Backoffice 
 * de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "hel_anotacio_email")
@org.hibernate.annotations.Table(
		appliesTo = "hel_anotacio_email")
public class AnotacioEmail implements Serializable, GenericEntity<Long> {

	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_anot_email")
	@TableGenerator(name="gen_anot_email", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	/** Expedient amb el qual s'associa o inclou l'anotació. */
	@ManyToOne(optional=true)
	@JoinColumn(name="anotacio_id")
	@ForeignKey(name = "hel_anotacio_id_fk")
	private Anotacio anotacio;

	/** Expedient amb el qual s'associa o inclou l'anotació. */
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_id")
	@ForeignKey(name = "hel_expedient_id_fk")
	private Expedient expedient;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "email_tipus", nullable = false)
	private EmailTipusEnumDto emailTipus;
	
	@Column(name = "remitent_codi", length = 64)
	private String remitentCodi;
	
	@Column(name = "destinatari_codi", length = 64)
	private String destinatariCodi;
	
	@Column(name = "destinatari_email", length = 256)
	private String destinatariEmail;
	
	@Column(name="enviament_agrupat")
	private boolean enviamentAgrupat;
	
	@Column(name="num_intents")
	private int numIntents;
	
	@Column(name="data_creacio")
	private Date dataCreacio;

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public AnotacioEmail() {
		super();
	};
	
	public AnotacioEmail(
				Anotacio anotacio,
				Expedient expedient,
				String destinatariCodi,
				String remitentCodi,
				EmailTipusEnumDto emailTipus,
				String destinatariEmail,
				boolean enviamentAgrupat,
				Date dataCreacio,
				int numIntents
				) {
		super();
		this.anotacio=anotacio;
		this.expedient=expedient;
		this.destinatariCodi=destinatariCodi;
		this.remitentCodi=remitentCodi;
		this.emailTipus=emailTipus;
		this.destinatariEmail=destinatariEmail;
		this.enviamentAgrupat=enviamentAgrupat;
		this.dataCreacio=dataCreacio;
		this.numIntents=numIntents;
	}
		

	public Anotacio getAnotacio() {
		return anotacio;
	}


	public void setAnotacio(Anotacio anotacio) {
		this.anotacio = anotacio;
	}


	public Expedient getExpedient() {
		return expedient;
	}


	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}


	public EmailTipusEnumDto getEmailTipus() {
		return emailTipus;
	}


	public void setEmailTipus(EmailTipusEnumDto emailTipus) {
		this.emailTipus = emailTipus;
	}


	public String getRemitentCodi() {
		return remitentCodi;
	}


	public void setRemitentCodi(String remitentCodi) {
		this.remitentCodi = remitentCodi;
	}


	public String getDestinatariCodi() {
		return destinatariCodi;
	}


	public void setDestinatariCodi(String destinatariCodi) {
		this.destinatariCodi = destinatariCodi;
	}


	public String getDestinatariEmail() {
		return destinatariEmail;
	}


	public void setDestinatariEmail(String destinatariEmail) {
		this.destinatariEmail = destinatariEmail;
	}


	public boolean isEnviamentAgrupat() {
		return enviamentAgrupat;
	}


	public void setEnviamentAgrupat(boolean enviamentAgrupat) {
		this.enviamentAgrupat = enviamentAgrupat;
	}


	public int getNumIntents() {
		return numIntents;
	}


	public void setNumIntents(int numIntents) {
		this.numIntents = numIntents;
	}


	public Date getDataCreacio() {
		return dataCreacio;
	}


	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}


	private static final long serialVersionUID = 1815997738055924981L;
}
