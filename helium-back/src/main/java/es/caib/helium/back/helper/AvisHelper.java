package es.caib.helium.back.helper;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.helium.commons.dto.AvisDto;
import es.caib.helium.logic.intf.service.AvisService;

/**
 * Utilitat per obtenir els avisos de sessiÃ³..
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";


	@SuppressWarnings("unchecked")
	public static void findAvisos(
			HttpServletRequest request, 
			AvisService avisService) {
		
		List<AvisDto> avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		if (avisos == null && /*!RequestHelper.isError(request) && */ avisService != null) {
			avisos = avisService.findActive();
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	

}
