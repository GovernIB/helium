/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;



/**
 * Controlador per la gestió dels documents de les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class TascaDocumentsController extends BaseController {

	private TascaService tascaService;
	private DocumentService documentService;
	private ExecucioMassivaService execucioMassivaService;
	private MesuresTemporalsHelper mesuresTemporalsHelper;


	@Autowired
	public TascaDocumentsController(
			TascaService tascaService,
			DocumentService documentService,
			ExecucioMassivaService execucioMassivaService,
			MesuresTemporalsHelper mesuresTemporalsHelper) {
		this.tascaService = tascaService;
		this.documentService = documentService;
		this.execucioMassivaService = execucioMassivaService;
		this.mesuresTemporalsHelper = mesuresTemporalsHelper;
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

	@SuppressWarnings("rawtypes")
	@ModelAttribute("commandReadOnly")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = null;
				try {
					tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						null,
						true,
						false);
				} catch (net.conselldemallorca.helium.core.model.exception.IllegalStateException ex) {
					return null;
				}
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				campsAddicionals.put("id", id);
				campsAddicionals.put("entornId", entorn.getId());
				campsAddicionals.put("procesScope", null);
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionalsClasses.put("id", String.class);
				campsAddicionalsClasses.put("entornId", Long.class);
				campsAddicionalsClasses.put("procesScope", Map.class);
				Object command = TascaFormUtil.getCommandForTasca(
						tasca,
						campsAddicionals,
						campsAddicionalsClasses);
				return command;
			} catch (NotFoundException ignored) {}
		}
		return null;
	}

	@RequestMapping(value = "/tasca/documents")
	public String documents(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				return "redirect:/tasca/personaLlistat.html";
			}
			TascaDto tasca = tascaService.getById(
					entorn.getId(),
					id,
					null,
					null,
					true,
					true);
			if (MesuresTemporalsHelper.isActiu())
				mesuresTemporalsHelper.mesuraIniciar("Tasca DOCUMENTS", "tasques", tasca.getExpedient().getTipus().getNom(), tasca.getNomLimitat());
			model.addAttribute("tasca", tasca);
			for (DocumentTasca document: tasca.getDocuments()) {
				DocumentExpedientCommand command = new DocumentExpedientCommand();
				command.setData(new Date());
				model.addAttribute(
						"documentCommand_" + document.getDocument().getCodi(),
						command);
			}
			if (MesuresTemporalsHelper.isActiu())
				mesuresTemporalsHelper.mesuraCalcular("Tasca DOCUMENTS", "tasques", tasca.getExpedient().getTipus().getNom(), tasca.getNomLimitat());
			return "tasca/documents";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentGuardar")
	public String documentGuardar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("contingut") final MultipartFile multipartFile,
			@ModelAttribute("command") DocumentExpedientCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				return "redirect:/tasca/personaLlistat.html";
			}
			if ("submit".equals(submit) || submit.length() == 0) {
				if (command.getData() != null && command.getContingut() != null) {
					if (multipartFile.getSize() > 0) {
						String nomArxiu = multipartFile.getOriginalFilename();
						int indexPunt = nomArxiu.lastIndexOf(".");
						String extensio = null;
						if (indexPunt != -1 && nomArxiu.length() > indexPunt + 1)
							extensio = nomArxiu.substring(indexPunt + 1);
						if (!documentService.isExtensioDocumentPermesa(
								id,
								codi,
								extensio)) {
							missatgeError(request, getMessage("error.extensio.document"));
						} else {
							accioDocumentGuardar(
									request,
									entorn.getId(),
									id,
									codi,
									nomArxiu,
									command.getData(),
									command.getContingut());
						}
						/*try {
							String nomArxiu = multipartFile.getOriginalFilename();
							tascaService.guardarDocument(
									entorn.getId(),
									id,
									codi,
									nomArxiu,
									command.getData(),
									command.getContingut());
							missatgeInfo(request, getMessage("info.document.guardat") );
				        } catch (Exception ex) {
				        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut guardar el document", ex);
				        }*/
					} else {
						missatgeError(request, getMessage("error.especificar.document") );
					}
				} else {
					missatgeError(request, getMessage("error.especificar.data") );
				}
			}
			return "redirect:/tasca/documents.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentEsborrar")
	public String documentEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				return "redirect:/tasca/personaLlistat.html";
			}
			accioDocumentEsborrar(
					request,
					entorn.getId(),
					id,
					codi);
			/*try {
				tascaService.esborrarDocument(
						entorn.getId(),
						id,
						codi);
				missatgeInfo(request, getMessage("info.document.esborrat") );
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el document", ex);
	        }*/
			return "redirect:/tasca/documents.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	/*@RequestMapping(value = "/tasca/documentDescarregar")
	public String documentDescarregar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentDto document = documentService.documentPerTasca(
					id,
					codi,
					false);
			if (document != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
			}
			return "arxiuView";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}*/

	@RequestMapping(value = "/tasca/documentDescarregarZip")
	public String documentDescarregarZip(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "codi", required = true) String codi) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
			if (massivaActiu) {
				String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipOutputStream out = new ZipOutputStream(baos);
					for (String tascaId: tascaIds) {
						
						DocumentDto document = documentService.documentPerTasca(
								tascaId,
								codi,
								true);
						if (document != null) {
							int indexPunt = document.getArxiuNom().lastIndexOf(".");
							StringBuilder nomEntrada = new StringBuilder();
							nomEntrada.append(document.getArxiuNom().substring(0, indexPunt));
							TascaDto tascaActual = tascaService.getById(
									entorn.getId(),
									tascaId,
									null,
									null,
									true,
									false);
							nomEntrada.append("(" + tascaActual.getExpedient().getIdentificador().replace("/", "|") + ")");
							nomEntrada.append(document.getArxiuNom().substring(indexPunt));
							ZipEntry ze = new ZipEntry(nomEntrada.toString());
							out.putNextEntry(ze);
							out.write(document.getArxiuContingut());
							out.closeEntry();
						}
					}
					out.close();
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, "documents.zip");
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, baos.toByteArray());
					return "arxiuView";
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.generacio.zip"), ex.getLocalizedMessage());
					logger.error("Error al generar el zip amb els arxius", ex);
					return "redirect:/tasca/documents.html?id=" + id;
				}
			} else {
				return "redirect:/tasca/documents.html?id=" + id;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/tasca/documentGenerar")
	public String documentGenerar(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "documentId", required = true) Long documentId,
			@RequestParam(value = "data", required = false) Date data) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			boolean adjuntarAuto = false;
			boolean esPlantilla = false;
			TascaDto tasca;
			try {
				tasca = tascaService.getById(
						entorn.getId(),
						id,
						null,
						null,
						true,
						false);
			} catch (net.conselldemallorca.helium.core.model.exception.IllegalStateException ex) {
				missatgeError(request, getMessage("error.tasca.no.disponible") );
				logger.error(getMessage("error.tascaService.noDisponible"), ex);
				return "redirect:/index.html";
			}
			for (DocumentTasca document: tasca.getDocuments()) {
				if (document.getDocument().getId().longValue() == documentId.longValue()) {
					adjuntarAuto = document.getDocument().isAdjuntarAuto();
					esPlantilla = document.getDocument().isPlantilla();
					break;
				}
			}
			if (esPlantilla && adjuntarAuto) {
				accioDocumentGenerar(
						request,
						entorn.getId(),
						id,
						documentId,
						data);
				return "redirect:/tasca/documents.html?id=" + id;
			} else {
				DocumentDto document = documentService.generarDocumentPlantilla(
						entorn.getId(),
						documentId,
						id,
						null,
						(data != null) ? data : new Date(),
						false);
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, document.getArxiuNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, document.getArxiuContingut());
				return "arxiuView";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
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
	}


	private boolean accioDocumentGuardar(
			HttpServletRequest request,
			Long entornId,
			String id,
			String codi,
			String nomArxiu,
			Date data,
			byte[] contingut) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.comprovarTascaAssignadaIValidada(entornId, id, null);
				documentService.guardarDocumentTasca(
						entornId,
						id,
						codi,
						data,
						nomArxiu,
						contingut);
				
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
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(bCorreu);
				dto.setTascaIds(tIds);
				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[7];
				params[0] = entornId;
				params[1] = codi;
				params[2] = data;
				params[3] = contingut;
				params[4] = nomArxiu;
				params[5] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[6] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				missatgeInfo(request, getMessage("info.tasca.massiu.document.guardar", new Object[] {tIds.length}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.comprovarTascaAssignadaIValidada(entornId, id, null);
				documentService.guardarDocumentTasca(
						entornId,
						id,
						codi,
						data,
						nomArxiu,
						contingut);
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.guardar.document") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el document '" + codi + "' a la tasca " + tascaIdLog, ex);
	        	return false;
	        }
			missatgeInfo(request, getMessage("info.document.guardat"));
		}
		return true;
	}
	private boolean accioDocumentEsborrar(
			HttpServletRequest request,
			Long entornId,
			String id,
			String codi) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.comprovarTascaAssignadaIValidada(entornId, id, null);
				documentService.esborrarDocument(
						id,
						null,
						codi);
				
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
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(bCorreu);
				dto.setTascaIds(tIds);
				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
				dto.setParam1("DocEsborrar");
				Object[] params = new Object[4];
				params[0] = entornId;
				params[1] = codi;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				missatgeInfo(request, getMessage("info.tasca.massiu.document.esborrar", new Object[] {tIds.length}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.comprovarTascaAssignadaIValidada(entornId, id, null);
				documentService.esborrarDocument(
						id,
						null,
						codi);
	        } catch (Exception ex) {
	        	String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.esborrar.document") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el document '" + codi + "' a la tasca " + tascaIdLog, ex);
	        	return false;
	        }
			missatgeInfo(request, getMessage("info.document.esborrat"));
		}
		return true;
	}
	private boolean accioDocumentGenerar(
			HttpServletRequest request,
			Long entornId,
			String id,
			Long documentId,
			Date data) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				TascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = task.getExpedient().getTipus().getId();
				if (data == null) data = new Date();
				
				// Restauram la primera tasca
				// ------------------------------------------
				documentService.generarDocumentPlantilla(
						entornId,
						documentId,
						id,
						null,
						data,
						false);
				
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
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(bCorreu);
				dto.setTascaIds(tIds);
				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipus.EXECUTAR_TASCA);
				dto.setParam1("DocGenerar");
				Object[] params = new Object[5];
				params[0] = entornId;
				params[1] = documentId;
				params[2] = data;
				params[3] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[4] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				missatgeInfo(request, getMessage("info.tasca.massiu.document.generar", new Object[] {tIds.length}));
			} catch (Exception e) {
				missatgeError(request, getMessage("error.no.massiu"));
				return false;
			}
		} else {
			try {
				documentService.generarDocumentPlantilla(
						entornId,
						documentId,
						id,
						null,
						(data != null) ? data : new Date(),
						false);
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				missatgeError(
		    			request,
		    			getMessage("error.generar.document") + " " + tascaIdLog,
		    			ex.getLocalizedMessage());
	        	logger.error("Error al generar el document (documentId=" + documentId + ") a la tasca " + tascaIdLog, ex);
	        	return false;
			}
			missatgeInfo(request, getMessage("info.document.adjuntat"));
		}
		return true;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		TascaDto tascaActual = tascaService.getById(
				entornId,
				tascaId,
				null,
				null,
				true,
				false);
		return tascaActual.getNom() + " - " + tascaActual.getExpedient().getIdentificador();
	}



	private static final Log logger = LogFactory.getLog(TascaDocumentsController.class);

}
