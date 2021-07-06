package es.caib.helium.client.integracio.portafirmes.model;

import java.util.Date;

import es.caib.helium.client.integracio.portafirmes.enums.TipusEstat;
import es.caib.helium.client.integracio.portafirmes.enums.Transicio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortaFirma  {

	private Long id;
	private Long documentId;
	private Long tokenId;
	private Date dataEnviat;
	private TipusEstat estat;
	private Transicio transition;
	private Long documentStoreId;
	private String motiuRebuig;
	private String transicioOK;
	private String transicioKO;
	private Date dataProcessamentPrimer;
	private Date dataProcessamentDarrer;
	private Date dataSignatRebutjat;
	private Date dataCustodiaIntent;
	private Date dataCustodiaOk;
	private Date dataSignalIntent;
	private Date dataSignalOk;
	private String errorCallbackProcessant;
	private String processInstanceId;
	private Long expedientId; 

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
}
