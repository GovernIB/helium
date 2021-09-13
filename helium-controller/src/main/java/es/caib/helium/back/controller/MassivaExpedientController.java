package es.caib.helium.back.controller;

import es.caib.helium.back.command.CanviVersioProcesCommand;
import es.caib.helium.back.command.DocumentExpedientCommand;
import es.caib.helium.back.command.ExecucioAccioCommand;
import es.caib.helium.back.command.ExpedientEinesAturarCommand;
import es.caib.helium.back.command.ExpedientEinesReassignarCommand;
import es.caib.helium.back.command.ExpedientEinesScriptCommand;
import es.caib.helium.back.command.ModificarVariablesCommand;
import es.caib.helium.back.helper.EnumHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NtiHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.back.helper.TascaFormHelper;
import es.caib.helium.back.helper.TascaFormValidatorHelper;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto;
import es.caib.helium.logic.intf.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.dto.SeleccioOpcioDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;
import es.caib.helium.logic.intf.service.ExpedientDadaService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.TascaService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/massiva")
public class MassivaExpedientController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Autowired
	private TascaService tascaService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Autowired
	private DocumentService documentService;
	@Resource
	private NtiHelper ntiHelper;

	@Autowired
	private Validator validator;



	private Set<Long> guardarIdsAccionesMasivas(HttpServletRequest request, Long consultaId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		sessionManager.removeSeleccioMassives();
		Set<Long> ids = null;
		if (consultaId == null) {
			ids = sessionManager.getSeleccioConsultaGeneral();
		} else {
			ids = sessionManager.getSeleccioInforme(consultaId);
		}
		sessionManager.setSeleccioMassives(ids);
		
		return ids;
	}

	private Set<Long> recuperarIdsAccionesMasivas(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		return sessionManager.getSeleccioMassives();
	}

	@RequestMapping(value = "/expedientsSeleccio", method = RequestMethod.GET)
	public String documentGenerarGet(
			HttpServletRequest request,
			Model model) {
		List<ExpedientDto> expedients = null;
		try {
			Set<Long> ids = recuperarIdsAccionesMasivas(request);
			expedients = expedientService.findAmbIds(ids);
		} catch (Exception e) {
			expedients = new ArrayList<ExpedientDto>();
			MissatgesHelper.error(request, getMessage(request, "error.no.massiu.expedients"));
			logger.error("Error en recuperar la llista dels expedients seleccionats", e);
		}
		model.addAttribute("expedients", expedients);
		return "v3/massivaInfoExpedients";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getExpedient(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = false) Long consultaId,
			@RequestParam(value = "readIdsAccionesMasivas", required = false) Boolean readIdsAccionesMasivas,
			Model model) {
		Set<Long> ids = null;
		if (readIdsAccionesMasivas != null && readIdsAccionesMasivas.equals(true))
			ids = recuperarIdsAccionesMasivas(request);
		else
			ids = guardarIdsAccionesMasivas(request, consultaId);
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec"));
			return "redirect:/v3";
		} if (expedientService.isDiferentsTipusExpedients(ids)) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec.diferenttipus"));
			return "redirect:/v3";
		} else {
			List<Long> listIds = new ArrayList<Long>(ids);			
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(listIds.get(0));
			model.addAttribute("consultaId", consultaId);
			model.addAttribute("numExpedients", ids.size());
			model.addAttribute("inici", null);
			model.addAttribute("correu", false);
			model.addAttribute(new ExpedientEinesScriptCommand());
			model.addAttribute(new ExecucioAccioCommand());
			model.addAttribute(new CanviVersioProcesCommand());
			model.addAttribute(new ExpedientEinesAturarCommand());
			model.addAttribute(new ModificarVariablesCommand());
			if(!model.containsAttribute("documentExpedientCommand")) {
				DocumentExpedientCommand documentExpedientCommand = new DocumentExpedientCommand();
				documentExpedientCommand.setExpedientId(expedient.getId());
				model.addAttribute(documentExpedientCommand);
			}
			model.addAttribute(new ExpedientEinesReassignarCommand());
			model.addAttribute(
					"accions",
					expedientService.accioFindVisiblesAmbProcessInstanceId(
							expedient.getId(),
							expedient.getProcessInstanceId()));
			CanviVersioProcesCommand canviVersioProcesCommand = new CanviVersioProcesCommand();
			DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
			
			canviVersioProcesCommand.setDefinicioProcesId(definicioProces.getId());			
			model.addAttribute(canviVersioProcesCommand);

			model.addAttribute("definicioProces",definicioProces);
			model.addAttribute("subDefinicioProces", dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId()));
			
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedient.getProcessInstanceId());
			model.addAttribute("instanciaProces", instanciaProces);
			
			// Variables
			List<CampDto> variables = new ArrayList<CampDto>();
			if (instanciaProces != null) {
				for (CampDto camp : expedientService.getCampsInstanciaProcesById(
						expedient.getTipus().getId(),
						expedient.getProcessInstanceId())){
					if (!CampTipusDto.ACCIO.equals(camp.getTipus())) {
						variables.add(camp);
					}
				}
			}
			Collections.sort(variables, new ComparadorCampCodi());
			model.addAttribute("variables", variables);
			// Documents			
			List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					expedient.getTipus().getId(),
					definicioProces.getId(),
					true);
			Collections.sort(documents, new ComparadorDocument());
			model.addAttribute("documents", documents);
			
			model.addAttribute("permisAdministrador", expedient.isPermisAdministration());
			return "v3/massivaInfo";
		}
	}

	public class ComparadorCampCodi implements Comparator<CampDto> {
	    public int compare(CampDto c1, CampDto c2) {
	        return c1.getCodi().compareToIgnoreCase(c2.getCodi());
	    }
	}

	public class ComparadorDocument implements Comparator<DocumentDto> {
	    public int compare(DocumentDto d1, DocumentDto d2) {
	        return d1.getDocumentNom().compareToIgnoreCase(d2.getDocumentNom());
	    }
	}

	@RequestMapping(value="reindexarMas", method = RequestMethod.POST)
	public String reindexarMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model, null, null);
	}

	@RequestMapping(value="buidarlogMas", method = RequestMethod.POST)
	public String buidarlogMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model, null, null);
	}

	@RequestMapping(value="reprendreExpedientMas", method = RequestMethod.POST)
	public String reprendreExpedientMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model, null, null);
	}
	
	@RequestMapping(value="finalitzarExpedientMas", method = RequestMethod.POST)
	public String finalitzarExpedientMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model, null, null);
	}
	
	@RequestMapping(value="migrarExpedientMas", method = RequestMethod.POST)
	public String migrarExpedientMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			Model model) {
		return massivaPost(request, inici, correu, null, accio, null, null, model, null, null);
	}

	@RequestMapping(value="massivaExecutarAccio", method = RequestMethod.POST)
	public String execucioAccioCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExecucioAccioCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, null, null);
	}

	@RequestMapping(value="aturarMas", method = RequestMethod.POST)
	public String expedientEinesAturarCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExpedientEinesAturarCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, null, null);
	}

	@RequestMapping(value="scriptMas", method = RequestMethod.POST)
	public String expedientEinesScriptCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute ExpedientEinesScriptCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, null, null);
	}

	@RequestMapping(value="massivaCanviVersio", method = RequestMethod.POST)
	public String canviVersioProcesCommandPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@ModelAttribute CanviVersioProcesCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, null, null);
	}

	public String massivaPost(
			HttpServletRequest request,
			String inici,
			boolean correu,
			Object command, 
			String accio,
			BindingResult result, 
			SessionStatus status, 
			Model model, 
			String multipartName,
			Long campId) {		
		
		getExpedient(request, null, true, model);
		Set<Long> ids = recuperarIdsAccionesMasivas(request);
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec"));
			return "redirect:/v3";
		}

		Date dInici = new Date();
		if (inici != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			try {
				dInici = sdf.parse(inici);
			} catch (ParseException e) {}
		}

		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		
		List<Long> listIds = new ArrayList<Long>(ids);
		
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setDataInici(dInici);
		dto.setEnviarCorreu(correu);
		dto.setExpedientIds(listIds);
		ExpedientDto expedientAux = expedientService.findAmbIdAmbPermis(listIds.get(0));
		dto.setExpedientTipusId(expedientAux.getTipus().getId());
		
		try {					
			if ("reindexar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.REINDEXAR);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.reindexar.massiu.executat", new Object[] {listIds.size()}));
			} else if ("buidarlog".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.BUIDARLOG);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.buidarlog.massiu.executat", new Object[] {listIds.size()}));
			} else if ("reprendreExpedient".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.REPRENDRE_EXPEDIENT);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.accio.massiu.reprendre_expedient", new Object[] {listIds.size()}));
			} else if ("finalitzarExpedient".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.FINALITZAR_EXPEDIENT);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.accio.massiu.finalitzar_expedient", new Object[] {listIds.size()}));
			} else if ("migrarExpedient".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MIGRAR_EXPEDIENT);
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.accio.massiu.migrar_expedient", new Object[] {listIds.size()}));
			} else if ("executar".equals(accio)) {
				new ExpedientScriptValidator().validate(command, result);
				if (result.hasErrors()) {
					model.addAttribute("expedientEinesScriptCommand", command);
					MissatgesHelper.error(request, getMessage(request, "error.executar.script"));
				} else {
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_SCRIPT);
					dto.setParam2(execucioMassivaService.serialize(((ExpedientEinesScriptCommand) command).getScript()));
					execucioMassivaService.crearExecucioMassiva(dto);
					MissatgesHelper.success(request, getMessage(request, "info.script.massiu.executat", new Object[] {listIds.size()}));
					model.addAttribute("expedientEinesScriptCommand", command);
				}
			} else if ("aturar".equals(accio)) {
				new ExpedientAturarValidator().validate(command, result);
				if (result.hasErrors()) {
					model.addAttribute("expedientEinesAturarCommand", command);
					MissatgesHelper.error(request, getMessage(request, "error.aturar.expedient"));
				} else {
					dto.setTipus(ExecucioMassivaTipusDto.ATURAR_EXPEDIENT);
					dto.setParam2(execucioMassivaService.serialize(((ExpedientEinesAturarCommand) command).getMotiu()));
					execucioMassivaService.crearExecucioMassiva(dto);		
					MissatgesHelper.success(request, getMessage(request, "info.expedient.massiu.aturats", new Object[] {listIds.size()}));
					model.addAttribute("expedientEinesAturarCommand", command);
				}
			} else if ("executar_accio".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_ACCIO);
					
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Object[] params = new Object[1];
				params[0] = ((ExecucioAccioCommand) command).getAccioCodi();
