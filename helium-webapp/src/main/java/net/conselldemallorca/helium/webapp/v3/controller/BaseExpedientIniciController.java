/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioAcceptarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador base per al controladors que inicien expedients.
 * - Inici sense dades
 * - Inici amb formulari de la 1a tasca
 * - Inici amb formulari demanant títol o número
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientIniciController extends BaseExpedientController {

	public static final String CLAU_SESSIO_ANY = "iniciexp_any";
	public static final String CLAU_SESSIO_TASKID = "iniciexp_taskId";
	public static final String CLAU_SESSIO_TITOL = "iniciexp_titol";
	public static final String CLAU_SESSIO_NUMERO = "iniciexp_numero";
	public static final String CLAU_SESSIO_FORM_VALIDAT = "iniciexp_form_validat";
	private static final String CLAU_SESSIO_FORM_COMMAND = "iniciexp_form_command";
	public static final String CLAU_SESSIO_FORM_VALORS = "iniciexp_form_registres";
	public static final String CLAU_SESSIO_ANOTACIO = "iniciexp_anotacio";
	private static final String CLAU_SESSIO_PREFIX_REGISTRE = "ExpedientIniciarController_reg_";

	@Autowired
	protected TascaService tascaService;

	@Autowired
	protected AnotacioService anotacioService;
	
	/** Mètode sincronitzat per iniciar un expedient compartit entre els controladors que inicien expedients. Posa el missatge comú d'expedient
	 * creat.
	 * @param request 
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @return
	 * 
	 * @throws Exception Hi poden haver excepcions no controlades amb sistemes externs.
	 */
	protected ExpedientDto iniciarExpedient (
			HttpServletRequest request,
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Integer any,
			Map<String, Object> valors,
			AnotacioAcceptarCommand anotacioAcceptarCommand) throws Exception  {
		
		ExpedientDto iniciat = expedientService.create(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				titol,
				null, null, null, null, false, null, null, null, null, null, null, false, null, null, false, 
				valors, 
				null,
				IniciadorTipusDto.INTERN,
				null, null, null, null,
				anotacioAcceptarCommand != null? anotacioAcceptarCommand.getId() : null,
				anotacioAcceptarCommand != null? anotacioAcceptarCommand.isAssociarInteressats() : false);

		MissatgesHelper.success(request, getMessage(request, "info.expedient.iniciat", new Object[] { iniciat.getIdentificador() }));
		if (anotacioAcceptarCommand != null) {
			AnotacioDto anotacio = anotacioService.findAmbId(anotacioAcceptarCommand.getId());
				MissatgesHelper.success(
						request, 
						getMessage(	request, 
									"anotacio.form.acceptar.incorporar.success",
									new Object[] { anotacio.getIdentificador(), iniciat.getNumeroIdentificador()}));
		}		
		// Esborra els valors temporals de la sessió
		netejarSessio(request);
			
		return iniciat;
	}
	
	/** Obté per a la select els anys seleccionables */
	public static List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			anys.add(new ParellaCodiValorDto(String.valueOf(anyActual - i), anyActual - i));
		}
		return anys;
	}
	
	@SuppressWarnings("unchecked")
	public static void netejarSessio(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(CLAU_SESSIO_PREFIX_REGISTRE))
				request.getSession().removeAttribute(atribut);
		}
		request.getSession().removeAttribute(CLAU_SESSIO_TASKID);
		request.getSession().removeAttribute(CLAU_SESSIO_NUMERO);
		request.getSession().removeAttribute(CLAU_SESSIO_TITOL);
		request.getSession().removeAttribute(CLAU_SESSIO_FORM_VALIDAT);
		request.getSession().removeAttribute(CLAU_SESSIO_FORM_COMMAND);
		request.getSession().removeAttribute(CLAU_SESSIO_FORM_VALORS);
		request.getSession().removeAttribute(CLAU_SESSIO_ANOTACIO);
	}

}
