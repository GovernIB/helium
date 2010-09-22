/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.DocumentoBTE;
import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interfície per a iniciar un expedient donat un tràmit de SISTRA.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DefaultIniciExpedientPlugin implements IniciExpedientPlugin {

	private DissenyService dissenyService;
	private ExpedientService expedientService;



	public List<DadesIniciExpedient> obtenirDadesInici(TramiteBTE tramit) {
		List<ExpedientTipus> candidats = dissenyService.findExpedientTipusAmbSistraTramitCodi(tramit.getIdentificadorTramite());
		if (candidats.size() > 0) {
			List<DadesIniciExpedient> resposta = new ArrayList<DadesIniciExpedient>();
			for (ExpedientTipus expedientTipus: candidats) {
				DadesIniciExpedient dadesIniciExpedient = new DadesIniciExpedient();
				dadesIniciExpedient.setEntornCodi(expedientTipus.getEntorn().getCodi());
				dadesIniciExpedient.setTipusCodi(expedientTipus.getCodi());
				if (expedientTipus.getDemanaTitol())
					dadesIniciExpedient.setTitol(tramit.getNumeroEntrada());
				if (expedientTipus.getDemanaNumero())
					dadesIniciExpedient.setNumero(expedientTipus.getNumeroExpedientActual());
				dadesIniciExpedient.setDadesInicials(getDadesInicials(expedientTipus, tramit));
				dadesIniciExpedient.setDocumentsInicials(getDocumentsInicials(expedientTipus, tramit));
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
						if (!campHelium.isMultiple()) {
							String valorSistra = valorVariableSistra(tramit, varSistra);
							Object valorHelium = valorVariableHelium(valorSistra, campHelium, false);
							resposta.put(varHelium, valorHelium);
						} else {
							String[] valorSistra = valorVariableSistraMultiple(tramit, varSistra);
							Object valorHelium = valorVariableHelium(valorSistra, campHelium, true);
							resposta.put(varHelium, valorHelium);
						}
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
		List<DocumentTasca> documents = getDocuments(expedientTipus);
		String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
		for (int i = 0; i < parts.length; i++) {
			String[] parella = parts[i].split(":");
			if (parella.length > 1) {
				String varSistra = parella[0];
				String varHelium = parella[1];
				net.conselldemallorca.helium.model.hibernate.Document docHelium = null;
				for (DocumentTasca document: documents) {
					if (document.getDocument().getCodi().equalsIgnoreCase(varHelium)) {
						docHelium = document.getDocument();
						break;
					}
				}
				try {
					if (docHelium != null)
						resposta.put(
								varHelium,
								valorDocumentSistra(tramit, varSistra, docHelium));
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		return resposta;
	}

	private String valorVariableSistra(TramiteBTE tramit, String varSistra) throws Exception {
		String[] parts = varSistra.split("\\.");
		String documentCodi = parts[0];
		int instancia = new Integer(parts[1]).intValue();
		String finestraCodi = parts[2];
		String campCodi = parts[3];
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			if (
					doc.getPresentacionTelematica().getValue().getIdentificador().equalsIgnoreCase(documentCodi)
				 && doc.getPresentacionTelematica().getValue().getNumeroInstancia() == instancia) {
				String content = new String(doc.getPresentacionTelematica().getValue().getContent());
				Document document = DocumentHelper.parseText(content);
				String xpath = "/FORMULARIO/" + finestraCodi + "/" + campCodi;
				Element node = (Element)document.selectSingleNode(xpath);
				
				if(node.attributeCount() > 0){
					return node.attribute(0).getValue();
				} else {
					return node.getText();
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String[] valorVariableSistraMultiple(TramiteBTE tramit, String varSistra) throws Exception {
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
	}

	private DadesDocument valorDocumentSistra(
			TramiteBTE tramit,
			String varSistra,
			net.conselldemallorca.helium.model.hibernate.Document varHelium) throws Exception {
		DadesDocument resposta = null;
		for (DocumentoBTE doc: tramit.getDocumentos().getDocumento()) {
			if (doc.getPresentacionTelematica().getValue().getIdentificador().equalsIgnoreCase(varSistra)) {
				resposta = new DadesDocument();
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
				resposta.setData(tramit.getFecha().toGregorianCalendar().getTime());
				
				/*String nom = doc.getPresentacionTelematica().getValue().getNombre();
				String extensio = doc.getPresentacionTelematica().getValue().getExtension();
				resposta.setArxiuNom(nom + "." + extensio);*/
				resposta.setArxiuNom(doc.getPresentacionTelematica().getValue().getNombre());
				
				
				resposta.setArxiuContingut(doc.getPresentacionTelematica().getValue().getContent());
				break;
			}
		}
		return resposta;
	}

	private Object valorVariableHelium(Object val, Camp camp, boolean multiple) throws Exception {
		if(multiple){
			String[] valors = (String[])val;
			if (valors == null)
				return null;
			
			if (camp == null) {
			} else if (camp.getTipus().equals(TipusCamp.DATE)) {
				Date[] valorsConvertits = new Date[valors.length];
				for(int i=0; i<valors.length; i++){
					valorsConvertits[i] = new SimpleDateFormat("dd/MM/yyyy").parse(valors[i]);
				}
				return valorsConvertits;
			} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
				Boolean[] valorsConvertits = new Boolean[valors.length];
				for(int i=0; i<valors.length; i++){
					valorsConvertits[i] = new Boolean(valors[i]);
				}
				return valorsConvertits;
			} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
				BigDecimal[] valorsConvertits = new BigDecimal[valors.length];
				for(int i=0; i<valors.length; i++){
					valorsConvertits[i] = new BigDecimal(valors[i]);
				}
				return valorsConvertits;
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				Long[] valorsConvertits = new Long[valors.length];
				for(int i=0; i<valors.length; i++){
					valorsConvertits[i] = new Long(valors[i]);
				}
				return valorsConvertits;
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				Double[] valorsConvertits = new Double[valors.length];
				for(int i=0; i<valors.length; i++){
					valorsConvertits[i] = new Double(valors[i]);
				}
				return valorsConvertits;
			}
			return valors;

		}
		
		String valor = (String)val;
		
		if (valor == null)
			return null;
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

	/*private Set<net.conselldemallorca.helium.model.hibernate.Document> getDocuments(ExpedientTipus expedientTipus) {
		DefinicioProcesDto definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(
				expedientTipus.getId()); 
		return definicioProces.getDocuments();
	}*/
	
	private List<DocumentTasca> getDocuments(ExpedientTipus expedientTipus) {
		TascaDto startTask = expedientService.getStartTask(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getId(),
				null,
				null);
		if (startTask != null)
			return startTask.getDocuments();
		return new ArrayList<DocumentTasca>();
	}

	private static final Log logger = LogFactory.getLog(DefaultIniciExpedientPlugin.class);

}
