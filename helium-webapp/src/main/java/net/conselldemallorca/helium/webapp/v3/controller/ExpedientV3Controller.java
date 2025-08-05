/** HERÈNCIA
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentFinalitzarDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientFinalitzarDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.CanviVersioProcesCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientRegistreService expedientRegistreService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Resource
	private ExpedientHelper expedientHelper;
	@Autowired
	private AplicacioService aplicacioService;

	/** Per donar format a les dates sense haver d'instanciar l'objecte cada cop. */
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		return mostrarInformacioExpedientPerPipella(
				request,
				expedientId,
				model,
				null);
	}
	
	@RequestMapping(value = "/proces/{processInstanceId}", method = RequestMethod.GET)
	public String infoProces(
			HttpServletRequest request, 
			@PathVariable String processInstanceId, 
			Model model) {
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		if (expedientId == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"error.expedientService.noExisteix"));
			return "redirect:/v3/expedient";
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId) {

		try {
			expedientService.delete(expedientId);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.expedient.esborrat"));
			return "redirect:/v3/expedient";
		} catch (Exception e) {
			String errMsg = getMessage(request, "error.esborrant.expedient", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
			String referer = request.getHeader("Referer");
		    return "redirect:"+ referer;
		}			
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
			MissatgesHelper.error(
					request,
					getMessage(request, "error.reindexar.expedient") + ". " + ex.getMessage(),
					ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	/** Mètode per finalitzar un expedient. Es crida al mètode de servei de finalitzar. Els expedients
	 * integrats amb l'Arxiu passen pel mètode prefinalitzar que permet escollir quins documents signar.
	 */
	@RequestMapping(value = "/{expedientId}/finalitzar", method = RequestMethod.GET)
	public String finalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.finalitzar(expedientId);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.expedient.finalitzat"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "expedient.error.finalitzant.expedient") + ". " + ex.getMessage();
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, 
					errMsg.substring(
							0, 
							Math.min(errMsg.contains("\n") ? errMsg.indexOf("\n") : errMsg.length(), 1024)),
					ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	/** Modal expedients integrats amb l'Arxiu que permet seleccionar quins documents pendents de firma firmar i guardar 
	 * a l'Arxiu com a definitius abans de finalitzar.
	 */
	@RequestMapping(value = "/{expedientId}/prefinalitzar", method = RequestMethod.GET)
	public String prefinalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		ExpedientFinalitzarDto expedientFinalitzarDto = new ExpedientFinalitzarDto();
		try {
			expedientFinalitzarDto = expedientDocumentService.findDocumentsFinalitzar(expedientId);
		} catch (Exception ex) {
			String errMsg = getMessage(request, "expedient.error.prefinalitzant.expedient") + ". " + ex.getMessage();
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg, ex);
			expedientFinalitzarDto.setError(true);
		}
		model.addAttribute(expedientFinalitzarDto);
		return "v3/expedient/prefinalitzar";
	}
	
	@RequestMapping(value = "/{expedientId}/prefinalitzar", method = RequestMethod.POST)
	public  String prefinalitzarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute("expedientFinalitzarDto") ExpedientFinalitzarDto expedientFinalitzarDto,
			Model model) {
		boolean error = false;
		try {
			// 1- firma els seleccionats
			if (expedientFinalitzarDto.getDocumentsFinalitzar()!=null &&
				expedientFinalitzarDto.getExpedient().isArxiuActiu() && 
				expedientFinalitzarDto.getExpedient().getArxiuUuid() != null) {
					for (DocumentFinalitzarDto dfDto: expedientFinalitzarDto.getDocumentsFinalitzar()) {
						if (dfDto.isSeleccionat()) {
							String document = (dfDto.isAdjunt() ?  "l'adjunt \"" : "el document \"" ) + dfDto.getDocumentCodi() + "\"";  
							try {
								expedientHelper.firmarDocumentServidorPerArxiuFiExpedient(dfDto.getDocumentStoreId());
								MissatgesHelper.success(request, getMessage(request, "expedient.prefinalitzar.document.firmat", new Object[]{document} ));
							} catch (Exception ex) {
								String errMsg = ex.getMessage();
								if (errMsg!=null) {
									errMsg = errMsg.substring(
											0, 
											Math.min(errMsg.contains("\n") ? errMsg.indexOf("\n") : errMsg.length(), 1024));
								}
								MissatgesHelper.error(
										request,
										getMessage(request, "expedient.prefinalitzar.document.error", new Object[]{document, errMsg} ),
										ex);
								error = true;
							}
						}
					}
			}
			
			// 2- Si s'han pogut firmar correctament i l'acció és de finalitzar llavors es procedeix a validar i finalitzar l'expedient
			if (!error) {
				if ("finalitzar".equals(expedientFinalitzarDto.getAccio()) 
						&& expedientDocumentService.validarFinalitzaExpedient(expedientId)) 
				{
					expedientHelper.finalitzar(expedientId, false);
					MissatgesHelper.success(request, getMessage(request, "expedient.prefinalitzar.finalitzat"));
				} else {
					MissatgesHelper.success(request, getMessage(request, "expedient.prefinalitzar.documents.firmats"));
				}
			}
		} catch (Exception ex) {
			String errMsg = getMessage(request, "expedient.error.prefinalitzant.expedient") + ". " + ex.getMessage();
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, 
					errMsg.substring(
							0, 
							Math.min(errMsg.contains("\n") ? errMsg.indexOf("\n") : errMsg.length(), 1024)),
					ex);
			
			error = true;
		}
		if (error) {
			model.addAttribute(expedientFinalitzarDto);
			return "redirect:" + request.getHeader("referer");			
		} else {
			return modalUrlTancar(true);
		}
	}
	
	@RequestMapping(value = "/{expedientId}/alertes", method = RequestMethod.GET)
	public String alertes(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		List<AlertaDto> alertes = expedientService.findAlertes(expedientId);
		Map<String, String> persones = getNomPersonaPerAlertes(alertes);
		
		model.addAttribute("expedientId", expedientId);		
		model.addAttribute("alertes",alertes);
		model.addAttribute("persones", persones);
		return "v3/expedient/alertes";
	}
	
	private Map<String, String> getNomPersonaPerAlertes(List<AlertaDto> alertes) {
		Map<String, String> resposta = new HashMap<String, String>();
		for (AlertaDto alerta: alertes) {
			if (alerta.getDestinatari() != null && resposta.get(alerta.getDestinatari()) == null) {
				PersonaDto persona = aplicacioService.findPersonaAmbCodi(alerta.getDestinatari());
				if (persona != null)
					resposta.put(persona.getCodi(), persona.getNomSencer());
			}
		}
		return resposta;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{expedientId}/errors", method = RequestMethod.GET)
	public String errors(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		Object[] errors = expedientService.findErrorsExpedient(expedientId);
		
		List<ExpedientErrorDto> errors_bas = (List<ExpedientErrorDto>) errors[0];
		List<ExpedientErrorDto> errors_int = (List<ExpedientErrorDto>) errors[1];
		
		model.addAttribute("expedientId", expedientId);		
		model.addAttribute("errors_bas",errors_bas);
		model.addAttribute("errors_int",errors_int);
		return "v3/expedient/errors";
	}
	
	@RequestMapping(value = "/{expedientId}/netejarErrorsExp", method = RequestMethod.GET)
	@ResponseBody
	public String netejarErrorsExp(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		try {
			expedientService.netejarErrorsExp(expedientId);
			MissatgesHelper.success(request, getMessage(request, "boto.eliminar.errors.ok"));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(request, "boto.eliminar.errors.ko") + "<br/> " + ex.getCause().getMessage(),
					ex);
		}
		return "ok";
	}

	@RequestMapping(value = "/{expedientId}/imatgeDefProces", method = RequestMethod.GET)
	public String imatgeProces(
			HttpServletRequest request,
			@PathVariable(value = "expedientId") Long expedientId, 
			Model model) {
		ArxiuDto imatge = expedientService.getImatgeDefinicioProces(
				expedientId,
				null);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				imatge.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				imatge.getContingut());
		return "arxiuView";
	}
	
	@RequestMapping(value = "/{expedientId}/canviVersio", method = RequestMethod.GET)
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
			List<DefinicioProcesExpedientDto> subDefinicioProces = dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId());
			CanviVersioProcesCommand canviVersioProcesCommand = new CanviVersioProcesCommand();
			canviVersioProcesCommand.setDefinicioProcesId(definicioProces.getId());		

			model.addAttribute("expedient", expedient);
			model.addAttribute(canviVersioProcesCommand);
			model.addAttribute("definicioProces",definicioProces);
			model.addAttribute("subDefinicioProces", subDefinicioProces);
		} catch (Exception ex) {
			logger.error("Canviant versió de la definició de procés (" + "id=" + expedientId + ")", ex);
			MissatgesHelper.error(
					request,
					getMessage(request, "error.canviar.versio.proces"),
					ex);
		}
		return "v3/expedient/canviVersio";
	}
	
	@RequestMapping(value = "/{expedientId}/canviVersio", method = RequestMethod.POST)
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute CanviVersioProcesCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			ModelMap model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
			List<DefinicioProcesExpedientDto> subDefinicioProces = dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId());
			expedientService.procesDefinicioProcesCanviVersio(
					expedientId, 
					command.getDefinicioProcesId(), 
					command.getSubprocesId(), 
					subDefinicioProces);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.canviversio"));
		} catch (Exception ex) {
			logger.error("Canviant versió de la definició de procés (" + "id=" + expedientId + ")", ex);
			MissatgesHelper.error(
					request,
					getMessage(request, "error.canviar.versio.proces"),
					ex);
		}
		return modalUrlTancar();
	}
	
