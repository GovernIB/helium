/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.CanalNotifEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDocumentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.service.DadesExternesService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInteressatV3Controller extends BaseExpedientController {

	@Autowired private ExpedientInteressatService expedientInteressatService;
	@Autowired private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired private DadesExternesService dadesExternesService;

	@RequestMapping(value="/{expedientId}/interessat/{codiInteressat}", method = RequestMethod.GET)
	@ResponseBody
	public InteressatDto info(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String codiInteressat,
			Model model) {
		return expedientInteressatService.findByCodi(codiInteressat);
	}

	@RequestMapping(value="/{expedientId}/interessat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("expedientId", expedientId);	
		return "v3/interessatLlistat";
	}

	@RequestMapping(value="/{expedientId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		Map<String, String[]> mapeigOrdenacions = new HashMap<String, String[]>();
		InteressatDto representantInteressat = null;
		List<InteressatDto> representantsExpedient = expedientInteressatService.findRepresentantsExpedient(expedientId);
		model.addAttribute(
				"representantsExpedient",
				representantsExpedient
				);
		model.addAttribute(
				"existeixenRepresentantsExpedient",
				representantsExpedient!=null && !representantsExpedient.isEmpty()
				);		
		for(InteressatDto interessat: expedientInteressatService.findByExpedient(expedientId)) {
			if(!interessat.getEs_representant() && interessat.getRepresentant()!=null) {
				representantInteressat = expedientInteressatService.findOne(interessat.getRepresentant().getId());
				interessat.setRepresentant(representantInteressat);
				interessat.setRepresentant_id(representantInteressat.getId());
				interessat.setExisteixenRepresentantsExpedient(true);
				mapeigOrdenacions.put("representantFullNom", new String[] {"representantInteressat.nom", "representantInteressat.llinatge1", "representantInteressat.llinatge2"});
				interessat.setTeRepresentant(true);
			}
		}
		mapeigOrdenacions.put("fullNom", new String[] {"nom", "llinatge1", "llinatge2", "raoSocial"});
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request, null, mapeigOrdenacions);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientInteressatService.findPerDatatable(
						expedientId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}
	
	/** Mètode per obtenir el detall d'un interessat. Es consulta quan s'expandeix la seva fila a la taula d'interessats. */
	@RequestMapping(value="/{expedientId}/interessat/{interessatId}/detall", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		model.addAttribute("expedientId", expedientId);	
		InteressatDto interessat = expedientInteressatService.findOne(
				interessatId);
		
		this.populateDadesInteressat(interessat);
		if(interessat.getRepresentant()!=null) {
			this.populateDadesInteressat(interessat.getRepresentant());
		}
		model.addAttribute(
				"interessatCanalsNotif", 
				this.populateCanalsNotif(request)
				);
		model.addAttribute("interessat", interessat);

		return "v3/interessatDetall";
	}


	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		InteressatCommand interessatCommand= new InteressatCommand();
		populateModel(request, model, null);
		interessatCommand.setPais("724");
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(interessatCommand);
		model.addAttribute("es_representant",false);
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
		boolean error = false;
        if (bindingResult.hasErrors()) {
        	error = true;
        } else {
        	try {
        		if (command.getTipus() != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(command.getTipus())) {
        			this.populateUOsCommand(command);
            	}
        		InteressatDto resultat = expedientInteressatService.create(
	    				ConversioTipusHelper.convertir(
	    						command,
	    						InteressatDto.class));
        		if (resultat.isPropagatArxiu()) {
        			MissatgesHelper.success(request, getMessage(request, "interessat.controller.creat") );
        		} else {
        			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.creat.err") );
        		}
        	} catch (Exception ex) {
        		String errMsg = getMessage(request, "interessat.controller.crear.error", new Object[] {ex.toString()});
        		logger.error(errMsg, ex);
        		MissatgesHelper.error(request, errMsg);
        		error = true;
        	}
        }
    	if (error) {
    		populateModel(request, model, null);
    		return "v3/interessatForm";
    	} else {
        	return modalUrlTancar(false);
    	}
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@RequestParam(value = "es_representant", required=false) boolean es_representant,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		InteressatDto dto = expedientInteressatService.findOne(
				interessatId);
		InteressatCommand interessatCommand = new InteressatCommand();
		if(dto.getPais()==null) {
			dto.setPais("724");
		}
		populateModel(request, model, dto.getProvincia());
		model.addAttribute("tipus",dto.getTipus());
		model.addAttribute("es_representant",dto.getEs_representant());
		//posem el valor de l'enum tal com apareix al llistat
		if(dto.getTipusDocIdent()==null) { //En el cas d'interessats antics no té tipusDocIdent li posem NIF de moment
			dto.setTipusDocIdent(InteressatDocumentTipusEnumDto.NIF.name());
		} else {
			dto.setTipusDocIdent(this.populateInteressatDocumentTipus(dto));
		}
		this.populateUOsCommand(interessatCommand);
		interessatCommand = ConversioTipusHelper.convertir(
				dto,
				InteressatCommand.class);
		if (interessatCommand.getTipus() != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(interessatCommand.getTipus())) {
    		this.populateUOsCommand(interessatCommand);
    		interessatCommand.setCifOrganGestor(interessatCommand.getDocumentIdent());
    	}
		model.addAttribute(interessatCommand);
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@RequestParam(value = "es_representant" , required=false) boolean es_representant,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@Validated(Modificacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
		boolean error = false;
        if (bindingResult.hasErrors()) {
        	error = true;
        } else {
        	try {
            	InteressatDto resultat = expedientInteressatService.update(
            			ConversioTipusHelper.convertir(
        						command,
        						InteressatDto.class));
            	
    	        if(es_representant) {
    	        	if (resultat.isPropagatArxiu()) {
    	        		MissatgesHelper.success(request, getMessage(request, "interessat.controller.representant.modificat") );
    	        	} else {
    	        		MissatgesHelper.warning(request, getMessage(request, "interessat.controller.representant.modificat.err") );
    	        	}
    	        } else {
    	        	if (resultat.isPropagatArxiu()) {
    	        		MissatgesHelper.success(request, getMessage(request, "interessat.controller.modificat") );
    	        	} else {
    	        		MissatgesHelper.warning(request, getMessage(request, "interessat.controller.modificat.err") );
    	        	}
    	        }
        	} catch(Exception ex) {
        		String errMsg = getMessage(request, "interessat.controller.modificar.error", new Object[] {ex.toString()});
        		MissatgesHelper.error(request, errMsg);
        		error = true; 		
        	}
        }
        if (error) {
    		populateModel(request, model, command.getProvincia());
        	command.setEs_representant(es_representant);
        	if (command.getTipus() != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(command.getTipus())) {
    			this.populateUOsCommand(command);
        	}
        	return "v3/interessatForm";
        } else {
			return modalUrlTancar(false);        	
        }
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/new", method = RequestMethod.GET)
	public String newRepresentantGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		InteressatCommand interessatCommand= new InteressatCommand();
		populateModel(request, model, null);
		interessatCommand.setPais("724");
		interessatCommand.setEs_representant(true);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(interessatCommand);
		model.addAttribute("es_representant",true);
		return "v3/interessatForm";
	}
		
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/new", method = RequestMethod.POST)
	public String newRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@Validated(Modificacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
		boolean error = false;
        if (bindingResult.hasErrors()) {
        	error = true;
        } else {
        	try {
            	command.setEs_representant(true);
            	if (command.getTipus() != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(command.getTipus())) {
        			this.populateUOsCommand(command);
            	}
            	expedientInteressatService.createRepresentant(
            			interessatId,
        				ConversioTipusHelper.convertir(
        						command,
        						InteressatDto.class));
    			MissatgesHelper.success(request, getMessage(request, "interessat.controller.representant.creat") );
        	} catch(Exception ex) {
        		String errMsg = getMessage(request, "interessat.controller.representant.creat.error", new Object[] {ex.toString()});
        		MissatgesHelper.error(request, errMsg);
        		error = true;
        	}
        }
        if (error) {
    		populateModel(request, model, command.getProvincia());
        	return "v3/interessatForm";
        } else {
  			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/search", method = RequestMethod.GET)
	public String searchRepresentantGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		InteressatCommand interessatCommand= new InteressatCommand();
		List<InteressatDto> representantsExpedient = expedientInteressatService.findRepresentantsExpedient(expedientId);
		model.addAttribute(
				"representantsExpedient",
				representantsExpedient
				);
		model.addAttribute(
				"existeixenRepresentantsExpedient",
				representantsExpedient!=null && !representantsExpedient.isEmpty()
				);	
		interessatCommand.setEs_representant(true);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("interessatId", interessatId);
		model.addAttribute(interessatCommand);
		model.addAttribute("es_representant",true);
		return "v3/interessatCercarRepresentant";
	}
		
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/search", method = RequestMethod.POST)
	public String searchRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatCercarRepresentant";
        } else {
        	InteressatDto interessat =expedientInteressatService.findOne(interessatId);
        	String representantId = command.getRepresentantSeleccionatId();
        	if(representantId !=null) {
	        	//Assignem el representant a l'interessat
	        	InteressatDto representat =expedientInteressatService.findOne(Long.valueOf(representantId));
	        	interessat.setRepresentant(representat);
	        	interessat.setRepresentant_id(representat.getId());
	        	expedientInteressatService.update(interessat);
        	}
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.representant.assignat") );
  			return modalUrlTancar(false);
        }
	}
	
	@ModelAttribute("interessatTipusDocuments")
	public List<ParellaCodiValorDto> populateTipusDocuments(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.NIF"), InteressatDocumentTipusEnumDto.NIF));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.CIF"), InteressatDocumentTipusEnumDto.CIF));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.PASSAPORT"), InteressatDocumentTipusEnumDto.PASSAPORT));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.DOCUMENT_IDENTIFICATIU_ESTRANGERS"), InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.ALTRES_DE_PERSONA_FISICA"), InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.document.enum.CODI_ORIGEN"), InteressatDocumentTipusEnumDto.CODI_ORIGEN));
		return resposta;
	}
	
	public String populateInteressatDocumentTipus(InteressatDto interessatDto) {
		//Si ve buit, per defecte posarem NIF
		String docIdentTipus = interessatDto.getTipusDocIdent() != null ? interessatDto.getTipusDocIdent() : InteressatDocumentTipusEnumDto.NIF.toString();
		if("NIF".equals(docIdentTipus) || "N".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.NIF.toString();
		} else if("CIF".equals(docIdentTipus) || "C".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.CIF.toString();
		} else if("PASSAPORT".equals(docIdentTipus) || "P".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.PASSAPORT.toString();
		} else if("NIE".equals(docIdentTipus) || "DOCUMENT_IDENTIFICATIU_ESTRANGERS".equals(docIdentTipus) || "E".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS.toString();
		} else if("CODI_ORIGEN".equals(docIdentTipus) || "O".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.CODI_ORIGEN.toString();
		} else if("ALTRES".equals(docIdentTipus) || "X".equals(docIdentTipus)) {
			docIdentTipus = InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA.toString();
		}
		return docIdentTipus;
	}
	
	@ModelAttribute("interessatCanalsNotif")
	public List<ParellaCodiValorDto> populateCanalsNotif(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(CanalNotifEnumDto.COMPARECENCIA_ELECTRONICA.getName(),CanalNotifEnumDto.COMPARECENCIA_ELECTRONICA.getValue()));
		resposta.add(new ParellaCodiValorDto(CanalNotifEnumDto.DIRECCION_ELECTRONICA_HABILITADA.getName(),CanalNotifEnumDto.DIRECCION_ELECTRONICA_HABILITADA.getValue()));
		resposta.add(new ParellaCodiValorDto(CanalNotifEnumDto.DIRECCION_POSTAL.getName(),CanalNotifEnumDto.DIRECCION_POSTAL.getValue()));
		return resposta;
	}
	
	
	@ModelAttribute("NotificaDomiciliConcretTipus")
	public List<ParellaCodiValorDto> populateDomiciliTipus(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		//resposta.add(new ParellaCodiValorDto(getMessage(request, "domiciliconcret.tipus.enum.NACIONAL"), NotificaDomiciliConcretTipusEnumDto.NACIONAL));
		//resposta.add(new ParellaCodiValorDto(getMessage(request, "domiciliconcret.tipus.enum.ESTRANGER"), NotificaDomiciliConcretTipusEnumDto.ESTRANGER));
		//resposta.add(new ParellaCodiValorDto(getMessage(request, "domiciliconcret.tipus.enum.APARTAT_CORREUS"), NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "domiciliconcret.tipus.enum.SENSE_NORMALITZAR"), NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR));
		return resposta;
	}
	
	@ModelAttribute("serveiTipusEstats")
	public List<ParellaCodiValorDto> populateServeiEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.servei.tipus.enum.NORMAL"), ServeiTipusEnumDto.NORMAL));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.servei.tipus.enum.URGENT"), ServeiTipusEnumDto.URGENT));
		return resposta;
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "es_representant", required=false) boolean es_representant,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		try {
			InteressatDto resultat = expedientInteressatService.delete(interessatId);
			if (resultat.isPropagatArxiu()) {
				MissatgesHelper.success(request, getMessage(request, "interessat.controller.esborrat"));
			} else {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.esborrat.err"));
			}						
		} catch (Exception ex) {
			String errMsg = getMessage(request, "interessat.controller.esborrar.error", new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:/v3/expedient/"+expedientId+"?pipellaActiva=interessats";
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/{representantId}/deleteRepresentant", method = RequestMethod.GET)
	public String deleteRepresentant(
			HttpServletRequest request,
			@RequestParam(value = "interessatId", required=false) Long interessatId,
			@PathVariable Long expedientId,
			@PathVariable Long representantId,
			Model model) {
		try {
			expedientInteressatService.deleteOrUnassignRepresentant(representantId, interessatId);
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.representant.esborrat"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "interessat.controller.representant.error", new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:/v3/expedient/"+expedientId+"?pipellaActiva=interessats";
	}

	private void populateModel(HttpServletRequest request, Model model, String provincia) {
		model.addAttribute(
				"interessatTipusOptions",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.form.tipus.enum."));
		model.addAttribute(
				"interessatTipusDocuments",
				this.populateTipusDocuments(request)
				);
		model.addAttribute(
				"interessatCanalsNotif", 
				this.populateCanalsNotif(request)
				);
		try {
			model.addAttribute("paisos", dadesExternesService.findPaisos());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.paisos.error"));
		}
		try {
			model.addAttribute("provincies", dadesExternesService.findProvincies());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.provincies.error"));
		}
		try {
			List<ParellaCodiValorDto> organs = new ArrayList<ParellaCodiValorDto>();
			for (UnitatOrganitzativaDto uo : unitatOrganitzativaService.findAll()) {
				organs.add(new ParellaCodiValorDto(uo.getCodi(), uo.getCodi() + " - " + uo.getDenominacio()));
			}
			model.addAttribute("organs", organs);
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.unitats.error"));
		}
		if (provincia != null) {
			try {
				model.addAttribute("municipis", dadesExternesService.findMunicipisPerProvincia(provincia));
			} catch (Exception e) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.municipis.error"));
			}
		}
	}
	
	@RequestMapping(value = "/municipis/{codiProvincia}", method = RequestMethod.GET)
	@ResponseBody
	public List<MunicipiDto> getMunicipisByCodiProvincia(
			HttpServletRequest request,
			@PathVariable String codiProvincia,
			Model model) {
		return dadesExternesService.findMunicipisPerProvincia(codiProvincia);
	}
	
	@RequestMapping(value = "/organ/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UnitatOrganitzativaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		UnitatOrganitzativaDto unitat = unitatOrganitzativaService.findByCodi(codi);
		if (unitat != null) {
			unitatOrganitzativaService.populateDadesExternesUO(unitat);
		}
		return unitat;
	}
	
	private void populateDadesInteressat(InteressatDto interessat) {
		if(InteressatTipusEnumDto.ADMINISTRACIO.equals(interessat.getTipus())){
			UnitatOrganitzativaDto uo= unitatOrganitzativaService.findByCodi(interessat.getDocumentIdent());
			if(uo!=null) {
				interessat.setRaoSocial(uo.getDenominacio());
				interessat.setPais(uo.getCodiPais());
				interessat.setCodiPostal(uo.getCodiPostal());
				interessat.setDireccio(uo.getNomVia() +", "+ uo.getNumVia());//tampoc està arribant l'adreça completa, faltaria el tipusVia mapejat
//				interessat.setMunicipi(uo.getLocalitat());//El plugin no està retornant aquest codi
				if(uo.getCodiProvincia()!=null) {
					String codiProvinciaDosDigits = String.format("%02d", uo.getCodiProvincia());//Fa falta que sigui de dos digits pq busqui bé els municipis
					interessat.setProvincia(codiProvinciaDosDigits);
					List<MunicipiDto> municipis = dadesExternesService.findMunicipisPerProvincia(codiProvinciaDosDigits);
					for(MunicipiDto municipi: municipis) {
						if(municipi.getNom().equals(uo.getNomLocalitat())) {//No ens ve informat el número de localitat/municipi en el plugin de uos
							interessat.setMunicipi(municipi.getCodi());
							break;
						}
					}		
				}
			}
		}
		if(interessat.getPais()!=null && !interessat.getPais().isEmpty()) {
			List<PaisDto> paisos =  dadesExternesService.findPaisos();
			for(PaisDto pais: paisos) {
				if(pais.getCodi().equals(interessat.getPais())) {
					interessat.setPaisNom(pais.getNom());
					break;
				}
			}
		}
		if(interessat.getProvincia()!=null && !interessat.getProvincia().isEmpty()) {
			List<ProvinciaDto> provincies = dadesExternesService.findProvincies();
			for(ProvinciaDto provincia: provincies) {
				if(provincia.getCodi().equals(interessat.getProvincia())) {
					interessat.setProvinciaNom(provincia.getNom());
					if(interessat.getMunicipi()!=null && !interessat.getMunicipi().isEmpty()) {
						List<MunicipiDto> municipis = dadesExternesService.findMunicipisPerProvincia(provincia.getCodi());
						for(MunicipiDto municipi: municipis) {
							if(municipi.getCodi().equals(interessat.getMunicipi())) {
								interessat.setMunicipiNom(municipi.getNom());
								break;
							}
						}
					}
					break;
				}
			}
		}	
	}
	
	private void populateUOsCommand(InteressatCommand command) {
		UnitatOrganitzativaDto uo= unitatOrganitzativaService.findByCodi(command.getCodi());
		if(uo!=null) {
			unitatOrganitzativaService.populateDadesExternesUO(uo);
			command.setRaoSocial(uo.getDenominacio());
			command.setPais(uo.getCodiPais());
			command.setMunicipi(uo.getLocalitat());//El plugin no està retornant aquest codi
			command.setCodiPostal(uo.getCodiPostal());
			command.setDireccio(uo.getNomVia() +", "+ uo.getNumVia());//tampoc està arribant l'adreça completa, faltaria el tipusVia mapejat
			if(uo.getCodiProvincia()!=null) {
				String codiProvinciaDosDigits = String.format("%02d", Long.valueOf(uo.getCodiProvincia()));//Fa falta que sigui de dos digits pq busqui bé els municipis
				command.setProvincia(codiProvinciaDosDigits);
				List<MunicipiDto> municipis = dadesExternesService.findMunicipisPerProvincia(codiProvinciaDosDigits);
				for(MunicipiDto municipi: municipis) {
					if(municipi.getNom().equals(uo.getNomLocalitat())) {//No ens ve informat el número de localitat/municipi en el plugin de uos
						command.setMunicipi(municipi.getCodi());
						break;
					}
				}		
			}
		}
	}
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatV3Controller.class);

}
