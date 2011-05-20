/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar la georeferència de l'expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGeorefModificarHandler extends AbstractHeliumActionHandler {

	private String posx;
	private String varPosx;
	private String posy;
	private String varPosy;
	private String referencia;
	private String varReferencia;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
		String posx = (String)getValorOVariable(executionContext, this.posx, varPosx);
		String posy = (String)getValorOVariable(executionContext, this.posy, varPosy);
		String referencia = (String)getValorOVariable(executionContext, this.referencia, varReferencia);
		if (ex != null) {
			if (posx != null)
				ex.setGeoPosX(new Double(posx));
			if (posy != null)
				ex.setGeoPosY(new Double(posy));
			if (referencia != null)
				ex.setGeoReferencia(referencia);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				getExpedientService().editar(
						expedient.getEntorn().getId(),
						expedient.getId(),
						expedient.getNumero(),
						expedient.getTitol(),
						expedient.getIniciadorCodi(),
						expedient.getResponsableCodi(),
						expedient.getDataInici(),
						expedient.getComentari(),
						expedient.getEstat().getId(),
						(posx != null) ? new Double(posx) : null,
						(posy != null) ? new Double(posy) : null,
						referencia);
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar el número");
			}
		}
	}

	public void setPosx(String posx) {
		this.posx = posx;
	}
	public void setVarPosx(String varPosx) {
		this.varPosx = varPosx;
	}
	public void setPosy(String posy) {
		this.posy = posy;
	}
	public void setVarPosy(String varPosy) {
		this.varPosy = varPosy;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public void setVarReferencia(String varReferencia) {
		this.varReferencia = varReferencia;
	}

}
