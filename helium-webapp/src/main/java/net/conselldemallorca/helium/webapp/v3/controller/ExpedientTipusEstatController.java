/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAccioEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisEstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.v3.core.api.exportacio.EstatExportacio;
import net.conselldemallorca.helium.webapp.v3.command.EstatReglaCommand;
import net.conselldemallorca.helium.webapp.v3.command.PermisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.AccioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.webapp.v3.command.EstatReglaCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstatCommand;
import net.conselldemallorca.helium.webapp.v3.command.ImportarDadesCommand;
import net.conselldemallorca.helium.webapp.v3.command.PermisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.validator.CodiValidator;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEstatController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("listEstats")
	public List<ParellaCodiValorDto> populateValorEstats() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(new ParellaCodiValorDto(Integer.toString(i), i));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estats")
	public String estats(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"estats");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusEstat";
	}

	@RequestMapping(value="/{expedientTipusId}/estats/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		paginacioParams.getOrdres().clear();
		paginacioParams.afegirOrdre("ordre", OrdreDireccioDto.ASCENDENT);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.estatFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusEstatCommand command = new ExpedientTipusEstatCommand();
		command.setOrdre(expedientTipusService.getEstatSeguentOrdre(expedientTipusId));
		
		command.setExpedientTipusId(expedientTipusId);
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusEstatCommand", command);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusEstatForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusEstatCommand.Creacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			if (entornActual != null) {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
						entornActual.getId(),
						expedientTipusId);
				model.addAttribute("expedientTipus", expedientTipus);
			}
        	return "v3/expedientTipusEstatForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.estatCreate(
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		EstatDto dto = expedientTipusService.estatFindAmbId(expedientTipusId, id);
		ExpedientTipusEstatCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusEstatCommand.class);
		command.setExpedientTipusId(expedientTipusId);

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		
		model.addAttribute("expedientTipusEstatCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusEstatForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEstatCommand.Modificacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("heretat", expedientTipusService.estatFindAmbId(expedientTipusId, id).isHeretat());
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			if (entornActual != null) {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
						entornActual.getId(),
						expedientTipusId);
				model.addAttribute("expedientTipus", expedientTipus);
			}
        	return "v3/expedientTipusEstatForm";
        } else {
        	expedientTipusService.estatUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			expedientTipusService.estatDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.estat.controller.eliminat"));
			return true;
		} catch (Exception e) {
			logger.error(e);
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.eliminat.no",
							new Object[] {e.getLocalizedMessage()}));
			return false;
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moure(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable int posicio,
			Model model) {
		boolean ret = false;

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto tipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(), 
				expedientTipusId); 
		
		boolean herencia = tipus.getExpedientTipusPareId() != null;
		if (herencia) {
			EstatDto estat = expedientTipusService.estatFindAmbId(expedientTipusId, estatId);
			boolean correcte = true;
			if (estat.isHeretat()) {
				MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"expedient.tipus.estat.controller.moure.heretat.error"));
				correcte = false;
			} else {
				// rectifica la posició restant tots els heretats que té per davant
				int nHeretats = 0;
				for(EstatDto e : expedientTipusService.estatFindAll(expedientTipusId, true))
					if (e.isHeretat())
						nHeretats ++;
				if (posicio < nHeretats) {
					MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.moure.heretat.error"));
					correcte = false;
				} else {
					posicio = posicio - nHeretats;
				}
			}
			if (correcte)
				ret = expedientTipusService.estatMoure(estatId, posicio);
			else
				ret = false;
		}
		else {
			ret = expedientTipusService.estatMoure(estatId, posicio);
		}
		return ret;
	}
	
	// Atenció: Els ordres no poden tenir forats entre mig, però poden tenir repetits.
	// Possibles ordres:
	//  - auto  --> No hi ha ambiguitat, i per tant es calcularà de forma automàtica. (Es mou entre dos estats que tenen el mateix ordre)
	//  - 1-1   --> Es mou a l'inici, i agafa el mateix valor del que hi havia abans al inici
	//  - 1-2   --> Es mou a l'inici, agafa el valor 1 i es desplaça la resta d'estats
	//  - 8-9   --> Es mou al final, i agafa el valor del que estava abans al final + 1
	//  - 9-9   --> Es mou al final, i agafa el mateix valor del que hi havia abans al final
	//  - 1-1-2 --> Es moun entre dos estats que tenen ordres consecutius, i agafa el mateix valor que el primer d'ells
	//  - 1-2-2 --> Es moun entre dos estats que tenen ordres consecutius, i agafa el mateix valor que el segon d'ells
	//  - 1-2-3 --> Es moun entre dos estats que tenen ordres consecutius, agafa el mateix valor que el segon d'ells, i desplaça la resta d'estats
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/moure/{posicio}/ordre/{ordre}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moureReglaAmbOrdre(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable int posicio,
			@PathVariable String ordre,
			Model model) {
		boolean ret = false;
	
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto tipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(),
				expedientTipusId);
	
		return expedientTipusService.estatMoureOrdre(estatId, posicio, ordre);
	}
	
	
	
	@RequestMapping(value = "/{expedientTipusId}/estat/exportar", method = RequestMethod.GET)
	@ResponseBody
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientTipusId) throws Exception {


        	try {
        		
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

				List<EstatExportacio> estatExportacioList = expedientTipusService.estatExportacio(expedientTipusId, true);
				byte[] exportacioJson = mapper.writeValueAsBytes(estatExportacioList);
        		
        		MissatgesHelper.success(
    					request, 
    					getMessage(
    							request, 
    							"expedient.tipus.estat.exportar.controller.success"));        			

        		response.setHeader("Pragma", "");
        		response.setHeader("Expires", "");
        		response.setHeader("Cache-Control", "");
        		response.setHeader("Content-Disposition", "attachment; filename=\"estats_exp.json\"");
        		response.setContentType("text/plain");
        		response.getOutputStream().write(exportacioJson);
        
        	} catch(Exception e) {
        		logger.error(e);
        		MissatgesHelper.error(
        				request,
        				getMessage(
        						request, 
        						"expedient.tipus.estat.exportar.controller.error",
        						new Object[]{e.getLocalizedMessage()}));
        		throw(e);
        	}        
	}	

	
	/** Mètode per obrir un formulari d'importació de dades d'estats. */
	@RequestMapping(value = "/{expedientTipusId}/estat/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute(new ImportarDadesCommand());
		return "v3/expedientTipusEstatImportarForm";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/estat/importar", method = RequestMethod.POST)
	public String importarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ImportarDadesCommand.Importar.class) ImportarDadesCommand command,
			BindingResult bindingResult,
			Model model) {
		if (command.getMultipartFile() == null || command.getMultipartFile().getSize() == 0) {
			bindingResult.rejectValue("multipartFile", "expedient.tipus.estat.importar.controller.validacio.multipartFile.buit");
		}
        if (bindingResult.hasErrors()) {
        	model.addAttribute("importarDadesCommand", command);
        	return "v3/expedientTipusEstatImportarForm";
        } else {

			int insercions = 0;
			int actualitzacions = 0;
        	try {
    			if (command.isEliminarValorsAntics()) {
    				for (EstatDto estat : expedientTipusService.estatFindAll(expedientTipusId, false))
   						expedientTipusService.estatDelete(estat.getId());
    			}

				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				List<EstatExportacio> estatExportacioList = mapper.readValue(command.getMultipartFile().getInputStream(), new TypeReference<List<EstatExportacio>>(){});
    			for (EstatExportacio estatExportacio: estatExportacioList) {
    					// Comprova que el codi sigui vàlid
					if (! CodiValidator.isValid(estatExportacio.getCodi())) {
    		        		MissatgesHelper.error(
    		        				request,
								getMessage(request, "expedient.tipus.estat.importar.controller.error.codi", new Object[]{estatExportacio.getCodi()}));
						continue;
					}

					// Crear o actualitzar estat
					EstatDto estat = expedientTipusService.estatFindAmbCodi(expedientTipusId, estatExportacio.getCodi());
        					if (estat == null) {
						EstatDto estatDto = EstatDto.builder()
								.codi(estatExportacio.getCodi())
								.nom(estatExportacio.getNom())
								.ordre(estatExportacio.getOrdre())
								.build();
						estat = expedientTipusService.estatCreate(expedientTipusId, estatDto);
        						insercions++;
        					} else {
						estat.setNom(estatExportacio.getNom());
						estat.setOrdre(estatExportacio.getOrdre());
        						expedientTipusService.estatUpdate(estat);
        						actualitzacions++;
        					}

					// Crear o actualitzar regles
					if (estatExportacio.getRegles() != null) {
						for(EstatReglaDto reglaExportacio: estatExportacio.getRegles()) {
							EstatReglaDto regla = expedientTipusService.estatReglaFindByNom(estat.getId(), reglaExportacio.getNom());
							if (regla == null) {
								expedientTipusService.estatReglaCreate(estat.getId(), reglaExportacio);
							} else {
								reglaExportacio.setId(regla.getId());
								expedientTipusService.estatReglaUpdate(estat.getId(), reglaExportacio);
							}
						}
					}

					// Actualitzar permisos
					if (estatExportacio.getPermisos() != null) {
						for (PermisEstatDto permisExportacio: estatExportacio.getPermisos()) {
							expedientTipusService.estatPermisUpdate(
									estat.getId(),
									conversioTipusHelper.convertir(permisExportacio, PermisDto.class));
    					}
    				}
    			}
        	} catch(Exception e) {
        		logger.error(e);
        		MissatgesHelper.error(
        				request,
        				getMessage(request, "expedient.tipus.estat.importar.controller.error", new Object[]{e.getLocalizedMessage()}));
        	}
    		MissatgesHelper.success(
					request, 
					getMessage(request, "expedient.tipus.estat.importar.controller.success", new Object[] {insercions, actualitzacions}));
			return modalUrlTancar(false);	
        }
	}


	// PERMISOS
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permisos", method = RequestMethod.GET)
	public String permisosGet(HttpServletRequest request,
							  @PathVariable Long expedientTipusId,
							  @PathVariable Long estatId,
							  Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(),
				expedientTipusId);
		EstatDto estat = expedientTipusService.estatFindAmbId(expedientTipusId, estatId);

		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("estat", estat);
		return "v3/expedientTipusEstatPermisos";
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse permisDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.estatPermisFindAll(estatId));
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/new", method = RequestMethod.GET)
	public String permisNewGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			Model model) {
		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		model.addAttribute(new PermisCommand());
		return "v3/expedientTipusEstatPermisForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/new", method = RequestMethod.POST)
	public String permisNewPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		return permisUpdatePost(
				request,
				expedientTipusId,
				estatId,
				null,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/{permisId}", method = RequestMethod.GET)
	public String permisUpdateGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long permisId,
			Model model) {
		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		PermisDto permis = expedientTipusService.estatPermisFindById(estatId, permisId);
		model.addAttribute(conversioTipusHelper.convertir(permis, PermisCommand.class));
		return "v3/expedientTipusEstatPermisForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/{permisId}", method = RequestMethod.POST)
	public String permisUpdatePost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long permisId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
			return "v3/expedientTipusEstatPermisForm";
		} else {
			expedientTipusService.estatPermisUpdate(
					estatId,
					conversioTipusHelper.convertir(command, PermisDto.class));

			MissatgesHelper.success(
					request,
					getMessage(request, "expedient.tipus.estat.controller.permis.actualitzat"));
			return modalUrlTancar();
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/permis/{permisId}/delete")
	public String permisDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long permisId,
			Model model) {

		expedientTipusService.estatPermisDelete(
				estatId,
				permisId);

		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		model.addAttribute(new PermisCommand());
		return "redirect:/v3/expedientTipus/" + expedientTipusId + "/estat/" + estatId + "/permisos";
	}



	// REGLES
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regles", method = RequestMethod.GET)
	public String reglesGet(HttpServletRequest request,
							@PathVariable Long expedientTipusId,
							@PathVariable Long estatId,
							Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(),
				expedientTipusId);
		EstatDto estat = expedientTipusService.estatFindAmbId(expedientTipusId, estatId);

		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("estat", estat);

		return "v3/expedientTipusEstatRegles";
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse reglesDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.estatReglaFindAll(estatId),
				"id");
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/new", method = RequestMethod.GET)
	public String reglaNewGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			Model model) {
		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		model.addAttribute(EstatReglaCommand.builder().estatId(estatId).expedientTipusId(expedientTipusId).build());
		modelRegles(model, expedientTipusId, null);
		return "v3/expedientTipusEstatReglaForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/new", method = RequestMethod.POST)
	public String reglaNewPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@Valid EstatReglaCommand command,
			BindingResult bindingResult,
			Model model) {
		return reglaUpdatePost(
				request,
				expedientTipusId,
				estatId,
				null,
				command,
				bindingResult,
				model);
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/{reglaId}", method = RequestMethod.GET)
	public String reglaUpdateGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long reglaId,
			Model model) {
		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		EstatReglaDto regla = expedientTipusService.estatReglaFindById(estatId, reglaId);
		model.addAttribute(conversioTipusHelper.convertir(regla, EstatReglaCommand.class));
		modelRegles(model, expedientTipusId, regla.getQue());
		return "v3/expedientTipusEstatReglaForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/{reglaId}", method = RequestMethod.POST)
	public String reglaUpdatePost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long reglaId,
			@Valid EstatReglaCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
			modelRegles(model, expedientTipusId, command.getQue());
			return "v3/expedientTipusEstatReglaForm";
		} else {
			if (reglaId == null) {
				expedientTipusService.estatReglaCreate(estatId, conversioTipusHelper.convertir(command, EstatReglaDto.class));
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.estat.controller.regla.creat"));
			} else {
				expedientTipusService.estatReglaUpdate(estatId, conversioTipusHelper.convertir(command, EstatReglaDto.class));
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.estat.controller.regla.actualitzat"));
			}
			return modalUrlTancar();
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/{reglaId}/delete")
	public String reglaDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long reglaId,
			Model model) {

		expedientTipusService.estatReglaDelete(
				estatId,
				reglaId);

		model.addAttribute("estat", expedientTipusService.estatFindAmbId(expedientTipusId, estatId));
		model.addAttribute(new EstatReglaCommand());
		return "redirect:/v3/expedientTipus/" + expedientTipusId + "/estat/" + estatId + "/regles";
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/regla/{reglaId}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moureRegla(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable Long reglaId,
			@PathVariable int posicio,
			Model model) {
		boolean ret = false;

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto tipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(),
				expedientTipusId);

		return expedientTipusService.estatReglaMoure(reglaId, posicio);
	}

	@RequestMapping(value = "/{expedientTipusId}/var/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> reglaGetVars(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {

		List<CampDto> campsDto = campService.findAllOrdenatsPerCodi(expedientTipusId, null);
		// Crea les parelles de codi i valor
		List<ParellaCodiValorDto> dades = new ArrayList<ParellaCodiValorDto>();
		for (CampDto camp : campsDto) {
			dades.add(ParellaCodiValorDto.builder().codi(camp.getCodi()).valor(camp.getEtiqueta()).build());
		}
		return dades;
	}

	@RequestMapping(value = "/{expedientTipusId}/doc/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> reglaGetDocs(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {

		List<DocumentDto> documentDtos = documentService.findAll(expedientTipusId, null);
		// Crea les parelles de codi i valor
		List<ParellaCodiValorDto> documents = new ArrayList<ParellaCodiValorDto>();
		for (DocumentDto document : documentDtos) {
			documents.add(ParellaCodiValorDto.builder().codi(document.getCodi()).valor(document.getNom()).build());
		}
		return documents;
	}

	@RequestMapping(value = "/{expedientTipusId}/term/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> reglaGetTerms(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {

		List<TerminiDto> terminiDtos = terminiService.findAll(expedientTipusId, null);
		// Crea les parelles de codi i valor
		List<ParellaCodiValorDto> terminis = new ArrayList<ParellaCodiValorDto>();
		for (TerminiDto termini : terminiDtos) {
			terminis.add(ParellaCodiValorDto.builder().codi(termini.getCodi()).valor(termini.getNom()).build());
		}
		return terminis;
	}

	@RequestMapping(value = "/{expedientTipusId}/agrup/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> reglaGetAgrups(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {

		List<CampAgrupacioDto> agrupacioDtos = campService.agrupacioFindAll(expedientTipusId, null, false);
		// Crea les parelles de codi i valor
		List<ParellaCodiValorDto> agrupacions = new ArrayList<ParellaCodiValorDto>();
		for (CampAgrupacioDto agrupacio : agrupacioDtos) {
			agrupacions.add(ParellaCodiValorDto.builder().codi(agrupacio.getCodi()).valor(agrupacio.getNom()).build());
		}
		return agrupacions;
	}

	@RequestMapping(value = "/persona/suggest/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggest(
			@PathVariable String text,
			Model model) {
		String textDecoded = text;
		List<PersonaDto> lista = aplicacioService.findPersonaLikeCodiOrNomSencer(textDecoded);
		String json = "[";
		for (PersonaDto persona: lista) {
			json += "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
	}

	@RequestMapping(value = "/persona/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggestInici(
			@PathVariable String text,
			Model model) {
		PersonaDto persona = aplicacioService.findPersonaAmbCodi(text);
		if (persona != null) {
			return "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"}";
		}
		return null;
	}

	private void modelRegles(Model model, Long expedientTipusId, QueEnum que) {
		model.addAttribute("quiOptions", EnumHelper.getOptionsForEnum(QuiEnum.class, "enum.regla.qui."));
		model.addAttribute("queOptions", EnumHelper.getOptionsForEnum(QueEnum.class, "enum.regla.que."));
		model.addAttribute("accioOptions", EnumHelper.getOptionsForEnum(AccioEnum.class, "enum.regla.accio."));

		List<String> valorsQue = new ArrayList<String>();

		List<ParellaCodiValorDto> valors = null;
		if (que != null) {
			switch (que) {
				case DADA:
					valors = reglaGetVars(null, expedientTipusId, null);
					break;
				case DOCUMENT:
					valors = reglaGetDocs(null, expedientTipusId, null);
					break;
				case TERMINI:
					valors = reglaGetTerms(null, expedientTipusId, null);
					break;
				case AGRUPACIO:
					valors = reglaGetAgrups(null, expedientTipusId, null);
					break;
			}
			if (valors != null && !valors.isEmpty()) {
				for (ParellaCodiValorDto codiValor: valors) {
					valorsQue.add(codiValor.getCodi() + " | " + codiValor.getValor());
				}
			}
		}
		model.addAttribute("valorsQue", valorsQue);
	}
	
	// ACCIONS
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/accions", method = RequestMethod.GET)
	public String accions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(),
				expedientTipusId);
		EstatDto estat = expedientTipusService.estatFindAmbId(expedientTipusId, estatId);

		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("estat", estat);

		return "v3/expedientTipusEstatAccions";
	}	
	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusEstatController.class);
}