//				params[1] = auth.getCredentials();
//				List<String> rols = new ArrayList<String>();
//				for (GrantedAuthority gauth : auth.getAuthorities()) {
//					rols.add(gauth.getAuthority());
//				}
//				params[2] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);				
				MissatgesHelper.success(request, getMessage(request, "info.accio.massiu.executat", new Object[] {listIds.size()}));
				model.addAttribute("execucioAccioCommand", command);
			} else if ("canviar_versio".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.ACTUALITZAR_VERSIO_DEFPROC);
				Object[] params = new Object[3];
				params[0] = ((CanviVersioProcesCommand) command).getDefinicioProcesId();
				params[1] = ((CanviVersioProcesCommand) command).getSubprocesId();
								
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(listIds.get(0));
				DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
				List<DefinicioProcesExpedientDto> supProcessos = dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId());

				String[] keys = new String[supProcessos.size()];
				int i = 0;
				for (DefinicioProcesExpedientDto subproces: supProcessos) {
					keys[i++] = subproces.getJbpmKey();
				}
				params[2] = keys;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.canvi.versio.massiu", new Object[] {listIds.size()}));
				model.addAttribute("canviVersioProcesCommand", command);
			} else if ("document_esborrar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				DocumentDto document = dissenyService.documentFindOne(docId);
				dto.setParam1(document.getDocumentNom());
				
				Object[] params = new Object[4];
				params[0] = docId;
				params[1] = null;
				params[2] = "delete";
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.document.massiu.esborrar", new Object[] {listIds.size()}));
				model.addAttribute("documentExpedientCommand", command);
			} else if ("document_generar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				DocumentDto document = dissenyService.documentFindOne(docId);
				dto.setParam1(document.getDocumentNom());
				
				Object[] params = new Object[4];
				params[0] = docId;
				params[1] = new Date();
				params[2] = "generate";
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.document.massiu.generar", new Object[] {listIds.size()}));
				model.addAttribute("documentExpedientCommand", command);
			} else if ("document_adjuntar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				Object[] params = new Object[12];
				params[0] = ((DocumentExpedientCommand)command).getDocId();
				params[1] = ((DocumentExpedientCommand)command).getData();
				String titol = ((DocumentExpedientCommand)command).getNom();
				params[2] = (titol != null)? titol : ((DocumentExpedientCommand)command).getCodi();
				if (((DocumentExpedientCommand)command).getArxiu().getBytes().length > 0) {
					dto.setParam1(multipartName);
					params[3] = ((DocumentExpedientCommand) command).getArxiu().getBytes();
				}
				params[4] = ((DocumentExpedientCommand)command).getNtiOrigen();
				params[5] = ((DocumentExpedientCommand)command).getNtiEstadoElaboracion();
				params[6] = ((DocumentExpedientCommand)command).getNtiTipoDocumental();
				params[7] = ((DocumentExpedientCommand)command).getNtiIdOrigen();
				if (((DocumentExpedientCommand)command).getArxiu().getBytes().length > 0) {
					params[8] = ((DocumentExpedientCommand) command).getArxiu().getContentType();
				}
				params[9] = ((DocumentExpedientCommand)command).isAmbFirma();
				params[10] = DocumentTipusFirmaEnumDto.SEPARAT.equals(((DocumentExpedientCommand)command).getTipusFirma());
				if (((DocumentExpedientCommand)command).getFirma().getBytes().length > 0) {
					params[11] = ((DocumentExpedientCommand) command).getFirma().getBytes();
				}
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				MissatgesHelper.success(request, getMessage(request, "info.document.massiu.guardat", new Object[] {listIds.size()}));
				return modalUrlTancar();
			} else if ("document_modificar".equals(accio)) {
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_DOCUMENT);
				Long docId = ((DocumentExpedientCommand) command).getDocId();
				Object[] params = new Object[12];
				params[0] = docId;
				
				if (((DocumentExpedientCommand) command).getArxiu().getBytes().length > 0) {
					// Modificar document
					dto.setParam1(multipartName);

					params[1] = ((DocumentExpedientCommand)command).getData();
					params[2] = ((DocumentExpedientCommand)command).getCodi();
					params[3] = ((DocumentExpedientCommand)command).getArxiu().getBytes();
				} else {
					// Modificar data
					dto.setParam1(((DocumentExpedientCommand)command).getCodi());
					params[1] = ((DocumentExpedientCommand)command).getData();
					params[2] = "date";
					params[3] = null;
				}
				params[4] = ((DocumentExpedientCommand)command).getNtiOrigen();
				params[5] = ((DocumentExpedientCommand)command).getNtiEstadoElaboracion();
				params[6] = ((DocumentExpedientCommand)command).getNtiTipoDocumental();
				params[7] = ((DocumentExpedientCommand)command).getNtiIdOrigen();
				if (((DocumentExpedientCommand)command).getArxiu().getBytes().length > 0) {
					params[8] = ((DocumentExpedientCommand) command).getArxiu().getContentType();
				}
				params[9] = ((DocumentExpedientCommand)command).isAmbFirma();
				params[10] = DocumentTipusFirmaEnumDto.SEPARAT.equals(((DocumentExpedientCommand)command).getTipusFirma());
				if (((DocumentExpedientCommand)command).getFirma().getBytes().length > 0) {
					params[11] = ((DocumentExpedientCommand) command).getFirma().getBytes();
				}
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.document.massiu.guardat", new Object[] {listIds.size()}));
				return modalUrlTancar();
			} else if ("modificar_variable".equals(accio)) {
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();

				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(listIds.get(0));
				String varCodi = null;
				String processInstanceId = expedient.getProcessInstanceId();
				for (CampDto camp : expedientService.getCampsInstanciaProcesById(
						expedient.getTipus().getId(),
						processInstanceId)){
					if (!CampTipusDto.ACCIO.equals(camp.getTipus()) && campId.equals(camp.getId())) {
						varCodi = camp.getCodi();
					}
				}

				List<ExpedientDadaDto> expedientDades = expedientDadaService.findAmbInstanciaProces(
						expedient.getId(),
						processInstanceId);
				List<TascaDadaDto> tascaDades = new ArrayList<TascaDadaDto>();
				for (ExpedientDadaDto expedientDada: expedientDades) {
					if (expedientDada.getVarCodi().equals(varCodi)) {
						tascaDades.add(
								TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
										expedientDada));
						break;
					}
				}
				Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(
						tascaDades,
						command,
						false);
				TascaFormValidatorHelper validatorHelper = new TascaFormValidatorHelper(
						expedientService,
						tascaDades,
						processInstanceId);
				Object commandPerValidacio = TascaFormHelper.getCommandForCampsExpedient(
						expedientDades,
						variables,
						expedient.getProcessInstanceId());

