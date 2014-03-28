/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesController extends ExpedientTramitacioController {

	@RequestMapping(value = "/{expedientId}/tasques", method = RequestMethod.GET)
	public String tasques(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			return mostrarInformacioExpedientPerPipella(request, expedientId, model, "tasques", expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		List<ExpedientTascaDto> tasques = expedientService.findTasquesPerExpedient(expedientId);
		model.addAttribute("tasques", tasques);
		model.addAttribute("expedientLogIds", expedientService.findLogIdTasquesById(tasques));
		return "v3/expedientTasques";
	}

	@RequestMapping(value = "/{expedientId}/tasquesPendents", method = RequestMethod.GET)
	public String tasquesPendents(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			return mostrarInformacioExpedientPerPipella(request, expedientId, model, "tasques", expedientService);
		}
		model.addAttribute("tasques", expedientService.findTasquesPendentsPerExpedient(expedientId));
		return "v3/expedientTasquesPendents";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/tramitar", 
			method = RequestMethod.GET)
	public String tramitar(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable String tascaId, Model model) {
		return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String form(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable String tascaId, 
			Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute("tasca", expedientService.getTascaPerExpedient(expedientId, tascaId));
		// Omple les dades del formulari i les de només lectura
		List<TascaDadaDto> dades = tascaService.findDadesPerTasca(tascaId);
		model.addAttribute("dades", dades);
		List<TascaDadaDto> dadesNomesLectura = new ArrayList<TascaDadaDto>();
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				if (dada.getText() != null && !dada.getText().isEmpty())
					dadesNomesLectura.add(dada);
				itDades.remove();
			}
		}
		model.addAttribute("dadesNomesLectura", dadesNomesLectura);
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocumentsPerTasca(tascaId);
		model.addAttribute("documents", documents);
		List<TascaDocumentDto> documentsNomesLectura = new ArrayList<TascaDocumentDto>();
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					documentsNomesLectura.add(document);
				itDocuments.remove();
			}
		}
		model.addAttribute("documentsNomesLectura", documentsNomesLectura);
		return "v3/expedientTascaTramitacio";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", 
			method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findOpcionsSeleccioPerCampTasca(tascaId, campId);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/completar", method = RequestMethod.POST)
	public String completar( 
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			boolean ok = accioCompletarTasca(
					request,
					entorn.getId(),
					id,
					submit);
			if (ok) {
				return "redirect:/v3/expedient/"+expedientId;
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.POST)
	public String formPost(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			@RequestParam(value = "id", required = false) String id, 
			@RequestParam(value = "submit", required = false) String submit, 
			@RequestParam(value = "submitar", required = false) String submitar, 
			@RequestParam(value = "helMultipleIndex", required = false) Integer index, 
			@RequestParam(value = "helMultipleField", required = false) String field, 
			@RequestParam(value = "iframe", required = false) String iframe, 
			@RequestParam(value = "registreEsborrarId", required = false) Long registreEsborrarId,
			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
			@RequestParam(value = "helAccioCamp", required = false) String accioCamp, 
			@RequestParam(value = "helCampFocus", required = false) String campFocus,
			@RequestParam(value = "helFinalitzarAmbOutcome", required = false) String finalitzarAmbOutcome, 
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
			this.validatorGuardar = new TascaFormValidatorHelper(tascaService, false);
			this.validatorValidar = new TascaFormValidatorHelper(tascaService);
			
			boolean opValidar = "validate".equals(submit) || "validate".equals(submitar);
			boolean opSubmit = "submit".equals(submit)  || "submit".equals(submitar);
			boolean opRestore = "restore".equals(submit) || "restore".equals(submitar);
			
			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
			List<TascaDadaDto> tascaDadas = tascaService.findDadesPerTasca(id);

			if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, partsCampFocus[0]);
				} else {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, campFocus);
				}
			}
			if (opSubmit || opValidar || "@@@".equals(finalitzarAmbOutcome)) {
				validatorGuardar.validate(command, result);
				if (result.hasErrors()) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				}
				boolean ok = accioGuardarForm(request, entorn.getId(), id, tascaDadas, command);
				if (!ok) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				} else if (!opValidar){
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar"));
				}
				if (accioCamp != null && accioCamp.length() > 0) {
					ok = accioExecutarAccio(request, entorn.getId(), id, accioCamp);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				if (opValidar || "@@@".equals(finalitzarAmbOutcome)) {
					validatorValidar.validate(command, result);
					try {
						afegirVariablesDelProces(command, tasca);
						Validator validator = TascaFormHelper.getBeanValidatorForCommand(tascaDadas);
						Map<String, Object> valorsCommand = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
						validator.validate(TascaFormHelper.getCommandForCamps(tascaDadas,valorsCommand,null,null,false), result);
					} catch (Exception ex) {
						logger.error("S'han produit errors de validació", ex);
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					if (result.hasErrors()) {
						MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					ok = accioValidarForm(request, entorn.getId(), id, tascaDadas, command);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				status.setComplete();
				if (finalitzarAmbOutcome != null && !finalitzarAmbOutcome.equals("@#@")) {
					boolean okCompletar = accioCompletarTasca(
							request,
							entorn.getId(),
							id,
							finalitzarAmbOutcome);
					if (okCompletar) {
						return "redirect:/v3/expedient/"+expedientId;
					}
				}
			} else if (opRestore) {
				boolean ok = accioRestaurarForm(request, entorn.getId(), id, tascaDadas, command);
				if (ok) {
					status.setComplete();
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				}
			} else if ("multipleAdd".equals(submit)) {
				try {
					if (field != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.addMultiple(field, command, tascaDadas));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.afegir.camp.multiple"));
					logger.error("No s'ha pogut afegir el camp múltiple", ex);
				}
			} else if ("multipleRemove".equals(submit)) {
				try {
					if (field != null && index != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.deleteMultiple(field, command, tascaDadas, index));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.esborrar.camp.multiple"));
					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
				}
			} else {
				status.setComplete();
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
					accioEsborrarRegistre(request, entorn.getId(), id, registreEsborrarId, registreEsborrarIndex);
				}
				TascaFormHelper.guardarCommandTemporal(request, command);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/cancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.cancelarTasca(entorn.getId(), String.valueOf(tascaId));
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.cancelar.tasca", new Object[] {String.valueOf(tascaId)} ));
		        	logger.error("No s'ha pogut cancel·lar la tasca " + String.valueOf(tascaId), ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/suspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.suspendreTasca(entorn.getId(), String.valueOf(tascaId));
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.suspendre.tasca", new Object[] {tascaId} ));
		        	logger.error("No s'ha pogut suspendre la tasca " + tascaId, ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.reprendreTasca(entorn.getId(), String.valueOf(tascaId));
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.reprendre.tasca", new Object[] {tascaId} ));
		        	logger.error("No s'ha pogut reprendre la tasca " + tascaId, ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/delegacioCancelar", method = RequestMethod.POST)
	public String cancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();	
		if (entorn != null) {
			try {
				tascaService.delegacioCancelar(entorn.getId(), String.valueOf(tascaId));
				MissatgesHelper.info(request, getMessage(request, "info.delegacio.cancelat") );
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
	        	logger.error("No s'ha pogut cancel·lar la delegació de la tasca " + tascaId, ex);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/tascaAlliberar", method = RequestMethod.GET)
	public String tascaAlliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarOReassignarExpedient(expedient)) {
				try {
					tascaService.alliberar(
							entorn.getId(),
							tascaId,
							false);
					MissatgesHelper.info(request, getMessage(request, "info.tasca.alliberada"));
				} catch (Exception e) {
					MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
					e.printStackTrace();
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/tascaAgafar", method = RequestMethod.GET)
	public String tascaAafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarOReassignarExpedient(expedient)) {
				try {
					tascaService.agafar(entorn.getId(), tascaId);
					MissatgesHelper.info(request, getMessage(request, "info.tasca.disponible.personals"));
				} catch (Exception e) {
					MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
					e.printStackTrace();
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientTasquesController.class);
}
