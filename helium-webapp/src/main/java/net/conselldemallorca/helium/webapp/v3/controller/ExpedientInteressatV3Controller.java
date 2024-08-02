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

import net.conselldemallorca.helium.core.helper.UnitatOrganitzativaHelper;
import net.conselldemallorca.helium.v3.core.api.dto.CanalNotifEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDocumentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
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
	@Autowired private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

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
		//populateModel(request, model);
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
		for(InteressatDto interessat: expedientInteressatService.findByExpedient(expedientId)) {
			if(!interessat.getEs_representant() && interessat.getRepresentant()!=null) {
				representantInteressat = expedientInteressatService.findOne(interessat.getRepresentant().getId());
				interessat.setRepresentant(representantInteressat);
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

	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		InteressatCommand interessatCommand= new InteressatCommand();
		populateModel(request, model);
		interessatCommand.setPais("724");
		model.addAttribute("expedientId", expedientId);
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
		model.addAttribute(
				"organs",
				unitatOrganitzativaHelper.findAll()
				);
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
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	try {
        		populateModel(request, model);
	        	expedientInteressatService.create(
	    				ConversioTipusHelper.convertir(
	    						command,
	    						InteressatDto.class));
				MissatgesHelper.success(request, getMessage(request, "interessat.controller.creat") );
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
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
		model.addAttribute("tipus",dto.getTipus());
		model.addAttribute("es_representant",dto.getEs_representant());
		//posem el valor de l'enum tal com apareix al llistat
		if(dto.getTipusDocIdent()==null) { //En el cas d'interessats antics no té tipusDocIdent li posem NIF de moment
			dto.setTipusDocIdent(InteressatDocumentTipusEnumDto.NIF.name());
		} else {
			dto.setTipusDocIdent(InteressatDocumentTipusEnumDto.valorAsEnum(dto.getTipusDocIdent()).name());
		}
		populateModel(request, model);
		if (dto.getProvincia() != null) {
			model.addAttribute("municipis", dadesExternesService.findMunicipisPerProvincia(dto.getProvincia()));
		}
		interessatCommand = ConversioTipusHelper.convertir(
				dto,
				InteressatCommand.class);
		if (InteressatTipusEnumDto.ADMINISTRACIO.equals(dto.getTipus())) {
			
				interessatCommand.setCifOrganGestor(interessatCommand.getDocumentIdent());
				model.addAttribute(
						"organs",
						unitatOrganitzativaHelper.findAll()
						);			
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
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
    		populateModel(request, model);
        	command.setEs_representant(es_representant);
        	expedientInteressatService.update(
        			ConversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.modificat") );
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/new", method = RequestMethod.GET)
	public String newRepresentantGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		InteressatCommand interessatCommand= new InteressatCommand();
		populateModel(request, model);
		interessatCommand.setPais("724");
		interessatCommand.setEs_representant(true);
		model.addAttribute("expedientId", expedientId);
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
		model.addAttribute(
				"organs",
				unitatOrganitzativaHelper.findAll()
				);
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
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
    		populateModel(request, model);
        	command.setEs_representant(true);
        	expedientInteressatService.createRepresentant(
        			interessatId,
    				ConversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.representant.creat") );
  			return modalUrlTancar(false);
        }
	}
	
	
	@ModelAttribute("interessatTipusEstats")
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.enum.ADMINISTRACIO"), InteressatTipusEnumDto.ADMINISTRACIO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.enum.FISICA"), InteressatTipusEnumDto.FISICA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "interessat.tipus.enum.JURIDICA"), InteressatTipusEnumDto.JURIDICA));
		return resposta;
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
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		try {
			expedientInteressatService.delete(interessatId);
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.esborrat"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "interessat.controller.esborrar.error", new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:/v3/expedient/"+expedientId+"?pipellaActiva=interessats";
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/cercar", method = RequestMethod.GET)
	public String cercarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		InteressatDto dto = expedientInteressatService.findOne(
				interessatId);
		model.addAttribute("tipus",dto.getTipus());
		model.addAttribute("es_representant",dto.getEs_representant());
		//posem el valor de l'enum tal com apareix al llistat
		if(dto.getTipusDocIdent()==null) { //En el cas d'interessats antics no té tipusDocIdent li posem NIF de moment
			dto.setTipusDocIdent(InteressatDocumentTipusEnumDto.NIF.getValor());
		}
		//dto.setTipusDocIdent(InteressatDocumentTipusEnumDto.valorAsEnum(dto.getTipusDocIdent()).name());
		model.addAttribute(
				ConversioTipusHelper.convertir(
						dto,
						InteressatCommand.class));
		return "v3/interessatForm";
	}
	@RequestMapping(value = "/{expedientId}/interessat/cercar", method = RequestMethod.POST)
	public String cercarPost(
			HttpServletRequest request,
			@RequestParam(value = "es_representant" , required=false) boolean es_representant,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@Validated(Modificacio.class) InteressatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/interessatForm";
        } else {
        	command.setEs_representant(es_representant);
        	expedientInteressatService.update(
        			ConversioTipusHelper.convertir(
    						command,
    						InteressatDto.class));
			MissatgesHelper.success(request, getMessage(request, "interessat.controller.modificat") );
			return modalUrlTancar(false);
        }
	}
	
	private void populateModel(HttpServletRequest request, Model model) {
		try {
			model.addAttribute("paisos", dadesExternesService.findPaisos());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.paisos.error"));
		}
//		try {
//			model.addAttribute("comunitats", dadesExternesService.findComunitats());
//		} catch (Exception e) {
//			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.comunitats.error"));
//		}
		try {
			model.addAttribute("provincies", dadesExternesService.findProvincies());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.provincies.error"));
		}
//		try {
//			model.addAttribute("nivellAdministracions", dadesExternesService.findNivellAdministracions());
//		} catch (Exception e) {
//			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.nivell.administracio.error"));
//		}
//		boolean dehActiu = false;
//		try {
//			dehActiu = Boolean.parseBoolean(configService.getConfigValue("es.caib.ripea.notificacio.enviament.deh.activa"));
//		} catch (Exception e) {}
//		model.addAttribute("dehActiu", dehActiu);
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
		return unitatOrganitzativaService.findByCodi(codi);
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatV3Controller.class);

}
