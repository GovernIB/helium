package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.service.CampService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	protected boolean validaEsborratCamp(HttpServletRequest request, Long expedientTipusId, Long id) {
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
				// Esbrina de quines defincions són les tasques i construeix el missatge de validació
				List<String> tasquesList = new ArrayList<String>();
				DefinicioProcesDto definicio;
				ExpedientTipusDto expedientTipus;
				for (TascaDto tasca : tasques) {					
					definicio = definicioProcesService.tascaFindDefinicioProcesDeTasca(tasca.getId());
					expedientTipus = null;
					for (CampTascaDto ct : tasca.getCamps())
						if (ct.getCamp().getId().equals(id)){
							if (ct.getExpedientTipusId() != null)
								expedientTipus = expedientTipusService.findAmbId(ct.getExpedientTipusId());
							else 
								expedientTipus = definicio.getExpedientTipus();
						}
					tasquesList.add((expedientTipus != null ? expedientTipus.getCodi() + "." : "") + definicio.getIdPerMostrar() + "." + tasca.getJbpmName());
				}
				MissatgesHelper.error(
						request, 
						getMessage(
								request,
								"expedient.tipus.camp.llistat.accio.esborrar.validacio.tasca",
								new Object[] {llistaToString(tasquesList)}));
			}
			valid = false;
		}
		// Valida que no pertany a cap consulta
		List<ConsultaDto> consultes = campService.findConsultesPerCamp(expedientTipusId, id);
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
		List<String> textList = new ArrayList<String>();
		String method = "get" + propietat.substring(0, 1).toUpperCase() + propietat.substring(1);
		if (objectes != null && objectes.size() > 0)
			for (T o : objectes) {
				try {
					// crida el .getCodi sobre l'objecte
					textList.add(o.getClass().getDeclaredMethod(method).invoke(o).toString());
				} catch (Exception e) {
					logger.error("Error consultant el \"getCodi\" de l'objecte " + o, e);
				}
			}
		return llistaToString(textList);
	}	
	
	/** Mètode per retornar un string amb una llista separada per comes */
	private String llistaToString(List<String> textList) {
		StringBuilder str = new StringBuilder();
		int N = textList.size();
		int i = 1;
		for (String text : textList) {
			str.append(text);
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
