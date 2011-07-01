package net.conselldemallorca.helium.webapp.dwr;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.interceptor.EntornInterceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				FormulariExtern formExtern = tascaService.iniciarFormulariExtern(
						entorn.getId(),
						taskId);
				String[] resposta = new String[] {
						formExtern.getUrl(),
						Integer.toString(formExtern.getFormWidth()),
						Integer.toString(formExtern.getFormHeight())};
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
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				FormulariExtern formExtern = tascaService.iniciarFormulariExtern(
						taskId,
						expedientTipusId,
						definicioProcesId);
				String[] resposta = new String[] {
						formExtern.getUrl(),
						Integer.toString(formExtern.getFormWidth()),
						Integer.toString(formExtern.getFormHeight())};
				return resposta;
			} catch (Exception ex) {
				logger.error("No s'ha pogut iniciar el formulari extern", ex);
			}
		}
		return null;
	}

	protected Entorn getEntornActiu(
			HttpServletRequest request) {
		return (Entorn)request.getSession().getAttribute(EntornInterceptor.VARIABLE_SESSIO_ENTORN_ACTUAL);
	}

	private static final Log logger = LogFactory.getLog(FormulariExternDwrService.class);

}
