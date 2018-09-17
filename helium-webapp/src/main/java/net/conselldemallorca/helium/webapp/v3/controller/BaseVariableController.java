package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

/**
 * Controlador base per als controladors de les variables del tipus d'expedient
 * i de les definicions de procés.
 * 
 */
public class BaseVariableController extends BaseDissenyController {
	
	public final Long AGRUPACIO_TOTES = -2L;	
	public final Long AGRUPACIO_SENSE = -1L;	
	
	@Autowired
	protected CampService campService;	
	
	/** Mètode per validar si una variable (camp) es pot esborrar o s'utilitza en registres,
	 * tasques o consultes.
	 */
	protected boolean validaEsborratCamp(HttpServletRequest request, Long id) {
		// Valida que la variable no s'utilitzi en cap registre o consulta
		boolean valid = true;
		// Recupera la informació del camp
		CampDto camp = campService.findAmbId(null, id);
		// Valida que no pertany a cap tasca
		List<TascaDto> tasques = campService.findTasquesPerCamp(id);
		if (tasques.size() > 0) {
			if( camp.getExpedientTipus() == null)
				MissatgesHelper.error(
						request, 
						getMessage(
								request,
								"expedient.tipus.camp.llistat.accio.esborrar.validacio.tasca",
								new Object[] {llistaToString(tasques, "jbpmName")}));
			else 
			{
				// Esbrina de quines defincions són les tasques
				List<DefinicioProcesDto> definicionsProces = new ArrayList<DefinicioProcesDto>();
				for (TascaDto tasca : tasques)
					definicionsProces.add(definicioProcesService.tascaFindDefinicioProcesDeTasca(tasca.getId()));
				
				MissatgesHelper.error(
						request, 
						getMessage(
								request,
								"expedient.tipus.camp.llistat.accio.esborrar.validacio.tasca.definicions",
								new Object[] {
										llistaToString(tasques, "jbpmName"),
										llistaToString(definicionsProces, "jbpmKey")}));
			}
			valid = false;
		}
		// Valida que no pertany a cap consulta
		List<ConsultaDto> consultes = campService.findConsultesPerCamp(id);
		if (consultes.size() > 0) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.validacio.consulta",
							new Object[] {llistaToString(consultes, "codi")}));			
			valid = false;
		}
		// Valida que no pertany a cap registre
		List<CampDto> registres = campService.findRegistresPerCamp(id);
		if (registres.size() > 0) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"expedient.tipus.camp.llistat.accio.esborrar.validacio.registre",
							new Object[] {llistaToString(registres, "codi")}));			
			valid = false;
		}
		return valid;
	}

	/** Mètode comú per fer una llista amb l'atribut codi de la llista passada com a paràmetre. Retorna
	 * un string amb els codis separats per coma ",".
	 * @param objectes
	 * @return
	 */
	private <T> String llistaToString(List<T> objectes, String propietat) {
		StringBuilder str = new StringBuilder();
		int N = objectes.size();
		int i = 1;
		String method = "get" + propietat.substring(0, 1).toUpperCase() + propietat.substring(1);
		if (objectes != null && N > 0)
			for (T o : objectes) {
				try {
					// crida el .getCodi sobre l'objecte
					str.append(o.getClass().getDeclaredMethod(method).invoke(o).toString());
				} catch (Exception e) {
					logger.error("Error consultant el \"getCodi\" de l'objecte " + o, e);
				}
				if (i++ < N)
					str.append(", ");
			}
		return str.toString();
	}	
	
	protected void omplirModelAgrupacions(
			HttpServletRequest request,
			Long expedientTipusId,
			Long definicioProcesId,
			Model model,
			boolean herencia) {
		model.addAttribute("agrupacions", obtenirParellesAgrupacions(request, expedientTipusId, definicioProcesId, herencia));		
	}
	
	/** Obté la llista de parelles codi-valor per les possibles agrupacions. A més també afegeix a l'inici
	 * les opcions de totes les variables (-2) o sensa grupació (-1).
	 * @param request
	 * @param expedientTipusId
	 * @param herencia
	 * 				Indica si incloure els resultats de les possibles agrupacions heretades pel tipus d'expedient.
	 * @return
	 */
	protected List<ParellaCodiValorDto> obtenirParellesAgrupacions(
			HttpServletRequest request, 
			Long expedientTipusId,
			Long definicioProcesId,
			boolean herencia) {
		List<CampAgrupacioDto> agrupacions = campService.agrupacioFindAll(expedientTipusId, definicioProcesId, herencia);
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(AGRUPACIO_TOTES.toString(), "[ " + getMessage(request, "expedient.tipus.camp.llistat.agrupacio.opcio.totes") + " ]"));
		resposta.add(new ParellaCodiValorDto(AGRUPACIO_SENSE.toString(), "[ " + getMessage(request, "expedient.tipus.camp.llistat.agrupacio.opcio.sense") + " ]"));
		for (CampAgrupacioDto agrupacio : agrupacions) {
			resposta.add(new ParellaCodiValorDto(agrupacio.getId().toString(), agrupacio.getNom()));
		}
		return resposta;
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}	
	
	private static final Log logger = LogFactory.getLog(BaseVariableController.class);
	
}