//				validator.validate(commandPerValidacio, result);
				validatorHelper.validate(commandPerValidacio, result);
				if (result.hasErrors()) {
					model.addAttribute("modificarVariablesCommand", command);
					return "v3/massivaInfoModificarVariables";
		        }
				dto.setTipus(ExecucioMassivaTipusDto.MODIFICAR_VARIABLE);
				dto.setParam1(varCodi);
				Object valors = PropertyUtils.getSimpleProperty(command, varCodi);
				Object[] params = new Object[] {entorn.getId(), null, valors};
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				MissatgesHelper.success(request, getMessage(request, "info.dada.massiu.modificat", new Object[] {varCodi, listIds.size()}));
				return modalUrlTancar();
			}
		} catch (Exception e) {
			MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
			logger.error("Error al programar les accions massives", e);
		}
		
		return "redirect:/v3/expedient/massiva?readIdsAccionesMasivas=true";
	}
	
	@ModelAttribute("modificarVariablesCommand")
	public Object populateCommand(
			HttpServletRequest request,
			Long campId,			
			Model model) {
		try {
//			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
//			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			Set<Long> ids = recuperarIdsAccionesMasivas(request);
			List<Long> listIds = new ArrayList<Long>(ids);			
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(listIds.get(0));
			CampDto campo = null;
			for (CampDto camp : expedientService.getCampsInstanciaProcesById(
					expedient.getTipus().getId(),
					expedient.getProcessInstanceId())){
				if (!CampTipusDto.ACCIO.equals(camp.getTipus()) && campId.equals(camp.getId())) {
					campo = camp;
				}
			}
			List<TascaDadaDto> listTasca = new ArrayList<TascaDadaDto>();
			TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
					expedientDadaService.findOnePerInstanciaProces(
							expedient.getId(),
							expedient.getProcessInstanceId(),
							campo.getCodi()));
			// Si la variable és múltiple i té valors es esborra
			if (tascaDada.isCampMultiple() && tascaDada.getMultipleDades().size() > 0) {
				while (tascaDada.getMultipleDades().size() > 1)
					tascaDada.getMultipleDades().remove(0);
				tascaDada.getMultipleDades().get(0).setVarValor(null);
				tascaDada.setVarValor(null);
			}
			listTasca.add(tascaDada);
			model.addAttribute("campId", campId);			
			model.addAttribute("dada", tascaDada);
			return TascaFormHelper.getCommandForCamps(
					listTasca,
					null,
					null, //campsAddicionals,
					null); //campsAddicionalsClasses);
		} catch (NoTrobatException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		} catch (Exception ignored) {} 
		return null;
	}
	
	@RequestMapping(value = "/{campId}/modificarVariables", method = RequestMethod.GET)
	public String modificarVariablesGet(
			HttpServletRequest request,
			@PathVariable Long campId,
			Model model) {
		Object command = populateCommand(request, campId, model);
		model.addAttribute("modificarVariablesCommand", command);
		return "v3/massivaInfoModificarVariables";
	}

	@RequestMapping(value = "/documentMassiu", method = RequestMethod.GET)
	public String documentMasForm(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "docId", required = false) Long docId,
			@RequestParam(value = "adjuntar", required = false, defaultValue = "true") boolean adjuntar,
			Model model) {
		return this.documentMassiu(request, model, inici, correu, docId, adjuntar, null);
	}
	
	/**
	/** Mètode comú per adjuntar o modificar un document massivament. 
	 * @param model 
	 * @param request */
	private String documentMassiu(
			HttpServletRequest request, 
			Model model, 
			String inici, 
			Boolean correu, 
			Long docId, 
			Boolean adjuntar,
			DocumentExpedientCommand command) {
		if (command == null){
			command = new DocumentExpedientCommand();
			model.addAttribute("documentExpedientCommand", command);
		}
		command.setData(new Date());
		command.setDocId(docId);
		model.addAttribute("inici", inici);
		model.addAttribute("correu", correu);
		Set<Long> ids = recuperarIdsAccionesMasivas(request);
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.exp.selec"));
			return "redirect:/v3";
		}
		DocumentDto documentDto = null;
		if (docId != null) {
			documentDto = documentService.findAmbId(null, docId);
			command.setNom(documentDto.getNom());
		}
		// NTI
		List<Long> listIds = new ArrayList<Long>(ids);
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(listIds.get(0));
		command.setExpedientId(expedient.getId());
		command.setValidarArxius(true);
		// Mira si el tipus d'expedient té metadades NTI i tots els primer seleccionat
		if (expedient.isNtiActiu()) {
			model.addAttribute("metadades", true);
			ntiHelper.omplirOrigen(model);
			ntiHelper.omplirEstadoElaboracion(model);
			ntiHelper.omplirTipoDocumental(model);
			ntiHelper.omplirTipoFirma(model);
			model.addAttribute("expedient", expedient);
			if (documentDto != null) {
				command.setNtiEstadoElaboracion(documentDto.getNtiEstadoElaboracion());
				command.setNtiOrigen(documentDto.getNtiOrigen());
				command.setNtiTipoDocumental(documentDto.getNtiTipoDocumental());
			}
		}
		if (docId != null) {
			DocumentDto document = dissenyService.documentFindOne(docId);
			command.setNom(document.getDocumentNom());
			command.setCodi(document.getCodi());
			model.addAttribute("document", document);			
		}
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		model.addAttribute("adjuntar", adjuntar);
		return "v3/massivaInfoDocumentForm";
	}
	
	@RequestMapping(value="/documentMasForm", method = RequestMethod.POST)
	public String documentMasFormPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			@RequestParam(value = "adjuntar", required = false, defaultValue = "true") boolean adjuntar,
			SessionStatus status, 
			@Validated(DocumentExpedientCommand.Massiu.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult result, 
			Model model) {	
		if (result.hasErrors()) {
			return this.documentMassiu(request, model, inici, correu, command.getDocId(), adjuntar, command);
		}
		return massivaPost(request, inici, correu, command, accio, result, status, model, request.getParameter("arxiuNom"), null);
	}

	/** Mètode que es crida sense formulari */
	@RequestMapping(value="/documentMas", method = RequestMethod.POST)
	public String documentMasInfoPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "accio", required = true) String accio,
			@ModelAttribute DocumentExpedientCommand command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, request.getParameter("arxiuNom"), null);		
	}

	@RequestMapping(value="/{campId}/modificarVariablesMas", method = RequestMethod.POST)
	public String modificarVariablesMasPost(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@PathVariable Long campId,
			@Valid @ModelAttribute("modificarVariablesCommand") Object command, 
			@RequestParam(value = "accio", required = true) String accio,
			BindingResult result, 
			SessionStatus status,
			Model model) {		
		return massivaPost(request, inici, correu, command, accio, result, status, model, null, campId);
	}

	@RequestMapping(value = "/{campId}/camp/{campId}/valorsSeleccio", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long campId,
			Model model) throws Exception {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				null,
				null,
				null,
				null,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = "/{campId}/camp/{campId}/valorsSeleccio/{valor}", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) throws Exception {
		return tascaService.findValorsPerCampDesplegable(
				null,
				null,
				campId,
				valor,
				null,
				null,
				null,
				new HashMap<String, Object>());
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValor> valors12(HttpServletRequest request) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValor(String.valueOf(i), i));
		return resposta;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
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



	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
	}
	
	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}

	private static final Log logger = LogFactory.getLog(MassivaExpedientController.class);

}
