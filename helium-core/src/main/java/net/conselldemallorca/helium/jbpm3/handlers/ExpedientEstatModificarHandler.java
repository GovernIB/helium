/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientEstatModificarHandler extends AbstractHeliumActionHandler implements ExpedientEstatModificarHandlerInterface {

	private String estatCodi;
	private String varEstatCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciantDto.getExpedient();
		String ec = (String)getValorOVariable(executionContext, estatCodi, varEstatCodi);
		if (ex != null) {
			Estat estat = getEstatPerExpedientTipusICodi(
					ex.getTipus().getId(),
					ec);
			if (estat != null) {
				logger.info("Informació modificació estat: " + ec + ", " + ex.getId() + ", " + ex.getTipus().getId());
				ex.setEstat(estat);
			} else {
				logger.info("Informació modificació estat: " + ec + ", " + ex.getId() + ", " + ex.getTipus().getId());
				throw new JbpmException("No existeix cap estat amb el codi '" + ec + "'");
			}
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				Estat estat = getEstatPerExpedientTipusICodi(
						expedient.getTipus().getId(),
						ec);
				if (estat != null) {
					logger.info("Informació modificació estat: " + ec + ", " + expedient.getId() + ", " + expedient.getTipus().getId());
					getExpedientService().editar(
							expedient.getEntorn().getId(),
							expedient.getId(),
							expedient.getNumero(),
							expedient.getTitol(),
							expedient.getIniciadorCodi(),
							expedient.getResponsableCodi(),
							expedient.getDataInici(),
							expedient.getComentari(),
							estat.getId(),
							expedient.getGeoPosX(),
							expedient.getGeoPosY(),
							expedient.getGeoReferencia());
				} else {
					logger.info("Informació modificació estat: " + ec + ", " + expedient.getId() + ", " + expedient.getTipus().getId());
					throw new JbpmException("No existeix cap estat amb el codi '" + ec + "'");
				}
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar l'estat");
			}
		}
	}

	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public void setVarEstatCodi(String varEstatCodi) {
		this.varEstatCodi = varEstatCodi;
	}



	private static final Log logger = LogFactory.getLog(ExpedientEstatModificarHandler.class);

}
