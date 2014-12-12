package net.conselldemallorca.helium.wsintegraciones.backoffice;

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

import net.conselldemallorca.helium.core.model.dto.DadesDocumentDto;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTelematic;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Mètodes base per a les diferents implementacions de backoffices
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BaseBackoffice {

	private ExpedientService expedientService;
	private DissenyService dissenyService;



	public int processarTramit(DadesTramit tramit) throws Exception {
		List<ExpedientTipus> candidats = dissenyService.findExpedientTipusAmbSistraTramitCodi(tramit.getIdentificador());
		for (ExpedientTipus expedientTipus: candidats) {
			String expedientTitol = null;
			if (expedientTipus.getTeTitol())
				expedientTitol = tramit.getNumero();
			/*String expedientNumero = null;
			if (expedientTipus.getTeNumero())
				expedientNumero = expedientService.getNumeroExpedientActual(
						expedientTipus.getEntorn().getId(),
						expedientTipus.getId());*/
			EntornActual.setEntornId(expedientTipus.getEntorn().getId());
			ExpedientDto expedientNou = expedientService.iniciar(
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
					IniciadorTipus.SISTRA,
					Expedient.crearIniciadorCodiPerSistra(
							tramit.getNumero(),
							new Long(tramit.getClauAcces()).toString()),
					null,
					getDocumentsInicials(expedientTipus, tramit),
					getDocumentsAdjunts(expedientTipus, tramit));
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
			ExpedientTipus expedientTipus,
			DadesTramit tramit) {
		/*if (expedientTipus.getSistraTramitMapeigCamps() == null)
			return null; 
		Map<String, Object> resposta = new HashMap<String, Object>();
		List<CampTasca> campsTasca = getCampsStartTask(expedientTipus);
		String[] parts = expedientTipus.getSistraTramitMapeigCamps().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				Camp campHelium = null;
				for (CampTasca campTasca: campsTasca) {
					if (campTasca.getCamp().getCodi().equalsIgnoreCase(varHelium)) {
						campHelium = campTasca.getCamp();
						break;
					}
				}
				try {
					if (campHelium != null) {
						resposta.put(
								varHelium,
								valorVariableHelium(
										valorVariableSistra(tramit, varSistra),
										campHelium));
					}
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		return resposta;*/
		List<MapeigSistra> mapeigsSistra = dissenyService.findMapeigSistraVariablesAmbExpedientTipus(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, Object> resposta = new HashMap<String, Object>();
		List<CampTasca> campsTasca = getCampsStartTask(expedientTipus);
		
		for (MapeigSistra mapeig : mapeigsSistra){
			trobat = true;
			Camp campHelium = null;
			for (CampTasca campTasca: campsTasca) {
				if (campTasca.getCamp().getCodi().equalsIgnoreCase(mapeig.getCodiHelium())) {
					campHelium = campTasca.getCamp();
					break;
				}
			}
			try {
				if (campHelium != null) {
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
			ExpedientTipus expedientTipus,
			DadesTramit tramit) {
		/*if (expedientTipus.getSistraTramitMapeigDocuments() == null)
			return null;
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<Document> documents = getDocuments(expedientTipus);
		String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				Document docHelium = null;
				for (Document document: documents) {
					if (document.getCodi().equalsIgnoreCase(varHelium)) {
						docHelium = document;
						break;
					}
				}
				try {
					if (docHelium != null)
						resposta.put(
								varHelium,
								documentSistra(tramit, varSistra, docHelium));
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		return resposta;*/
		
		List<MapeigSistra> mapeigsSistra = dissenyService.findMapeigSistraDocumentsAmbExpedientTipus(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<Document> documents = getDocuments(expedientTipus);
		
		for (MapeigSistra mapeig : mapeigsSistra){
			trobat = true;
			Document docHelium = null;
			for (Document document : documents){
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
			ExpedientTipus expedientTipus,
			DadesTramit tramit) {
		/*if (expedientTipus.getSistraTramitMapeigAdjunts() == null)
			return null;
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();
		String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
		for (int i = 0; i < parts.length; i++) {
			String varSistra = parts[i];
			try {
				resposta.addAll(documentsSistraAdjunts(tramit, varSistra));
			} catch (Exception ex) {
				logger.error("Error llegint dades del document de SISTRA", ex);
			}
		}
		return resposta;*/
		
		List<MapeigSistra> mapeigsSistra = dissenyService.findMapeigSistraAdjuntsAmbExpedientTipus(expedientTipus.getId());
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();

		for (MapeigSistra mapeig : mapeigsSistra){
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
			DadesTramit tramit,
			String varHelium,
			String varSistra) throws Exception {
		String[] parts = varSistra.split("\\.");
		String documentCodi = parts[0];
		int instancia = new Integer(parts[1]).intValue();
		String finestraCodi = parts[2];
		String campCodi = parts[3];
		for (DocumentTramit doc: tramit.getDocuments()) {
			if (documentCodi.equalsIgnoreCase(doc.getIdentificador()) && doc.getInstanciaNumero() == instancia) {
				org.w3c.dom.Document document = xmlToDocument(new ByteArrayInputStream(doc.getDocumentTelematic().getArxiuContingut()));
				
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
						return node.getNodeValue();
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
										valors[i] = columna.getNodeValue();
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
			DadesTramit tramit,
			String varSistra,
			Document varHelium) throws Exception {
		DadesDocumentDto resposta = null;
		for (DocumentTramit document: tramit.getDocuments()) {
			if (varSistra.equalsIgnoreCase(document.getIdentificador()) && document.getDocumentTelematic() != null) {
				resposta = new DadesDocumentDto();
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
				resposta.setData(tramit.getData());
				DocumentTelematic documentTelematic = document.getDocumentTelematic();
				if (documentTelematic.getEstructurat() != null && documentTelematic.getEstructurat().booleanValue()) {
					resposta.setArxiuNom(documentTelematic.getArxiuNom());
					resposta.setArxiuContingut(documentTelematic.getArxiuContingut());
				} else {
					DadesVistaDocument vista = getVistaDocumentTramit(
							documentTelematic.getReferenciaCodi(),
							documentTelematic.getReferenciaClau(),
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
			DadesTramit tramit,
			String varSistra) throws Exception {
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();
		for (DocumentTramit document: tramit.getDocuments()) {
			if (document.getIdentificador().equalsIgnoreCase(varSistra) && document.getDocumentTelematic() != null) {
				DadesDocumentDto docResposta = new DadesDocumentDto();
				docResposta.setTitol(document.getNom());
				docResposta.setData(tramit.getData());
				DocumentTelematic documentTelematic = document.getDocumentTelematic();
				if (documentTelematic.getEstructurat() != null && documentTelematic.getEstructurat().booleanValue()) {
					docResposta.setArxiuNom(documentTelematic.getArxiuNom());
					docResposta.setArxiuContingut(documentTelematic.getArxiuContingut());
				} else {
					DadesVistaDocument vista = getVistaDocumentTramit(
							documentTelematic.getReferenciaCodi(),
							documentTelematic.getReferenciaClau(),
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
				return new BigDecimal(valor);
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				return new Long(valor);
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				return new Double(valor);
			}
			return valor;
		} catch (Exception ex) {
			return null;
		}
	}

	private List<CampTasca> getCampsStartTask(ExpedientTipus expedientTipus) {
		TascaDto startTask = expedientService.getStartTask(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getId(),
				null,
				null);
		if (startTask != null)
			return startTask.getCamps();
		return new ArrayList<CampTasca>();
	}

	private List<Document> getDocuments(ExpedientTipus expedientTipus) {
		DefinicioProcesDto definicioProces = dissenyService.findDarreraAmbExpedientTipus(expedientTipus.getId());
		return dissenyService.findDocumentsAmbDefinicioProces(definicioProces.getId());
	}
	private static final Log logger = LogFactory.getLog(BaseBackoffice.class);

}
