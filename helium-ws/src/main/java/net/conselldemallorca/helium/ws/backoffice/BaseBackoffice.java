/**
 * 
 */
package net.conselldemallorca.helium.ws.backoffice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.intf.dto.DadesDocumentDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.MapeigSistraDto;
import es.caib.helium.logic.intf.dto.TramitDocumentDto;
import es.caib.helium.logic.intf.dto.TramitDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.util.EntornActual;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.CampTasca;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.MapeigSistra;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.repository.CampTascaRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;

/**
 * Mètodes base per a les diferents implementacions de backoffices
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BaseBackoffice {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Resource
	private DocumentRepository documentRepository;
	
	@Resource
	private CampTascaRepository campTascaRepository;

	private DissenyService dissenyService;

	@Autowired
	protected PluginHelper pluginHelper;

	/** Comprova ja existeix l'expedient a partir del tràmit. Consulta per codi
	 * @param numero 
	 * 
	 * @return
	 */
	public boolean existeixExpedient(String numeroEntrada, String clauAcces) {
		boolean existeix = false;
		List<ExpedientDto> expedients = expedientService.findAmbIniciadorCodi(Expedient.crearIniciadorCodiPerSistra(numeroEntrada, clauAcces));
		existeix = expedients.size() > 0;
		return existeix;
	}

	public int processarTramit(TramitDto tramit) throws Exception {
		List<ExpedientTipusDto> candidats = expedientTipusService.findAmbSistraTramitCodi(tramit.getIdentificador());
		for (ExpedientTipusDto expedientTipus: candidats) {
			String expedientTitol = null;
			if (expedientTipus.isTeTitol())
				expedientTitol = tramit.getNumero();

			EntornActual.setEntornId(expedientTipus.getEntorn().getId());

			// Crida al mètode de creació de l'expedient
			ExpedientDto expedientNou = expedientService.create(
					expedientTipus.getEntorn().getId(),
					null,
					expedientTipus.getId(),
					null,
					null,
					null, // expedientNumero
					expedientTitol,
					tramit.getRegistreNumero(),
					tramit.getRegistreData(),
					new Long(tramit.getUnitatAdministrativa()),
					tramit.getIdioma(),
					!AutenticacioTipus.ANONIMA.equals(tramit.getAutenticacioTipus()),
					tramit.getTramitadorNif(),
					tramit.getTramitadorNom(),
					tramit.getInteressatNif(),
					tramit.getInteressatNom(),
					tramit.getRepresentantNif(),
					tramit.getRepresentantNom(),
					tramit.isAvisosHabilitats(),
					tramit.getAvisosEmail(),
					tramit.getAvisosSms(),
					tramit.isNotificacioTelematicaHabilitada(),
					getDadesInicials(expedientTipus, tramit),
					null,
					IniciadorTipusDto.SISTRA, //IniciadorTipus.SISTRA,
					Expedient.crearIniciadorCodiPerSistra(
							tramit.getNumero(),
							new Long(tramit.getClauAcces()).toString()),
					null,
					getDocumentsInicials(expedientTipus, tramit),
					getDocumentsAdjunts(expedientTipus, tramit),
					null, // tramitId,
					false);
			logger.info("S'ha creat un expedient del tipus " + expedientTipus.getCodi() + ": " + expedientNou.getIdentificador());
		}
		return candidats.size();
	}



	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}



	protected abstract DadesVistaDocument getVistaDocumentTramit(
			long referenciaCodi,
			String referenciaClau,
			String plantillaTipus,
			String idioma);


	private Map<String, Object> getDadesInicials(
			ExpedientTipusDto expedientTipus,
			TramitDto tramit) {
		
		List<MapeigSistraDto> mapeigsSistra = expedientTipusService.mapeigFindAll(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, Object> resposta = new HashMap<String, Object>();
		List<CampTasca> campsTasca = getCampsStartTask(expedientTipus);
		for (MapeigSistraDto mapeig: mapeigsSistra){
			trobat = true;
			Camp campHelium = null;
			for (CampTasca campTasca: campsTasca) {
				if (campTasca.getCamp().getCodi().equalsIgnoreCase(mapeig.getCodiHelium())) {
					campHelium = campTasca.getCamp();
					break;
				}
			}
			if (campHelium != null) {
				StringBuilder logMsg = new StringBuilder();
				try {
					logMsg.append("Mapeig variable SISTRA-HELIUM (" +
								"codiSistra=" + mapeig.getCodiSistra() + ", " +
								"codiHelium=" + mapeig.getCodiHelium() + ", " +
								"camp.codi=" + campHelium.getCodi() + ", " +
								"camp.etiqueta=" + campHelium.getEtiqueta() + ", " +
								"camp.tipus=" + campHelium.getTipus() + ", " );
					
					if (!expedientTipus.isAmbInfoPropia()) {		
						if (campHelium.getDefinicioProces() != null) {							
							logMsg.append("camp.dp.jbpmId=" + campHelium.getDefinicioProces().getJbpmId() + ", " +
									"camp.dp.jbpmKey=" + campHelium.getDefinicioProces().getJbpmKey() + ", " +
									"camp.dp.versio=" + campHelium.getDefinicioProces().getVersio() + ", ");
							if (campHelium.getDefinicioProces().getExpedientTipus() != null) {
								logMsg.append("camp.dp.et.codi=" + campHelium.getDefinicioProces().getExpedientTipus().getCodi() + ", " +
											  "camp.dp.et.nom=" + campHelium.getDefinicioProces().getExpedientTipus().getNom() + ")");
							}
						}
					}
					else  if (campHelium.getExpedientTipus() != null) {
						logMsg.append(
						"camp.et.codi=" + campHelium.getExpedientTipus().getCodi() + ", " +
						"camp.et.nom=" + campHelium.getExpedientTipus().getNom() + ")");
					}
								
					logger.debug(logMsg.toString());
					Object valorSistra = valorVariableSistra(
							tramit,
							mapeig.getCodiHelium(),
							mapeig.getCodiSistra());
					Object valorHelium = valorVariableHelium(
							valorSistra,
							campHelium);
					resposta.put(
							mapeig.getCodiHelium(),
							valorHelium);
				} catch (Exception ex) {
					logger.error("Error en el mapeig de camp. " + logMsg.toString() + ":" + ex.getMessage());
				}
			}
		}
		
		if (trobat)
			return resposta;
		else
			return null;
		
	}

	private Map<String, DadesDocumentDto> getDocumentsInicials(
			ExpedientTipusDto expedientTipus,
			TramitDto tramit) {

		List<MapeigSistraDto> mapeigsSistra = expedientTipusService.mapeigFindAll(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<DocumentDto> documents = getDocuments(expedientTipus);
		
		for (MapeigSistraDto mapeig : mapeigsSistra){
			trobat = true;
			DocumentDto docHelium = null;
			for (DocumentDto document : documents){
				if (document.getCodi().equalsIgnoreCase(mapeig.getCodiHelium())){
					docHelium = document;
					break;
				}
			}
			try {
				if (docHelium != null) {
					DadesDocumentDto document = documentSistra(tramit, mapeig.getCodiSistra(), docHelium);
					if (document != null) {
						resposta.put(mapeig.getCodiHelium(), document);
					}
				}
			} catch (Exception ex) {
				logger.error("Error llegint dades del document de SISTRA", ex);
			}
		}
		
		if (trobat)
			return resposta;
		else
			return null;
		
	}


	private List<DadesDocumentDto> getDocumentsAdjunts(
			ExpedientTipusDto expedientTipus,
			TramitDto tramit) {
		
		List<MapeigSistraDto> mapeigsSistra = expedientTipusService.mapeigFindAll(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();

		for (MapeigSistraDto mapeig : mapeigsSistra){
			if (MapeigSistra.TipusMapeig.Adjunt.equals(mapeig.getTipus())){
				trobat = true;
				try {
					resposta.addAll(documentsSistraAdjunts(tramit, mapeig.getCodiHelium()));
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		
		if (trobat)
			return resposta;
		else
			return null;
	}

	private Object valorVariableSistra(
			TramitDto tramit,
			String varHelium,
			String varSistra) throws Exception {
		String[] parts = varSistra.split("\\.");
		String documentCodi = parts[0];
		int instancia = new Integer(parts[1]).intValue();
		String finestraCodi = parts[2];
		String campCodi = parts[3];
		for (TramitDocumentDto doc: tramit.getDocuments()) {
			if (documentCodi.equalsIgnoreCase(doc.getIdentificador()) && doc.getInstanciaNumero() == instancia) {
				org.w3c.dom.Document document = xmlToDocument(new ByteArrayInputStream(doc.getTelematicArxiuContingut()));
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				Element node= (Element) xpath.evaluate(
						"/FORMULARIO/" + finestraCodi + "/" + campCodi,
						document, 
						XPathConstants.NODE);
				if (node != null) {	 
				    if (node.hasAttribute("indice")) {
						return node.getAttribute("indice");
					} else if (node.getChildNodes().getLength() == 1) {
						return node.getTextContent();
					} else {
						List<String[]> valorsFiles = new ArrayList<String[]>();
						NodeList nodes = node.getChildNodes();
						for (int index = 0; index < nodes.getLength(); index++) {
							Node fila = nodes.item(index);
							if (fila.getNodeName().startsWith("ID")) {
								NodeList columnes = fila.getChildNodes();
								List<String> valors = new ArrayList<String>();
								for (int i = 0; i < columnes.getLength(); i++) {
									if (columnes.item(i).getNodeType() == Node.ELEMENT_NODE) {
										Element columna = (Element)columnes.item(i);
										if (columna.hasAttribute("indice")) {
											valors.add(columna.getAttribute("indice"));
										} else {
											valors.add(columna.getTextContent());
										}
									}
								}
								valorsFiles.add(valors.toArray(new String[valorsFiles.size()]));
							}
						}
						return valorsFiles.toArray(new Object[valorsFiles.size()]);
					}
				}
			}
		}
		return null;
	}

	private org.w3c.dom.Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		org.w3c.dom.Document doc = builder.parse(is);
		is.close();
		return doc;
	}
	
	private DadesDocumentDto documentSistra(
			TramitDto tramit,
			String varSistra,
			DocumentDto varHelium) throws Exception {
		DadesDocumentDto resposta = null;
		for (TramitDocumentDto document: tramit.getDocuments()) {
			if (varSistra.equalsIgnoreCase(document.getIdentificador()) && document.isTelematic()) {
				resposta = new DadesDocumentDto();
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
				resposta.setData(tramit.getData());
				if (document.getTelematicEstructurat() != null ? document.getTelematicEstructurat().booleanValue() : false) {
					resposta.setArxiuNom(document.getTelematicArxiuNom());
					resposta.setArxiuContingut(document.getTelematicArxiuContingut());
				} else {
					DadesVistaDocument vista = getVistaDocumentTramit(
							document.getTelematicReferenciaCodi(),
							document.getTelematicReferenciaClau(),
							null,
							null);
					resposta.setArxiuNom(vista.getArxiuNom());
					resposta.setArxiuContingut(vista.getArxiuContingut());
				}
				break;
			}
		}
		return resposta;
	}
	private List<DadesDocumentDto> documentsSistraAdjunts(
			TramitDto tramit,
			String varSistra) throws Exception {
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();
		for (TramitDocumentDto document: tramit.getDocuments()) {
			if (document.getIdentificador().equalsIgnoreCase(varSistra) && document.isTelematic()) {
				DadesDocumentDto docResposta = new DadesDocumentDto();
				docResposta.setTitol(document.getNom());
				docResposta.setData(tramit.getData());
				if (document.getTelematicEstructurat() != null ? document.getTelematicEstructurat().booleanValue() : false ) {
					docResposta.setArxiuNom(document.getTelematicArxiuNom());
					docResposta.setArxiuContingut(document.getTelematicArxiuContingut());
				} else {
					DadesVistaDocument vista = getVistaDocumentTramit(
							document.getTelematicReferenciaCodi(),
							document.getTelematicReferenciaClau(),
							null,
							null);
					docResposta.setArxiuNom(vista.getArxiuNom());
					docResposta.setArxiuContingut(vista.getArxiuContingut());
				}
				resposta.add(docResposta);
			}
		}
		return resposta;
	}
	private Object valorVariableHelium(Object val, Camp camp) throws Exception {
		if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
			if (val instanceof Object[]) {
				Object[] dadesSistra = (Object[])val;
				if (camp.isMultiple()) {
					Object[] resposta = new Object[dadesSistra.length];
					for (int i = 0; i < resposta.length; i++) {
						String[] filaSistra = (String[])dadesSistra[i];
						Object[] filaResposta = new Object[camp.getRegistreMembres().size()];
						for (int j = 0; j < filaResposta.length && j < filaSistra.length; j++) {
							Camp campRegistre = camp.getRegistreMembres().get(j).getMembre();
							filaResposta[j] = valorPerHeliumSimple(filaSistra[j], campRegistre);
						}
						resposta[i] = filaResposta;
					}
					return resposta;
				} else {
					Object[] resposta = new Object[camp.getRegistreMembres().size()];
					for (int i = 0; i < resposta.length && i < dadesSistra.length; i++) {
						Camp campRegistre = camp.getRegistreMembres().get(i).getMembre();
						resposta[i] = valorPerHeliumSimple(((String[])dadesSistra)[i], campRegistre);
					}
					return resposta;
				}
			} else {
				logger.error("No s'ha pogut mapejar el camp " + camp.getCodi() + ": el camp és buit o no és de tipus registre");
				return null;
			}
		} else {
			if (camp.isMultiple()) {
				Object[] dadesSistra = (Object[])val;
				Object[] resposta = new Object[dadesSistra.length];
				for (int i = 0; i < resposta.length; i++) {
					String[] filaSistra = (String[])dadesSistra[i];
					if (filaSistra.length == 1) {
						resposta[i] = valorPerHeliumSimple(filaSistra[1], camp);
					} else {
						logger.error("No s'ha pogut mapejar el camp " + camp.getCodi() + ": el nombre de columnes és major que 1");
						return null;
					}
				}
				return resposta;
			} else {
				return valorPerHeliumSimple((String)val, camp);
			}
		}
	}
	private Object valorPerHeliumSimple(String valor, Camp camp) {
		try {
			if (camp == null) {
			} else if (camp.getTipus().equals(TipusCamp.DATE)) {
				return new SimpleDateFormat("dd/MM/yyyy").parse(valor);
			} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
				return new Boolean(valor);
			} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
				Object preu = null;
				try {
					preu = new BigDecimal(valor);
				} catch(Exception e) {
					// No compleix amb el format "0.##", es prova amb el format #,##0.###
					DecimalFormat df = new DecimalFormat("#,##0.###");
					preu = new BigDecimal(df.parse(valor).doubleValue());
				}
				return preu;
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				return new Long(valor);
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				return new Double(valor);
			}
			return valor;
		} catch (Exception ex) {
			if (camp != null) {
				logger.error("Error en el mapeig de camp [codi=" + camp.getCodi() + 
						", etiqueta=" + camp.getEtiqueta() + ", tipus=" + camp.getTipus() +
						" pel valor " + valor + ":" + ex.getMessage());
			}
			return null;
		}
	}

	private List<CampTasca> getCampsStartTask(ExpedientTipusDto expedientTipus) {
		ExpedientTascaDto startTask = expedientService.getStartTask(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getId(),
				null,
				null);
		if (startTask != null) {
			return campTascaRepository.findAmbTascaIdOrdenats(startTask.getTascaId(), expedientTipus.getId());
		}
		return new ArrayList<CampTasca>();
	}

	private List<DocumentDto> getDocuments(ExpedientTipusDto expedientTipus) {
		DefinicioProcesDto definicioProces =
				expedientTipus.isAmbInfoPropia() ?
					null
					: dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId());
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedientTipus.getId(), 
				definicioProces != null ? definicioProces.getId() : null,
						true);
		return documents;
	}
	private static final Log logger = LogFactory.getLog(BaseBackoffice.class);

}
