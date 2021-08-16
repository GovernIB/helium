/** HERÈNCIA
 * 
 */
package es.caib.helium.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.DadaIndexadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.service.ExpedientDadaService;
import es.caib.helium.logic.intf.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina d'informació de l'expedient amb les dades de Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/lucene")
public class ExpedientLuceneController extends BaseExpedientController {
	
	@Autowired
	private ExpedientDadaService expedientDadaService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		ExpedientDto expedient = null;
		try {
			expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute("expedient", expedient);
			this.omplirModelDades(request, model, expedient);
			return "v3/expedientLucene";
		} catch (Exception e) {
			String errMsg = "Error consultant les dades indexades de l'expedient amb id " + expedientId + ": " + e.getMessage();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			return "redirect:/v3/expedient" + (expedient != null ? "/" + expedient.getId() : "");
		}
	}
	
	/**
	 *  Consulta les variables de l'expedient, les dades indexades pels camps definits a tipus de definició de procés i crea una llista  de Map<codi, valor> 
	 *  amb els valors per construir la llista al model.
	 * 
	 * @param request
	 * @param model
	 * @param expedient
	 */
	private void omplirModelDades(HttpServletRequest request, Model model, ExpedientDto expedient) {
		// Dades pel model
		List<Map<String, Object>> dades = new ArrayList<Map<String, Object>>();
		
		// Consulta les dades indexades de l'expedient
		List<Map<String, DadaIndexadaDto>> dadesLucene = expedientService.luceneGetDades(expedient.getId());
		if (dadesLucene.size() > 0) {
			// Si hi ha més d'una entrada a líndex avisa
			if (dadesLucene.size() > 1) {
				MissatgesHelper.error(request, getMessage(request, "expedient.lucene.error.varis", new Object[] {dadesLucene.size(), expedient.getId()}));
			}
			// Map<codi, DadaIndexada>
			Map<String, DadaIndexadaDto> dadesIndexades = dadesLucene.get(0);
						
			// Map<String, ExpedientDadaDto> amb totes les variables de l'expedient
			Map<String, ExpedientDadaDto> dadesExpedient = new HashMap<String, ExpedientDadaDto>();
			for(ExpedientDadaDto dadaExpedient : expedientDadaService.findAmbInstanciaProces(	expedient.getId(),
																								expedient.getProcessInstanceId())) {
				dadesExpedient.put(dadaExpedient.getVarCodi(), dadaExpedient);
			}

			// Map<codi, CampDto> amb tots els camps definits pel tipus d'expedient
			// Conté la informació per completar les dades i serveix per saber si s'ha tractat ja
			Map<String, CampDto> camps = new HashMap<String, CampDto>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedient.getProcessInstanceId());
			for (CampDto camp : dissenyService.findCampsOrdenatsPerCodi(expedient.getTipus().getId(),
																		instanciaProces.getDefinicioProces().getId(),
																		true)) {
				camps.put(camp.getCodi(), camp);
			}

			// Crea la llista de dades pel model, primer recorre les dades indexades i després
			// les de l'expedient que queden per processar
			int nErrorsSincronitzacio = 0;
			int nValorsDiferents = 0;
			// Converteix lada dada amb errors de reindexació en un map<codi, error>
			Map<String, String> errorsReindexacio;
			try {
				errorsReindexacio = dadesIndexades.containsKey(Constants.EXPEDIENT_CAMP_ERRORS_REINDEXACIO.replace('$', '%')) ?
						new ObjectMapper().readValue(dadesIndexades.get(Constants.EXPEDIENT_CAMP_ERRORS_REINDEXACIO.replace('$', '%')).toString(), HashMap.class)
						: new HashMap<String, String>();
			} catch(Exception e) {
				String errMsg = "Error llegint els errors de sincronització de l'índex per l'expedient " + expedient.getId() + ": " + e.getMessage();
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg);
				errorsReindexacio = new HashMap<String, String>();
			}
			
			// 1 Recorre les dades indexades
			for (DadaIndexadaDto dadaIndexada : dadesIndexades.values()) {
				Map<String, Object> dada = new HashMap<String, Object>();
				String codi = dadaIndexada.getCampCodi();
				dada.put("codi", codi);
				// Tipus expedient EX, variable TE o variable DP
				String tipus;
				if (dadaIndexada.getCampCodi().startsWith(Constants.EXPEDIENT_PREFIX))
					tipus = "EX";
				else 
					tipus = dadaIndexada.isDadaExpedient() ? "TE" : "DP";
				dada.put("tipus", tipus);
				dada.put("etiqueta", dadaIndexada.getEtiqueta());
				dada.put("valorIndex", dadaIndexada.getValorIndex());
				dada.put("valorMostrar", dadaIndexada.getValorMostrar());
				dada.put("definicioProces", dadaIndexada.getDefinicioProcesCodi());
				
				// Errors del camp
				String errorReindexacio = null;
				if (dadaIndexada.getError() != null)
					errorReindexacio = dadaIndexada.getError();
				else if (errorsReindexacio.containsKey(dadaIndexada.getCampCodi()))
					errorReindexacio = errorsReindexacio.get(dadaIndexada.getCampCodi());
				if (errorReindexacio != null && !errorReindexacio.isEmpty()) {
					dada.put("errorReindexacio", errorReindexacio);
					nErrorsSincronitzacio++;
				}
				// Dades de la tasca
				if (!tipus.equals("EX")) {
					if (camps.containsKey(dadaIndexada.getCampCodi())) {
						CampDto camp = camps.get(codi);
						if (camp.getAgrupacio() != null)
							dada.put("agrupacio", camp.getAgrupacio().getCodi());
						dada.put("multiple", camp.isMultiple());
						String valorIndexat = String.valueOf(dadaIndexada.getValorMostrar());
						String valorJbpm;
						ExpedientDadaDto expedientDada = dadesExpedient.get(codi);
						if (expedientDada != null) {
							valorJbpm = expedientDada.getText();
						} else {
							valorJbpm = null;
						}
						boolean valorDiferent = !valorIndexat.equals(valorJbpm); 
						if (valorDiferent) {
							nValorsDiferents++;
						}
						dada.put("valorDiferent", valorDiferent);
						dada.put("valorJbpm", valorJbpm);						
						// Treu la variable de la llista per no tornar a tractar-la
						camps.remove(dadaIndexada.getCampCodi());
					}
				}
				dades.add(dada);
			}
			// 2 Recorre les dades de l'expedient que no han estat reindexades
			for (ExpedientDadaDto dadaExpedient : dadesExpedient.values()) {
				String codi = dadaExpedient.getVarCodi();
				// Comprova si està en la llista de camps que no s'han tractat i que no estan
				// indexades i que té un valor jbpm
				if (camps.containsKey(codi) && dadaExpedient.getVarValor() != null) {
					Map<String, Object> dada = new HashMap<String, Object>();
					dada.put("codi", codi);
					CampDto camp = camps.get(codi);
					// Tipus expedient EX, variable TE o variable DP
					dada.put("tipus", camp.getDefinicioProces() == null ? "TE" : "DP");
					dada.put("etiqueta", camp.getEtiqueta());
					dada.put("definicioProces", camp.getDefinicioProces() != null ? camp.getDefinicioProces().getJbpmKey() : null);
					if (camp.getAgrupacio() != null)
						dada.put("agrupacio", camp.getAgrupacio().getCodi());
					dada.put("multiple", camp.isMultiple());

					// Errors del camp
					String errorReindexacio;
					if (errorsReindexacio.containsKey(codi))
						errorReindexacio = errorsReindexacio.get(codi);
					else
						errorReindexacio = "El camp '" + codi + "' no està a l'índex de l'expedient"; 
					String valorJbpm = dadaExpedient.getText();
					dada.put("valorJbpm", valorJbpm);						
					if (errorReindexacio != null && !errorReindexacio.isEmpty()) {
						dada.put("errorReindexacio", errorReindexacio);
						nErrorsSincronitzacio++;
					} else if (!valorJbpm.isEmpty()){
						nValorsDiferents++;
						dada.put("valorDiferent", true);
					}

					dades.add(dada);					
				}
			}
			
			if (nErrorsSincronitzacio > 0)
				MissatgesHelper.error(request, "S'han trobat " + nErrorsSincronitzacio + " camps amb error de sincronització");
			if (nValorsDiferents > 0)
				MissatgesHelper.warning(request, "Hi ha " + nValorsDiferents + " camps que tenen un valor indexat diferent al valor de l'expedient. Podeu revisar-los i reindexeu si ho troveu adient.");
		} else {
			MissatgesHelper.warning(request, "No s'han trobat dades indexades per l'expedient");
		}
		model.addAttribute("dades", dades);
	}


	@RequestMapping(value = "/{expedientId}/reindexa", method = RequestMethod.GET)
	public String reindexa(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		try {
			if (expedientService.luceneReindexarExpedient(expedientId))
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"info.expedient.reindexat"));
			else
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"info.expedient.reindexat.error"));
				
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reindexar.expedient") + ". " + ex.getMessage());
		}
		return "redirect:/modal/v3/expedient/lucene/" + expedientId;
	}

	
	private static final Log logger = LogFactory.getLog(ExpedientLuceneController.class);	
}
