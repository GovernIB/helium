/**
 * 
 */
package net.conselldemallorca.helium.ws.backoffice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;

/**
 * Mètodes base per a les diferents implementacions de backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BaseBackoffice {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private DefinicioProcesService definicioProcesService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private CampService campService;

	@Autowired
	PluginHelper pluginHelper;

	public int processarTramit(TramitDto tramit) throws Exception {
		List<ExpedientTipusDto> candidats = dissenyService.findExpedientTipusAmbSistraTramitCodi(tramit.getIdentificador());
		for (ExpedientTipusDto expedientTipus: candidats) {
			String expedientTitol = null;
			if (expedientTipus.isTeTitol())
				expedientTitol = tramit.getNumero();
			EntornActual.setEntornId(expedientTipus.getEntorn().getId());
			// Cridada sincronitzada al mètode transaccional
			synchronized (this) 
			{
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
						ExpedientDto.IniciadorTipusDto.SISTRA,
						ExpedientDto.crearIniciadorCodiPerSistra(
								tramit.getNumero(),
								new Long(tramit.getClauAcces()).toString()),
						null,
						getDocumentsInicials(expedientTipus, tramit),
						getDocumentsAdjunts(expedientTipus, tramit));
				logger.info("S'ha creat un expedient del tipus " + expedientTipus.getCodi() + ": " + expedientNou.getIdentificador());
			}
		}
		return candidats.size();
	}

	protected abstract DadesVistaDocument getVistaDocumentTramit(
			long referenciaCodi,
			String referenciaClau,
			String plantillaTipus,
			String idioma);


	private Map<String, Object> getDadesInicials(
			ExpedientTipusDto expedientTipus,
			TramitDto tramit) {
		List<String> mapeigsSistra = expedientTipusService.mapeigFindCodiHeliumAmbTipus(
				expedientTipus.getId(), 
				TipusMapeig.Variable);
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, Object> resposta = new HashMap<String, Object>();
		List<CampTascaDto> campsTasca = getCampsStartTask(expedientTipus);
		MapeigSistraDto mapeigSistra;
		for (String mapeig : mapeigsSistra){
			trobat = true;
			CampDto campHelium = null;
			for (CampTascaDto campTasca: campsTasca) {
				if (campTasca.getCamp().getCodi().equalsIgnoreCase(mapeig)) {
					campHelium = campTasca.getCamp();
					break;
				}
			}
			try {
				if (campHelium != null) {
					mapeigSistra = expedientTipusService.mapeigFindAmbCodiHeliumPerValidarRepeticio(
							expedientTipus.getId(),
							mapeig);
					Object valorSistra = valorVariableSistra(
							tramit,
							mapeig,
							mapeigSistra.getCodiSistra());
					Object valorHelium = valorVariableHelium(
							valorSistra,
							campHelium);
					resposta.put(
							mapeig,
							valorHelium);
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

	private Map<String, DadesDocumentDto> getDocumentsInicials(
			ExpedientTipusDto expedientTipus,
			TramitDto tramit) {
		
		List<String> mapeigsSistra = expedientTipusService.mapeigFindCodiHeliumAmbTipus(
				expedientTipus.getId(), 
				TipusMapeig.Document);
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<DocumentDto> documents = getDocuments(expedientTipus);
		MapeigSistraDto mapeigSistra;
		
		for (String mapeig : mapeigsSistra){
			trobat = true;
			DocumentDto docHelium = null;
			for (DocumentDto document : documents){
				if (document.getCodi().equalsIgnoreCase(mapeig)){
					docHelium = document;
					break;
				}
			}
			try {
				if (docHelium != null) {
					mapeigSistra = expedientTipusService.mapeigFindAmbCodiHeliumPerValidarRepeticio(
							expedientTipus.getId(),
							mapeig);
					DadesDocumentDto document = documentSistra(tramit, mapeigSistra.getCodiSistra(), docHelium);
					if (document != null) {
						resposta.put(mapeig, document);
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
		
		List<String> mapeigsSistra = expedientTipusService.mapeigFindCodiHeliumAmbTipus(
				expedientTipus.getId(), 
				TipusMapeig.Adjunt);
		if (mapeigsSistra.size() == 0)
			return null;
				
		boolean trobat = false;
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();

		for (String mapeig : mapeigsSistra){
				try {
					resposta.addAll(documentsSistraAdjunts(tramit, mapeig));
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
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
								String[] valors = new String[columnes.getLength()];
								for (int i = 0; i < columnes.getLength(); i++) {
									Element columna = (Element) columnes.item(i);
									if (columna.hasAttribute("indice"))
										valors[i] = columna.getAttribute("indice");
									else
										valors[i] = columna.getTextContent();
								}
								valorsFiles.add(valors);
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
				if (document.getTelematicEstructurat()) {
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
				if ( document.getTelematicEstructurat()) {
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
	private Object valorVariableHelium(Object val, CampDto camp) throws Exception {
		if (camp.getTipus().equals(CampTipusDto.REGISTRE)) {
			if (val instanceof Object[]) {
				Object[] dadesSistra = (Object[])val;
				List<CampDto> membres = campService.registreFindMembresAmbRegistreId(camp.getId());
				if (camp.isMultiple()) {
					Object[] resposta = new Object[dadesSistra.length];
					for (int i = 0; i < resposta.length; i++) {
						String[] filaSistra = (String[])dadesSistra[i];
						Object[] filaResposta = new Object[membres.size()];
						for (int j = 0; j < filaResposta.length && j < filaSistra.length; j++) {
							CampDto campRegistre = membres.get(j);
							filaResposta[j] = valorPerHeliumSimple(filaSistra[j], campRegistre);
						}
						resposta[i] = filaResposta;
					}
					return resposta;
				} else {
					Object[] resposta = new Object[membres.size()];
					for (int i = 0; i < resposta.length && i < dadesSistra.length; i++) {
						CampDto campRegistre = membres.get(i);
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
	private Object valorPerHeliumSimple(String valor, CampDto camp) {
		try {
			if (camp == null) {
			} else if (camp.getTipus().equals(CampTipusDto.DATE)) {
				return new SimpleDateFormat("dd/MM/yyyy").parse(valor);
			} else if (camp.getTipus().equals(CampTipusDto.BOOLEAN)) {
				return new Boolean(valor);
			} else if (camp.getTipus().equals(CampTipusDto.PRICE)) {
				return new BigDecimal(valor);
			} else if (camp.getTipus().equals(CampTipusDto.INTEGER)) {
				return new Long(valor);
			} else if (camp.getTipus().equals(CampTipusDto.FLOAT)) {
				return new Double(valor);
			}
			return valor;
		} catch (Exception ex) {
			return null;
		}
	}

	private List<CampTascaDto> getCampsStartTask(ExpedientTipusDto expedientTipus) {
		ExpedientTascaDto startTask = expedientService.getStartTask(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getId(),
				null,
				null);
		if (startTask != null)
			return definicioProcesService.tascaCampFindCampAmbTascaId(startTask.getTascaId());
		else
			return new ArrayList<CampTascaDto>();
	}

	private List<DocumentDto> getDocuments(ExpedientTipusDto expedientTipus) {
		List<DocumentDto> ret;
		if (expedientTipus.isAmbInfoPropia())
			ret = documentService.findAll(expedientTipus.getId(), null);
		else {
			DefinicioProcesDto definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId());
			return documentService.findAll(null, definicioProces.getId());
		}
		return ret;		
	}
	private static final Log logger = LogFactory.getLog(BaseBackoffice.class);

}
