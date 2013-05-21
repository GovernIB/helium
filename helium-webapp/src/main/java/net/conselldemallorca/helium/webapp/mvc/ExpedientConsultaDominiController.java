/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per fer consultes als dominis de dins d'un
 * expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientConsultaDominiController extends BaseController {

	private DissenyService dissenyService;



	@Autowired
	public ExpedientConsultaDominiController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@RequestMapping(value = "/domini/consultaExpedient")
	public String consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "campCodi", required = true) String campCodi,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "tipus", required = true) String tipus,
			@RequestParam(value = "valors", required = false) String valors,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				model.addAttribute(
						"camp",
						dissenyService.findCampAmbDefinicioProcesICodi(definicioProcesId, campCodi));
				List<FilaResultat> resultat = dissenyService.getResultatConsultaCamp(
						taskId,
						processInstanceId,
						definicioProcesId,
						campCodi,
						textInicial,
						getMapDelsValors(valors));
				for (FilaResultat filaResultat: resultat) {
					for (ParellaCodiValor codiValor: filaResultat.getColumnes()) {
						if (codiValor.getValor() instanceof String) {
							String valor = (String)codiValor.getValor();
							codiValor.setValor(valor.replaceAll("\\p{Cntrl}", "").trim());
						}
					}
				}
				model.addAttribute("resultat", resultat);
				/*
				List<FilaResultat> f = new ArrayList<FilaResultat>();
				f = dissenyService.getResultatConsultaCamp(
						taskId,
						processInstanceId,
						definicioProcesId,
						campCodi,
						textInicial,
						getMapDelsValors(valors));
				int indexFila = 0;
				for (FilaResultat fila: f) {
					System.out.println(">>> Fila: " + indexFila++);
					int indexColumna = 0;
					for (ParellaCodiValor columna: fila.getColumnes()) {
						System.out.println(">>>>>> Columna: " + indexColumna++ + ": [" + columna.getCodi() + ", " + columna.getValor() + "]");
					}
				}
				*/
			} catch (Exception ex) {
				logger.error("Error en la consulta de domini pel camp " + campCodi, ex);
			}
			if (tipus.equals("select"))
				return "domini/consultaCampSelect";
			else
				return "domini/consultaCampSuggest";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(ExpedientConsultaDominiController.class);

}
