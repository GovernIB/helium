/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear expedients a dins la zona personal del
 * ciutadà.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientCrearHandler extends AbstractHeliumActionHandler {

	private String descripcio;
	private String varDescripcio;



	public void execute(ExecutionContext executionContext) throws Exception {
		String desc = (String)getValorOVariable(
				executionContext,
				descripcio,
				varDescripcio);
		Expedient ex = ExpedientIniciant.getExpedient();
		if (ex != null) {
			getPluginTramitacioDao().publicarExpedient(
					getPublicarExpedientRequest(ex, desc));
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			getPluginTramitacioDao().publicarExpedient(
					getPublicarExpedientRequest(expedient, desc));
		}
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public void setVarDescripcio(String varDescripcio) {
		this.varDescripcio = varDescripcio;
	}



	private PublicarExpedientRequest getPublicarExpedientRequest(
			Expedient expedient,
			String descripcio) {
		PublicarExpedientRequest request = new PublicarExpedientRequest();
		request.setExpedientIdentificador(expedient.getNumeroIdentificador());
		request.setIdioma(expedient.getIdioma());
		request.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		request.setDescripcio(descripcio);
		request.setTramitNumero(expedient.getNumeroEntradaSistra());
		request.setAutenticat(expedient.isAutenticat());
		if (expedient.isAutenticat()) {
			if (expedient.getRepresentantNif() != null) {
				request.setRepresentantNif(expedient.getRepresentantNif());
				request.setRepresentatNif(expedient.getInteressatNif());
				request.setRepresentatNom(expedient.getInteressatNom());
			} else {
				request.setRepresentantNif(expedient.getInteressatNif());
			}
		}
		return request;
	}

}
