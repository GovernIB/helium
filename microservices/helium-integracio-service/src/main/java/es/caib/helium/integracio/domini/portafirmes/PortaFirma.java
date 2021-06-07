package es.caib.helium.integracio.domini.portafirmes;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.enums.portafirmes.Transicio;
import lombok.Data;

@Data
@Entity
@Table(name="hel_portasignatures", uniqueConstraints={@UniqueConstraint(columnNames={"document_id", "token_id"})})
public class PortaFirma implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="document_id", nullable=false)
	private Long documentId;
	@Column(name="token_id", nullable=false)
	private Long tokenId;
	@Column(name="data_enviat")
	private Date dataEnviat;
	@Column(name="estat")
	private TipusEstat estat;
	@Column(name="transicio")
	private Transicio transition;
	@Column(name="document_store_id")
	private Long documentStoreId;
	@Column(name="motiu_rebuig")
	private String motiuRebuig;
	@Column(name="transicio_ok")
	private String transicioOK;
	@Column(name="transicio_ko")
	private String transicioKO;
	@Column(name="data_cb_pri")
	private Date dataProcessamentPrimer;
	@Column(name="data_cb_dar")
	private Date dataProcessamentDarrer;
	@Column(name="data_signat_rebutjat")
	private Date dataSignatRebutjat;
	@Column(name="data_custodia_intent")
	private Date dataCustodiaIntent;
	@Column(name="data_custodia_ok")
	private Date dataCustodiaOk;
	@Column(name="data_signal_intent")
	private Date dataSignalIntent;
	@Column(name="data_signal_ok")
	private Date dataSignalOk;
	@Column(name="error_cb_proces")
	private String errorCallbackProcessant;
	@Column(name="process_instance_id", length=255, nullable=false)
	private String processInstanceId;
	@Column(name="expedient_id", nullable=false)
	private Long expedientId; 

	public PortaFirma() {}
	public PortaFirma(
			Long documentId,
			Long tokenId,
			Date dataEnviat,
			TipusEstat estat,
			Transicio transition,
			Long documentStoreId,
			String motiuRebuig,
			String transicioOK,
			String transicioKO) {
		this.documentId = documentId;
		this.tokenId = tokenId;
		this.dataEnviat = dataEnviat;
		this.estat = estat;
		this.transition = transition;
		this.documentStoreId = documentStoreId;
		this.motiuRebuig = motiuRebuig;
		this.transicioOK = transicioOK;
		this.transicioKO = transicioKO;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataEnviat == null) ? 0 : dataEnviat.hashCode());
		result = prime * result
				+ ((documentId == null) ? 0 : documentId.hashCode());
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
		result = prime * result + ((tokenId == null) ? 0 : tokenId.hashCode());
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
		PortaFirma other = (PortaFirma) obj;
		if (dataEnviat == null) {
			if (other.dataEnviat != null)
				return false;
		} else if (!dataEnviat.equals(other.dataEnviat))
			return false;
		if (documentId == null) {
			if (other.documentId != null)
				return false;
		} else if (!documentId.equals(other.documentId))
			return false;
		if (estat == null) {
			if (other.estat != null)
				return false;
		} else if (!estat.equals(other.estat))
			return false;
		if (tokenId == null) {
			if (other.tokenId != null)
				return false;
		} else if (!tokenId.equals(other.tokenId))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
