/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraEnumDto;

/**
 * Objecte de domini que representa l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitSistraExportacio implements Serializable {

	private Long id;
	private String sistraTramitCodi;
	private TramitSistraEnumDto tipus;
	private String codiVarIdentificadorExpedient;
	private AccioExportacio accio;
	private List<MapeigSistraExportacio> mapeigSistras = new ArrayList<MapeigSistraExportacio>();

	public TramitSistraExportacio() {}
	
	public TramitSistraExportacio(
			String sistraTramitCodi, 
			TramitSistraEnumDto tipus, 
			String codiVarIdentificadorExpedient, 
			AccioExportacio accio, 
			List<MapeigSistraExportacio> mapeigSistras) {
		this.sistraTramitCodi = sistraTramitCodi;
		this.tipus = tipus;
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
		this.mapeigSistras = mapeigSistras;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}

	public TramitSistraEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TramitSistraEnumDto tipus) {
		this.tipus = tipus;
	}

	public String getCodiVarIdentificadorExpedient() {
		return codiVarIdentificadorExpedient;
	}
	public void setCodiVarIdentificadorExpedient(String codiVarIdentificadorExpedient) {
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
	}

	public AccioExportacio getAccio() {
		return accio;
	}
	public void setAccio(AccioExportacio accio) {
		this.accio = accio;
	}

	public List<MapeigSistraExportacio> getMapeigSistras() {
		return mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistraExportacio> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}

	private static final long serialVersionUID = 1L;

}