//	@RequestMapping(value = "/{expedientId}/updateDefinicioProces/{versio}", method = RequestMethod.GET)
//	@ResponseBody
//	public String changeDefProc(
//			HttpServletRequest request,
//			@PathVariable Long expedientId,
//			@PathVariable int versio,
//			ModelMap model) {
//		String nom = null;
//		try {
//			nom = expedientService.canviVersioDefinicioProces(
//					expedientId,
//					versio);
//			MissatgesHelper.success(request, getMessage(request, "info.canvi.versio.realitzat") );
//		} catch (Exception ex) {
//			logger.error("Canviant versió de la definició de procés (" +
//					"id=" + expedientId + ", " +
//					"versio=" + versio + ")", ex);
//			if (ex.getCause() instanceof ChangeLogException)
//				MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces.logs") + "<br/> " + ex.getCause().getMessage());
//			else
//				MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
//		}
//	        	
//		return JSONValue.toJSONString(nom);
//	}
	
	@RequestMapping(value = "/{expedientId}/buidalog", method = RequestMethod.GET)
	public String buidaLog(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			if (expedient.isPermisLogManage()) {
				expedientRegistreService.registreBuidarLog(
						expedient.getId());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.buidatlog"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.buidar.logs"));
			}
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(request, "error.buidarlog.expedient") + ": " + ex.getLocalizedMessage(),
					ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/desfinalitzar", method = RequestMethod.GET)
	public String desfinalitzar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.desfinalitzar(expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.desfinalitzat") );
		} catch (Exception e) {
			String errMsg = getMessage(request, "error.desfinalitzant.expedient", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}		
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	/** Mètode Ajax per refrescar l'estat de l'expedient quan es tramiten tasques des de la gestió
	 * de l'expedient.
	 * @return Retorna un JSON amb {estat: "Estat", dataFi : "dd/MM/yyyy HH:mm"}
	 */
	@RequestMapping(value = "/{expedientId}/consultaEstat", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> consultaEstat(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {

		// Objecte amb les propietats de retorn
		Map<String, Object> data = new HashMap<String, Object>();
		// Recupera l'informació de l'expedient
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		Date dataFi = expedient.getDataFi();
		String estat;
		if (dataFi == null) {
			estat = expedient.getEstat() != null? 
					expedient.getEstatNom() 
					: getMessage(request, "comu.estat.iniciat");
		} else {
			estat = getMessage(request, "comu.estat.finalitzat");
		}
		data.put("estat", estat);
		data.put("dataFi", dataFi != null? sdf.format(dataFi) : null);
		data.put("ambErrors", expedient.isAmbErrors());
		data.put("aturat", expedient.isAturat());
		data.put("anulat", expedient.isAnulat());
		data.put("anulatComentari", expedient.getComentariAnulat());
		data.put("alertesTotals", expedient.getAlertesTotals());
		data.put("alertesPendents", expedient.getAlertesPendents());
		data.put("reindexarData", (expedient.getReindexarData() != null) ?
				sdf.format(expedient.getReindexarData())
				: null);
		data.put("reindexarError", expedient.isReindexarError());
		
		return data;
	}

	@RequestMapping(value = "/{expedientId}/metadadesNti", method = RequestMethod.GET)
	public String info(
			@PathVariable Long expedientId,
			HttpServletRequest request,
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute(
					"expedient",
					expedient);
			if (expedient.isArxiuActiu() && expedient.getNtiOrgano() == null) {
				// La migració d'expedients no NTI provocava errors
				model.addAttribute("errorMetadadesNti", Boolean.TRUE);
			}
			model.addAttribute(
					"arxiuDetall",
					expedientService.getArxiuDetall(expedientId));
			if (expedient.getErrorArxiu()!=null && !"".equals(expedient.getErrorArxiu())) {
				MissatgesHelper.error(request, "Necessari sincronitzar amb l'Arxiu digital, per els seguents motius:<br/>"+expedient.getErrorArxiu());
			}
		} catch (Exception e) {
			String errMsg = getMessage(request, "expedient.info.error.consulta.arxiu", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}			
		return "v3/expedientMetadadesNtiInfo";
	}
	
	@RequestMapping(value = "/{expedientId}/sicronitzarArxiu", method = RequestMethod.GET)
	@ResponseBody
	public String sicronitzarArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		try {
			expedientService.sincronitzarArxiu(expedientId, false);
			MissatgesHelper.success(request, getMessage(request, "expedient.boto.sincro.arxiu.ok"));
			return "ok";
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(request, "expedient.boto.sincro.arxiu.ko") + "<br/> " + ex.getLocalizedMessage(),
					ex);
			return "ko";
		}
		
	}
	
	/** Mètode per incoporar el document a l'Arxiu en el cas que l'expedient estigui integrat però el document no. Acció des
	 * de la modal de metadades NTI del document.
	 * @return Retorna cap a la pàgina de metadades nti del document.
	 */
	@RequestMapping(value = "/{expedientId}/metadadesNti/arreglar", method = RequestMethod.POST)
	public String arreglarNti(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		try {
			expedientService.arreglarMetadadesNti(expedientId);
			MissatgesHelper.success(request, getMessage(request, "expedient.metadades.nti.dades.error.arreglar.success"));
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.metadades.nti.dades.error.arreglar.error", new Object[] {e.getMessage()}); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}
		return "redirect:" + request.getHeader("Referer");
	}
	

	@RequestMapping(value = "/{expedientId}/migrarArxiu", method = RequestMethod.GET)
	public String migrarArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			if (expedient.isPermisAdministration()) {
				expedientService.sincronitzarArxiu(expedient.getId(), true);
				MissatgesHelper.success(request, getMessage(request, "info.expedient.migrat.arxiu"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.migrar.expedient.arxiu"));
			}
		} catch (Exception ex) {
			logger.error("Error migrant l'expedient a l'arxiu: ", ex);
			MissatgesHelper.error(
					request,
					getMessage(request, "error.migrar.expedient.arxiu") + ": " + ex.getLocalizedMessage(),
					ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/estat/{estatId}/canviar", method = RequestMethod.GET)
	public String estatCanviar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long estatId) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			if (this.validarVariablesDocumentsCanviEstat(request, expedient, estatId)) {
				expedientService.estatCanviar(expedient.getId(), estatId);
				MissatgesHelper.success(request, getMessage(request, "expedient.info.estat.canviar.correcte"));
			}
		} catch (Exception ex) {
			StringBuilder message = new StringBuilder(ex.getClass().getSimpleName() + ": ");
			Throwable t = ex;
			boolean root;
			do {
				message.append(t.getMessage());
				t = t.getCause();
				root = t == null || t == t.getCause();
				if (!root) {
					message.append(": ");
				}
			} while (!root);
			String errMsg = getMessage(request, "expedient.info.estat.canviar.error", new Object[] {message.toString()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg, ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}


	private boolean validarVariablesDocumentsCanviEstat(HttpServletRequest request, ExpedientDto expedient, Long nextEstatId) {
		boolean correcte = true;
		// Comrova les variables obligatòries
		List<String> variablesObligatories = new ArrayList<String>();
		for (DadaListDto dada : expedientDadaService.findDadesExpedient(expedient.getId(), null, true, true, false, new PaginacioParamsDto())) {
			if (dada.isObligatori()) {
				if (this.dadaBuidaONula(dada)) {
					variablesObligatories.add(dada.getNom());
					correcte = false;
				}
			}
		}
		
		for (DadaListDto dada : expedientDadaService.findDadesExpedient(expedient.getId(), nextEstatId, true, true, false, new PaginacioParamsDto())) {
			if (dada.isObligatoriEntrada()) {
				if (this.dadaBuidaONula(dada)) {
					variablesObligatories.add(dada.getNom());
					correcte = false;
				}
			}
		}
		
		if (!variablesObligatories.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.estat.canviar.dades.obligatories", new Object[] {variablesObligatories.size(), variablesObligatories}));
		}
		
		// Comprova els documents obligatoris
		List<String> documentsObligatoris = new ArrayList<String>();
		for (DocumentListDto document : expedientDocumentService.findDocumentsExpedient(expedient.getId(), null, true, new PaginacioParamsDto())) {
			if (document.isObligatori() && document.getId() == null) {
				documentsObligatoris.add(document.getNom());
				correcte = false;
			}
		}
		
		for (DocumentListDto document : expedientDocumentService.findDocumentsExpedient(expedient.getId(), nextEstatId, true, new PaginacioParamsDto())) {
			if (document.isObligatoriEntrada() && document.getId() == null) {
				documentsObligatoris.add(document.getNom());
				correcte = false;
			}
		}
		
		if (!documentsObligatoris.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.estat.canviar.documents.obligatoris", new Object[] {documentsObligatoris.size(), documentsObligatoris}));
		}
		return correcte;
	}
	
	/** Comprova si la dada és buida o nula segons el tipus de dada */
	private boolean dadaBuidaONula(DadaListDto dada) {
		boolean ret = false;				
		if (dada == null  || dada.getId() == null) {
			ret = true;
		} else {
			// Comprova el valor
			if (dada.getValor() == null) {
				ret = true;
			} else if (dada.isMultiple()) {
				if (dada.getValor().getFiles() == 0 || dada.getValor().getValorMultiple() == null || dada.getValor().getValorMultiple().isEmpty()) {
					// Valor múltiple buit
					ret = true;
				}
			} else {
				// Valor simple buit
				ret = dada.getValor().getValorSimple() == null || dada.getValor().getValorSimple().isEmpty();
			}
		}
		return ret;
	}

	@RequestMapping(value="/{expedientTipusId}/documentDownload", method = RequestMethod.GET)
	public String documentDownload(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			Model model) {
		ArxiuDto arxiu = expedientTipusService.getManualAjuda(expedientTipusId);
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}

	//Genera un PDF amb una taula resum dels fitxers de l'expedient.
	@RequestMapping(value = "/{expedientId}/generarIndexExpedient", method = RequestMethod.GET)
	@ResponseBody
	public void exportarIndexExpedient(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId) throws Exception {
    	try { 
    		DocumentDto resultat = expedientDocumentService.generarIndexExpedient(expedientId);
    		this.writeFileToResponse(resultat.getArxiuNom(), resultat.getArxiuContingut(), response);
    	} catch(Exception e) {
    		MissatgesHelper.error(
    				request,
    				getMessage(
    						request, 
    						"expedient.exportacio.eni.error",
    						new Object[]{e.getMessage()}),
					e);
    		response.sendRedirect("/helium/v3/expedient/" + expedientId);
    	}        
	}
	
	//Genera un ZIP amb els documents definitius de l'expedient i la informació ENI (format XML) per cada document i de l'expedient mateix
	//a més de l'index en PDF de l'expedient.
	@RequestMapping(value = "/{expedientId}/exportarEniDocumentsAmbIndex", method = RequestMethod.GET)
	@ResponseBody
	public void exportarEniDocuments(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId) throws Exception {
    	try { 
    		DocumentDto resultat = expedientDocumentService.exportarEniDocumentsAmbIndex(expedientId);
    		this.writeFileToResponse(resultat.getArxiuNom(), resultat.getArxiuContingut(), response);
    	} catch(Exception e) {
    		MissatgesHelper.error(
    				request,
    				getMessage(
    						request, 
    						"expedient.exportacio.eni.error",
    						new Object[]{e.getMessage()}),
					e);
    		response.sendRedirect("/helium/v3/expedient/" + expedientId);
    	}        
	}
	
	//Genera el fitxer ENI (format XML) de l'expedient.
	@RequestMapping(value = "/{expedientId}/exportarEniExpedient", method = RequestMethod.GET)
	@ResponseBody
	public void exportarEniExpedient(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId) throws Exception {
    	try { 
    		DocumentDto resultat = expedientDocumentService.exportarEniExpedient(expedientId);
    		this.writeFileToResponse(resultat.getArxiuNom(), resultat.getArxiuContingut(), response);
    	} catch(Exception e) {
    		MissatgesHelper.error(
    				request,
    				getMessage(
    						request, 
    						"expedient.exportacio.eni.error",
    						new Object[]{e.getMessage()}),
					e);
    		response.sendRedirect("/helium/v3/expedient/" + expedientId);
    	}        
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
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
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientV3Controller.class);
}
