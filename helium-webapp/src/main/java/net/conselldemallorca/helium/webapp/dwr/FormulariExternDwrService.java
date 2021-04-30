package net.conselldemallorca.helium.webapp.dwr;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Servei DWR per a l'inici de formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FormulariExternDwrService {

	private TascaService tascaService;

	@Autowired
	public FormulariExternDwrService(TascaService tascaService) {
		this.tascaService = tascaService;
	}

	public String[] dadesIniciFormulari(
			HttpServletRequest request,
			String taskId) {
		Entorn entorn = getEntornActual(request);
		if (entorn != null) {
			try {
				FormulariExternDto formExtern = tascaService.formulariExternObrir(taskId);
				String[] resposta = new String[] {
						formExtern.getUrl(),
						Integer.toString(formExtern.getWidth()),
						Integer.toString(formExtern.getHeight())};
				return resposta;
			} catch (Exception ex) {
				logger.error("No s'ha pogut iniciar el formulari extern", ex);
			}
		}
		return null;
	}

	public String[] dadesIniciFormulariInicial(
			HttpServletRequest request,
			String taskId,
			Long expedientTipusId,
			Long definicioProcesId) {
		Entorn entorn = getEntornActual(request);
		if (entorn != null) {
			try {
				FormulariExternDto formExtern = tascaService.formulariExternObrirTascaInicial(
						taskId,
						expedientTipusId,
						definicioProcesId);
				String[] resposta = new String[] {
						formExtern.getUrl(),
						Integer.toString(formExtern.getWidth()),
						Integer.toString(formExtern.getHeight())};
				return resposta;
			} catch (Exception ex) {
				logger.error("No s'ha pogut iniciar el formulari extern", ex);
			}
		}
		return null;
	}

	protected Entorn getEntornActual(
			HttpServletRequest request) {
		return (Entorn)request.getSession().getAttribute(
				SessionHelper.VARIABLE_ENTORN_ACTUAL);
	}

	private static final Log logger = LogFactory.getLog(FormulariExternDwrService.class);

}
