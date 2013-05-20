/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a gestionar la tramitació massiva de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioMassiva {

	public static final String SESSIO_TASQUES_TRAMITACIO = "HEL_TRAM_MASS_TASQUES";
	public static final String SESSIO_TASCA_ACTUAL = "HEL_TRAM_MASS_TASKID";
	public static final String SESSIO_TASCA_INICI = "HEL_TRAM_MASS_INICI";
	public static final String SESSIO_TASCA_CORREU = "HEL_TRAM_MASS_CORREU";

	public static void iniciarTramitacioMassiva(
			HttpServletRequest request,
			String id,
			String[] ids) {
		request.getSession().setAttribute(SESSIO_TASCA_ACTUAL, id);
		request.getSession().setAttribute(SESSIO_TASQUES_TRAMITACIO, ids);
	}
	public static String[] getTasquesTramitacioMassiva(
			HttpServletRequest request,
			String id) {
		if (isTramitacioMassivaActiu(request, id))
			return (String[])request.getSession().getAttribute(SESSIO_TASQUES_TRAMITACIO);
		else
			return null;
	}
	public static boolean isTramitacioMassivaActiu(
			HttpServletRequest request,
			String id) {
		String idActual = (String)request.getSession().getAttribute(SESSIO_TASCA_ACTUAL);
		if (idActual != null && idActual.equals(id))
			return request.getSession().getAttribute(SESSIO_TASQUES_TRAMITACIO) != null;
		else
			return false;
	}
	public static void setParamsTramitatcio(HttpServletRequest request, String inici, String correu, String id) {
		if (isTramitacioMassivaActiu(request, id)) {
			request.getSession().setAttribute(SESSIO_TASCA_INICI, inici);
			request.getSession().setAttribute(SESSIO_TASCA_CORREU, correu);
		}
	}
	public static String[] getParamsTramitacioMassiva(
			HttpServletRequest request,
			String id) {
		if (isTramitacioMassivaActiu(request, id))
			return new String[] {
				(String)request.getSession().getAttribute(SESSIO_TASCA_INICI),
				(String)request.getSession().getAttribute(SESSIO_TASCA_CORREU)};
		else
			return new String[2];
	}
	public static void netejarTramitacioMassiva(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSIO_TASCA_ACTUAL);
		request.getSession().removeAttribute(SESSIO_TASQUES_TRAMITACIO);
		request.getSession().removeAttribute(SESSIO_TASCA_INICI);
		request.getSession().removeAttribute(SESSIO_TASCA_CORREU);
	}

}
