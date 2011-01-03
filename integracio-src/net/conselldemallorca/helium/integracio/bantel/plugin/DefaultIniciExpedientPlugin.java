/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.DatosDocumentoTelematico;
import net.conselldemallorca.helium.integracio.bantel.client.wsdl.DocumentoBTE;
import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;
import net.conselldemallorca.helium.integracio.redose.client.wsdl.BackofficeFacade;
import net.conselldemallorca.helium.integracio.redose.client.wsdl.DocumentoRDS;
import net.conselldemallorca.helium.integracio.redose.client.wsdl.ReferenciaRDS;
import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interfície per a iniciar un expedient donat un tràmit de SISTRA.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DefaultIniciExpedientPlugin implements IniciExpedientPlugin {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private BackofficeFacade sistraRedoseClient;



	public List<DadesIniciExpedient> obtenirDadesInici(TramiteBTE tramit) {
		List<ExpedientTipus> candidats = dissenyService.findExpedientTipusAmbSistraTramitCodi(tramit.getIdentificadorTramite());
		if (candidats.size() > 0) {
			List<DadesIniciExpedient> resposta = new ArrayList<DadesIniciExpedient>();
			for (ExpedientTipus expedientTipus: candidats) {
				DadesIniciExpedient dadesIniciExpedient = new DadesIniciExpedient();
				dadesIniciExpedient.setEntornCodi(expedientTipus.getEntorn().getCodi());
				dadesIniciExpedient.setTipusCodi(expedientTipus.getCodi());
				if (expedientTipus.getTeTitol())
					dadesIniciExpedient.setTitol(tramit.getNumeroEntrada());
				if (expedientTipus.getTeNumero())
					dadesIniciExpedient.setNumero(expedientTipus.getNumeroExpedientActual());
				dadesIniciExpedient.setRegistreNumero(tramit.getNumeroRegistro());
				dadesIniciExpedient.setRegistreData(getDataRegistre(tramit));
				if (tramit.getHabilitarAvisos() != null)
					dadesIniciExpedient.setAvisosHabilitats("S".equalsIgnoreCase(tramit.getHabilitarAvisos().getValue()));
				else
					dadesIniciExpedient.setAvisosHabilitats(false);
				if (tramit.getAvisoEmail() != null)
					dadesIniciExpedient.setAvisosEmail(tramit.getAvisoEmail().getValue());
				else
					dadesIniciExpedient.setAvisosEmail(null);
				if (tramit.getAvisoSMS() != null)
					dadesIniciExpedient.setAvisosMobil(tramit.getAvisoSMS().getValue());
				else
					dadesIniciExpedient.setAvisosMobil(null);
				if (tramit.getHabilitarNotificacionTelematica() != null)
					dadesIniciExpedient.setNotificacioTelematicaHabilitada("S".equalsIgnoreCase(tramit.getHabilitarNotificacionTelematica().getValue()));
				else
					dadesIniciExpedient.setNotificacioTelematicaHabilitada(false);
				dadesIniciExpedient.setDadesInicials(getDadesInicials(expedientTipus, tramit));
				dadesIniciExpedient.setDocumentsInicials(getDocumentsInicials(expedientTipus, tramit));
				dadesIniciExpedient.setDocumentsAdjunts(getDocumentsAdjunts(expedientTipus, tramit));
				resposta.add(dadesIniciExpedient);
			}
			return resposta;
		} else {
			throw new RuntimeException("No s'han configurat els paràmetres necessàris per a l'inici d'un tràmit del tipus " + tramit.getIdentificadorTramite());
		}
	}

	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setSistraRedoseClient(BackofficeFacade sistraRedoseClient) {
		this.sistraRedoseClient = sistraRedoseClient;
	}



	private Map<String, Object> getDadesInicials(ExpedientTipus expedientTipus, TramiteBTE tramit) {
		if (expedientTipus.getSistraTramitMapeigCamps() == null)
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
						/*if (!campHelium.isMultiple()) {
							String valorSistra = valorVariableSistra(tramit, varSistra);
							Object valorHelium = valorVariableHelium(valorSistra, campHelium, false);
							resposta.put(varHelium, valorHelium);
						} else {
							String[] valorSistra = valorVariableSistraMultiple(tramit, varSistra);
							Object valorHelium = valorVariableHelium(valorSistra, campHelium, true);
							resposta.put(varHelium, valorHelium);
						}*/
					}
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		return resposta;
	}

	private Map<String, DadesDocument> getDocumentsInicials(ExpedientTipus expedientTipus, TramiteBTE tramit) {
		if (expedientTipus.getSistraTramitMapeigDocuments() == null)
			return null;
		Map<String, DadesDocument> resposta = new HashMap<String, DadesDocument>();
		List<net.conselldemallorca.helium.model.hibernate.Document> documents = getDocuments(expedientTipus);
		String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				net.conselldemallorca.helium.model.hibernate.Document docHelium = null;
				for (net.conselldemallorca.helium.model.hibernate.Document document: documents) {
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
		return resposta;
	}

	private List<DadesDocument> getDocumentsAdjunts(ExpedientTipus expedientTipus, TramiteBTE tramit) {
		if (expedientTipus.getSistraTramitMapeigAdjunts() == null)
			return null;
		List<DadesDocument> resposta = new ArrayList<DadesDocument>();
		String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
		for (int i = 0; i < parts.length; i++) {
			String varSistra = parts[i];
			try {
				resposta.addAll(documentsSistraAdjunts(tramit, varSistra));
			} catch (Exception ex) {
				logger.error("Error llegint dades del document de SISTRA", ex);
			}
		}
		return resposta;
	}

	@SuppressWarnings("unchecked")
	private Object valorVariableSistra(TramiteBTE tramit, String varSistra) throws Exception {
		String[] parts = varSistra.split("\\.");
		String documentCodi = parts[0];
		int instancia = new Integer(parts[1]).intValue();
		String finestraCodi = parts[2];
		String campCodi = parts[3];
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			if (	doc.getPresentacionTelematica().getValue().getIdentificador().equalsIgnoreCase(documentCodi)
				 && doc.getPresentacionTelematica().getValue().getNumeroInstancia() == instancia) {
				String content = new String(doc.getPresentacionTelematica().getValue().getContent());
				Document document = DocumentHelper.parseText(content);
				String xpath = "/FORMULARIO/" + finestraCodi + "/" + campCodi;
				Element node = (Element)document.selectSingleNode(xpath);
				if (node.attribute("indice")!= null) {
					return node.attribute("indice").getValue();
				} else if (node.isTextOnly()) {
					return node.getText();
				} else {
					List<String[]> valorsFiles = new ArrayList<String[]>();
					List<Element> files = node.elements();
					for (Element fila: files) {
						if (fila.getName().startsWith("ID")) {
							List<Element> columnes = fila.elements();
							String[] valors = new String[columnes.size()];
							for (int i = 0; i < columnes.size(); i++) {
								Element columna = columnes.get(i);
								if (columna.attribute("indice")!= null)
									valors[i] = columna.attribute("indice").getValue();
								else
									valors[i] = columna.getText();
							}
							valorsFiles.add(valors);
						}
					}
					return valorsFiles.toArray(new Object[valorsFiles.size()]);
				}
			}
		}
		return null;
	}
	/*private String[] valorVariableSistraMultiple(TramiteBTE tramit, String varSistra) throws Exception {
		String[] parts = varSistra.split("\\.");
		String documentCodi = parts[0];
		int instancia = new Integer(parts[1]).intValue();
		String finestraCodi = parts[2];
		String campCodi = parts[3];
		
		List<String> resposta = new ArrayList<String>();
		
		
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			if (
					doc.getPresentacionTelematica().getValue().getIdentificador().equalsIgnoreCase(documentCodi)
				 && doc.getPresentacionTelematica().getValue().getNumeroInstancia() == instancia) {
				
				String rutaSistra = "/FORMULARIO/" + finestraCodi + "/" + campCodi;
				String[] trossos = rutaSistra.split("//");
				
				String contentNodes = new String(doc.getPresentacionTelematica().getValue().getContent());
				Document documentNodes = DocumentHelper.parseText(contentNodes);
				Element nodePare = (Element)documentNodes.selectSingleNode(trossos[0]);
				
				if(trossos.length > 1){ //Cas del component del Sistra per elements múltiples
					int i = 1;
					
					for(Object n:nodePare.elements()){
						if(n instanceof DefaultElement){
							String novaRuta = trossos[0] + "/ID" + i+ "/" + trossos[1];
							String content = new String(doc.getPresentacionTelematica().getValue().getContent());
							Document document = DocumentHelper.parseText(content);
							List<Element> llistatNodes = document.selectNodes(novaRuta);
							
							for(Element node: llistatNodes){
								if(node.attributeCount() > 0){
									resposta.add(node.attribute(0).getValue());
								} else {
									resposta.add(node.getText());
								}
							}
							i++;
						}
					}
					String[] variables = new String[resposta.size()];
					int index = 0;
					for(String var: resposta){
						variables[index] = var;
						index++;
					}
					return variables;
					
				} else {
					String content = new String(doc.getPresentacionTelematica().getValue().getContent());
					Document document = DocumentHelper.parseText(content);
					List<Element> llistatNodes = document.selectNodes(rutaSistra);
					
					for(Element node: llistatNodes){
						if(node.attributeCount() > 0){
							resposta.add(node.attribute(0).getValue());
						} else {
							resposta.add(node.getText());
						}
					}
					
					String[] variables = new String[resposta.size()];
					int index = 0;
					for(String var: resposta){
						variables[index] = var;
						index++;
					}
					return variables;
				}
			}
		}
		return null;
	}*/
	private DadesDocument documentSistra(
			TramiteBTE tramit,
			String varSistra,
			net.conselldemallorca.helium.model.hibernate.Document varHelium) throws Exception {
		DadesDocument resposta = null;
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			DatosDocumentoTelematico documentSistra = doc.getPresentacionTelematica().getValue();
			if (documentSistra.getIdentificador().equalsIgnoreCase(varSistra)) {
				resposta = new DadesDocument();
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
				resposta.setData(tramit.getFecha().toGregorianCalendar().getTime());
				/*String nom = doc.getPresentacionTelematica().getValue().getNombre();
				String extensio = doc.getPresentacionTelematica().getValue().getExtension();
				resposta.setArxiuNom(nom + "." + extensio);*/
				if (!documentSistra.isEstructurado()) {
					resposta.setArxiuNom(documentSistra.getNombre());
					resposta.setArxiuContingut(documentSistra.getContent());
				} else {
					ReferenciaRDS referencia = new ReferenciaRDS();
					referencia.setCodigo(documentSistra.getCodigoReferenciaRds());
					referencia.setClave(documentSistra.getClaveReferenciaRds());
					DocumentoRDS documentRDS = sistraRedoseClient.consultarDocumento(referencia);
					resposta.setArxiuNom(documentRDS.getNombreFichero());
					resposta.setArxiuContingut(documentRDS.getDatosFichero());
				}
				break;
			}
		}
		return resposta;
	}
	private List<DadesDocument> documentsSistraAdjunts(
			TramiteBTE tramit,
			String varSistra) throws Exception {
		List<DadesDocument> resposta = new ArrayList<DadesDocument>();
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			DatosDocumentoTelematico documentSistra = doc.getPresentacionTelematica().getValue();
			if (documentSistra.getIdentificador().equalsIgnoreCase(varSistra)) {
				DadesDocument docResposta = new DadesDocument();
				docResposta.setTitol(doc.getNombre());
				docResposta.setData(tramit.getFecha().toGregorianCalendar().getTime());
				if (!documentSistra.isEstructurado()) {
					docResposta.setArxiuNom(documentSistra.getNombre());
					docResposta.setArxiuContingut(documentSistra.getContent());
				} else {
					ReferenciaRDS referencia = new ReferenciaRDS();
					referencia.setCodigo(documentSistra.getCodigoReferenciaRds());
					referencia.setClave(documentSistra.getClaveReferenciaRds());
					DocumentoRDS documentRDS = sistraRedoseClient.consultarDocumento(referencia);
					docResposta.setArxiuNom(documentRDS.getNombreFichero());
					docResposta.setArxiuContingut(documentRDS.getDatosFichero());
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
						if (camp.getRegistreMembres().size() == filaSistra.length) {
							Object[] filaResposta = new Object[filaSistra.length];
							for (int j = 0; j < filaResposta.length; j++) {
								Camp campRegistre = camp.getRegistreMembres().get(j).getMembre();
								filaResposta[j] = valorPerHeliumSimple(filaSistra[j], campRegistre);
							}
							resposta[i] = filaResposta;
						} else {
							logger.error("No s'ha pogut mapejar el camp " + camp.getCodi() + ": el nombre de columnes no coincideix");
							return null;
						}
					}
					return resposta;
				} else {
					if (camp.getRegistreMembres().size() == dadesSistra.length) {
						Object[] resposta = new Object[dadesSistra.length];
						for (int i = 0; i < resposta.length; i++) {
							Camp campRegistre = camp.getRegistreMembres().get(i).getMembre();
							resposta[i] = valorPerHeliumSimple(((String[])dadesSistra)[i], campRegistre);
						}
						return resposta;
					} else {
						logger.error("No s'ha pogut mapejar el camp " + camp.getCodi() + ": el nombre de columnes no coincideix");
						return null;
					}
				}
			} else {
				logger.error("No s'ha pogut mapejar el camp " + camp.getCodi() + ": el camp del sistra no és de tipus registre");
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

	private List<net.conselldemallorca.helium.model.hibernate.Document> getDocuments(ExpedientTipus expedientTipus) {
		DefinicioProcesDto definicioProces = dissenyService.findDarreraAmbExpedientTipus(expedientTipus.getId());
		return dissenyService.findDocumentsAmbDefinicioProces(definicioProces.getId());
	}

	private Date getDataRegistre(TramiteBTE tramit) {
		XMLGregorianCalendar xmlCalendar = tramit.getFechaRegistro();
		if (xmlCalendar == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(xmlCalendar.getYear(), xmlCalendar.getMonth(), xmlCalendar.getDay());
		return cal.getTime();
	}

	private static final Log logger = LogFactory.getLog(DefaultIniciExpedientPlugin.class);

}
