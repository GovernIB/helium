/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el número d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientNumeroModificarHandler extends AbstractHeliumActionHandler implements ExpedientNumeroModificarHandlerInterface {

	private String numero;
	private String varNumero;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciantDto.getExpedient();
		String n = (String)getValorOVariable(executionContext, numero, varNumero);
		if (ex != null) {
			ExpedientDto expedientRepetit = getExpedientService().findExpedientAmbEntornTipusINumero(
					ex.getEntorn().getId(),
					ex.getTipus().getId(),
					n);
			if (expedientRepetit == null) {
				ex.setNumero(n);
			} else {
				throw new JbpmException("Ja existeix un altre expedient del tipus " + ex.getTipus().getCodi() + " amb el número " + n);
			}
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				ExpedientDto expedientRepetit = getExpedientService().findExpedientAmbEntornTipusINumero(
						expedient.getEntorn().getId(),
						expedient.getTipus().getId(),
						n);
				if (expedientRepetit == null) {
					getExpedientService().editar(
							expedient.getEntorn().getId(),
							expedient.getId(),
							n,
							expedient.getTitol(),
							expedient.getResponsableCodi(),
							expedient.getDataInici(),
							expedient.getComentari(),
							(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
							expedient.getGeoPosX(),
							expedient.getGeoPosY(),
							expedient.getGeoReferencia());
				} else {
					throw new JbpmException("Ja existeix un altre expedient d'aquest tipus (" + expedient.getTipus().getCodi() + ") amb el número " + n);
				}
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar el número");
			}
		}
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setVarNumero(String varNumero) {
		this.varNumero = varNumero;
	}

}
