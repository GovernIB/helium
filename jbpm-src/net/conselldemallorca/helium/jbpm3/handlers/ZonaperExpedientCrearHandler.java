/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
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
			getExpedientService().publicarExpedient(
					ex,
					getPublicarExpedientRequest(ex, desc));
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			getExpedientService().publicarExpedient(
					expedient,
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
			String descripcio) throws JbpmException {
		if (!IniciadorTipus.SISTRA.equals(expedient.getIniciadorTipus()))
			throw new JbpmException("Aquest expedient no prové d'un tramit de SISTRA");
		if (!expedient.isAvisosHabilitats())
			throw new JbpmException("El tràmit no té els avisos habilitats");
		PublicarExpedientRequest request = new PublicarExpedientRequest();
		request.setIdioma(expedient.getIdioma());
		request.setDescripcio(descripcio);
		request.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		request.setTramitNumero(expedient.getNumeroEntradaSistra());
		request.setAutenticat(expedient.isAutenticat());
		if (expedient.isAutenticat()) {
			if (expedient.getRepresentantNif() != null) {
				request.setRepresentantNif(expedient.getRepresentantNif());
				request.setRepresentatNif(expedient.getInteressatNif());
				request.setRepresentatNom(expedient.getInteressatNom());
			} else if (expedient.getInteressatNif() != null) {
				request.setRepresentantNif(expedient.getInteressatNif());
			} else {
				request.setRepresentantNif(expedient.getTramitadorNif());
			}
		}
		return request;
	}

}
