/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.extern.domini.DominiHeliumException;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.PersonaService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per la gestió de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaController extends BaseController {

	public static final String SESSIO_TRAMITACIO_MASSIVA = "HEL_TRAM_MASS";

	private String TAG_FORM_INICI = "<!--helium:form-inici-->";
	private String TAG_FORM_FI = "<!--helium:form-fi-->";

	private TascaService tascaService;
	private TerminiService terminiService;
	private DissenyService dissenyService;
	private PersonaService  personaService;
	private ExecucioMassivaService execucioMassivaService;
	
	@Autowired
	public TascaController(
			TascaService tascaService,
			TerminiService terminiService,
			DissenyService dissenyService,
			PersonaService  personaService,
			ExecucioMassivaService execucioMassivaService) {
		this.tascaService = tascaService;
		this.terminiService = terminiService;
		this.dissenyService = dissenyService;
		this.personaService = personaService;
		this.execucioMassivaService = execucioMassivaService;
	}

	@ModelAttribute("prioritats")
	public List<HashMap<String, Object>> populateEstats(HttpServletRequest request) {
		List<HashMap<String, Object>> resposta = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> moltAlta = new HashMap<String, Object>();
		moltAlta.put("value", 2);
		moltAlta.put("label", getMessage("txt.m_alta") );
		resposta.add(moltAlta);
		HashMap<String, Object> alta = new HashMap<String, Object>();
		alta.put("value", 1);
		alta.put("label", getMessage("txt.alta") );
		resposta.add(alta);
		HashMap<String, Object> normal = new HashMap<String, Object>();
		normal.put("value", 0);
		normal.put("label", getMessage("txt.normal") );
		resposta.add(normal);
		HashMap<String, Object> baixa = new HashMap<String, Object>();
		baixa.put("value", -1);
		baixa.put("label", getMessage("txt.baixa") );
		resposta.add(baixa);
		HashMap<String, Object> moltBaixa = new HashMap<String, Object>();
		moltBaixa.put("value", -2);
		moltBaixa.put("label", getMessage("txt.m_baixa") );
		resposta.add(moltBaixa);
		return resposta;
	}

	@ModelAttribute("seleccioMassiva")
	public List<TascaLlistatDto> populateSeleccioMassiva(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id) {
		if (id != null) {
			Entorn entorn = getEntornActiu(request);
			if (entorn != null) {
				String[] ids = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
				if (ids != null) {
					List<TascaLlistatDto> tasquesTramitacioMassiva = tascaService.findTasquesPerTramitacioMassiva(
							entorn.getId(),
							null,
							id);
					for (Iterator<TascaLlistatDto> it = tasquesTramitacioMassiva.iterator(); it.hasNext();) {
						TascaLlistatDto tasca = it.next();
						boolean trobada = false;
						for (String tascaId: ids) {
							if (tascaId.equals(tasca.getId())) {
								trobada = true;
								break;
							}
						}
						if (!trobada)
							it.remove();
					}
					return tasquesTramitacioMassiva;
				}
			}
		}
		return null;
	}

	@RequestMapping(value = "/tasca/massivaSeleccio", method = RequestMethod.GET)
	public String massivaSeleccio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (!TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
				TramitacioMassiva.netejarTramitacioMassiva(request);
			}
			List<TascaLlistatDto> tasquesTramitacioMassiva = tascaService.findTasquesPerTramitacioMassiva(
					entorn.getId(),
					null,
					id);
			model.addAttribute("terminisIniciats", findTerminisIniciatsPerTasques(tasquesTramitacioMassiva));
			model.addAttribute("personaLlistat", tasquesTramitacioMassiva);
			model.addAttribute(
					"personaLlistatCount",
					tascaService.countTasquesPersonalsEntorn(
							entorn.getId(),
							null));
			model.addAttribute(
					"grupLlistatCount",
					tascaService.countTasquesGrupEntorn(
							entorn.getId(),
							null));
			return "tasca/massivaSeleccio";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/massivaTramitacio", method = RequestMethod.POST)
	public String massivaTramitacio(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "tascaId", required = false) String[] tascaId,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) String correu,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (tascaId == null || tascaId.length == 0) {
				missatgeError(request, getMessage("error.no.tasques.selec"));
				return "redirect:/tasca/massivaSeleccio.html?id=" + id;
			} else {
				TramitacioMassiva.iniciarTramitacioMassiva(request, id, tascaId);
				 TramitacioMassiva.setParamsTramitatcio(request, inici, correu, id);
				return "redirect:/tasca/info.html?id=" + id + "&massiva=s";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec"));
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/info")
	public String info(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "massiva", required = false) String massiva,
			@RequestParam(value = "ini", required = false) String ini,
			ModelMap model) throws DominiHeliumException {
		Entorn entorn = getEntornActiu(request);
		TascaDto tasca = tascaService.getByIdSenseComprovacio(id);
		Long tid = tasca.getExpedient().getTipus().getId();
		if (entorn != null) {
			if (massiva == null || !massiva.equalsIgnoreCase("s")) {
				TramitacioMassiva.netejarTramitacioMassiva(request);
				model.remove("seleccioMassiva");
			}
			TascaDelegacioCommand command = new TascaDelegacioCommand();
			command.setTaskId(id);
			model.addAttribute(
					"command",
					command);
			try {
				model.addAttribute(
						"tasca",
						tascaService.getById(
								entorn.getId(),
								id,
								null,
								null,
								false,
								false));
			} catch (Exception ex) {
				logger.error("S'ha produït un error processant la seva petició", ex);
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				return "redirect:/tasca/personaLlistat.html";
			}
			if ("s".equals(ini)) {
				
				if(tasca.isDelegacioOriginal()){
					return "redirect:/tasca/info.html?id="+id;
				}else{
					
					if (!tasca.getCamps().isEmpty()) {
						return "redirect:/tasca/form.html?id="+id;
					} else if(!tasca.getDocuments().isEmpty()) {
						return "redirect:/tasca/documents.html?id="+id;
					} else if (!tasca.getSignatures().isEmpty()) {
						return "redirect:/tasca/signatures.html?id="+id;
						
					}	
					
				}
			}
			Set<PersonaDto> destinataris =  personaService.findPersonesAmbPermisosPerExpedientTipus(tid);
			model.addAttribute(
					 "destinataris",
					 destinataris);
	        return "tasca/info";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/agafar")
	public String agafar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		TascaDto tasca = tascaService.getByIdSenseComprovacio(id);
		if (entorn != null) {
			try {
				tascaService.agafar(entorn.getId(), id);
				missatgeInfo(request, getMessage("info.tasca.disponible.personals") );
				
				if(tasca.isDelegacioOriginal()){
					return "redirect:/tasca/info.html?id="+id;
				}else{
					
					if (!tasca.getCamps().isEmpty()) {
						return "redirect:/tasca/form.html?id="+id;
					} else if(!tasca.getDocuments().isEmpty()) {
						return "redirect:/tasca/documents.html?id="+id;
					} else if (!tasca.getSignatures().isEmpty()) {
						return "redirect:/tasca/signatures.html?id="+id;
					}	
				}
				
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut agafar la tasca", ex);
	        	return "redirect:/tasca/grupLlistat.html";
	        }
			return "redirect:/tasca/info.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/completar")
	public String completar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "pipella", required = false) String pipella,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			boolean ok = accioCompletarTasca(
					request,
					entorn.getId(),
					id,
					submit);
			if (ok) {
				return "redirect:/tasca/personaLlistat.html";
			} else {
				if ("info".equals(pipella)) {
	        		return "redirect:/tasca/info.html?id=" + id;
	        	} else if ("form".equals(pipella)) {
	        		return "redirect:/tasca/form.html?id=" + id;
	        	} else if ("documents".equals(pipella)) {
	        		return "redirect:/tasca/documents.html?id=" + id;
	        	} else if ("signatures".equals(pipella)) {
	        		return "redirect:/tasca/signatures.html?id=" + id;
	        	} else {
	        		return "redirect:/tasca/info.html?id=" + id;
	        	}
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/formRecurs")
	public String formRecurs(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						null,
						false,
						false);
				byte[] contingut = dissenyService.getDeploymentResource(
						tasca.getDefinicioProces().getId(),
						tasca.getRecursForm());
				String text = textFormRecursProcessat(tasca, new String(contingut, "UTF-8"));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_FILENAME,
						tasca.getRecursForm().substring(tasca.getRecursForm().lastIndexOf("/") + 1));
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA,
						text.getBytes());
				return "arxiuView";
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut mostrar l'arxiu", ex);
				return "redirect:/tasca/info.html?id=" + id;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	private String textFormRecursProcessat(TascaDto tasca, String text) {
		int indexFormInici = text.indexOf(TAG_FORM_INICI);
		int indexFormFi = text.indexOf(TAG_FORM_FI);
		if (indexFormInici != -1 && indexFormFi != -1) {
			return text.substring(
					indexFormInici + TAG_FORM_INICI.length(),
					indexFormFi);
		}
		return null;
	}
	
	public class TascaPersonaFiltreCommand {
		private String nom;
		private String expedient;
		private Long tipusExpedient;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Integer prioritat;
		private Date dataLimitInici;
		private Date dataLimitFi;
		private String columna = "3";
		private String ordre = "2";
		public TascaPersonaFiltreCommand() {}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getExpedient() {
			return expedient;
		}
		public void setExpedient(String expedient) {
			this.expedient = expedient;
		}
		public Long getTipusExpedient() {
			return tipusExpedient;
		}
		public void setTipusExpedient(Long tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}
		public Date getDataCreacioInici() {
			return dataCreacioInici;
		}
		public void setDataCreacioInici(Date dataCreacioInici) {
			this.dataCreacioInici = dataCreacioInici;
		}
		public Date getDataCreacioFi() {
			return dataCreacioFi;
		}
		public void setDataCreacioFi(Date dataCreacioFi) {
			this.dataCreacioFi = dataCreacioFi;
		}
		public Integer getPrioritat() {
			return prioritat;
		}
		public void setPrioritat(Integer prioritat) {
			this.prioritat = prioritat;
		}
		public Date getDataLimitInici() {
			return dataLimitInici;
		}
		public void setDataLimitInici(Date dataLimitInici) {
			this.dataLimitInici = dataLimitInici;
		}
		public Date getDataLimitFi() {
			return dataLimitFi;
		}
		public void setDataLimitFi(Date dataLimitFi) {
			this.dataLimitFi = dataLimitFi;
		}
		public String getColumna() {
			return columna;
		}
		public void setColumna(String columna) {
			this.columna = columna;
		}
		public String getOrdre() {
			return ordre;
		}
		public void setOrdre(String ordre) {
			this.ordre = ordre;
		}
	}
	
	public class TascaGrupFiltreCommand {
		private String nom;
		private String expedient;
		private Long tipusExpedient;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private Integer prioritat;
		private Date dataLimitInici;
		private Date dataLimitFi;
		private String columna = "3";
		private String ordre = "2";
		public TascaGrupFiltreCommand() {}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getExpedient() {
			return expedient;
		}
		public void setExpedient(String expedient) {
			this.expedient = expedient;
		}
		public Long getTipusExpedient() {
			return tipusExpedient;
		}
		public void setTipusExpedient(Long tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}
		public Date getDataCreacioInici() {
			return dataCreacioInici;
		}
		public void setDataCreacioInici(Date dataCreacioInici) {
			this.dataCreacioInici = dataCreacioInici;
		}
		public Date getDataCreacioFi() {
			return dataCreacioFi;
		}
		public void setDataCreacioFi(Date dataCreacioFi) {
			this.dataCreacioFi = dataCreacioFi;
		}
		public Integer getPrioritat() {
			return prioritat;
		}
		public void setPrioritat(Integer prioritat) {
			this.prioritat = prioritat;
		}
		public Date getDataLimitInici() {
			return dataLimitInici;
		}
		public void setDataLimitInici(Date dataLimitInici) {
			this.dataLimitInici = dataLimitInici;
		}
		public Date getDataLimitFi() {
			return dataLimitFi;
		}
		public void setDataLimitFi(Date dataLimitFi) {
			this.dataLimitFi = dataLimitFi;
		}
		public String getColumna() {
			return columna;
		}
		public void setColumna(String columna) {
			this.columna = columna;
		}
		public String getOrdre() {
			return ordre;
		}
		public void setOrdre(String ordre) {
			this.ordre = ordre;
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}



	private List<TerminiIniciat> findTerminisIniciatsPerTasques(List<TascaLlistatDto> tasques) {
		List<TerminiIniciat> resposta = new ArrayList<TerminiIniciat>();
		if (tasques != null) {
			String[] taskInstanceIds = new String[tasques.size()];
			int i = 0;
			for (TascaLlistatDto tasca: tasques)
				taskInstanceIds[i++] = tasca.getId();
			List<TerminiIniciat> terminis = terminiService.findIniciatsAmbTaskInstanceIds(taskInstanceIds);
			for (TascaLlistatDto tasca: tasques) {
				boolean found = false;
				for (TerminiIniciat termini: terminis) {
					if (termini.getTaskInstanceId().equals(tasca.getId())) {
						resposta.add(termini);
						found = true;
						break;
					}
				}
				if (!found)
					resposta.add(null);
			}
		}
		return resposta;
	}

//	private boolean accioCompletarTasca(
//			HttpServletRequest request,
//			Long entornId,
//			String id,
//			String submit) {
//		TascaDto tasca = tascaService.getById(
//				entornId,
//				id,
//				null,
//				null,
//				false,
//				false);
//		String transicio = null;
//		for (String outcome: tasca.getOutcomes()) {
//			if (outcome != null && outcome.equals(submit)) {
//				transicio = outcome;
//				break;
//			}
//		}
//		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
//		String[] tascaIds;
//		if (massivaActiu)
//			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
//		else
//			tascaIds = new String[]{id};
//		boolean error = false;
//		for (String tascaId: tascaIds) {
//			try {
//				tascaService.completar(entornId, tascaId, true, null, transicio);
//			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
//					missatgeError(
//		        			request,
//		        			getMessage("error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
//				} else {
//					missatgeError(
//		        			request,
//		        			getMessage("error.finalitzar.tasca") + " " + tascaIdLog,
//		        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
//					logger.error("No s'ha pogut finalitzar la tasca " + tascaIdLog, ex);
//				}
//	        	error = true;
//	        }
//		}
//		if (!error) {
//			if (massivaActiu)
//				missatgeInfo(request, getMessage("info.tasca.completades"));
//			else
//				missatgeInfo(request, getMessage("info.tasca.completat"));
//		}
//		return !error;
//	}
	
	private boolean accioCompletarTasca(
			HttpServletRequest request,
			Long entornId,
			String id,
			String submit) {
		TascaDto tasca = tascaService.getById(
				entornId,
				id,
				null,
				null,
				false,
				false);
		String transicio = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(submit)) {
				transicio = outcome;
				break;
			}
		}
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				TascaDto task = tascaService.getByIdSenseComprovacio(tascaIds[0]);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.completar(entornId, id, true, null, transicio);
				
				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					// ------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
					dto.setParam1("Completar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = transicio;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					missatgeInfo(request, getMessage("info.tasca.massiu.completar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.completar(entornId, id, true, null, transicio);
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					missatgeError(
		        			request,
		        			getMessage("error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
				} else {
					missatgeError(
		        			request,
		        			getMessage("error.finalitzar.tasca") + " " + tascaIdLog,
		        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
					logger.error("No s'ha pogut finalitzar la tasca " + tascaIdLog, ex);
				}
	        	return false;
	        }
			missatgeInfo(request, getMessage("info.tasca.completat"));
		}
		return true;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		TascaDto tascaActual = tascaService.getById(
				entornId,
				tascaId,
				null,
				null,
				false,
				false);
		return tascaActual.getNom() + " - " + tascaActual.getExpedient().getIdentificador();
	}

	private static final Log logger = LogFactory.getLog(TascaController.class);

}
