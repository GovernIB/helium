package net.conselldemallorca.helium.ws.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.conselldemallorca.helium.core.model.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.ws.backoffice.BaseBackoffice;


public class BaseBackOfficeTest extends BaseBackoffice {
	
	protected static String numero = "num";
	protected static long clauAcces = 111;
	protected static String identificador = "PROVESHELIUM";
	protected static long unitatAdministrativa = 222 ;
	protected static int versio = 1;
	protected static Date data = new Date();
	protected static String idioma = "ca";
	protected static String registreNumero = "registreNumero";
	protected static Date registreData = new Date();
	protected static String preregistreTipusConfirmacio = "preregTipConf";
	protected static String preregistreNumero = "preregNum";
	protected static Date preregistreData = new Date();
	protected static AutenticacioTipus autenticacioTipus = AutenticacioTipus.USUARI;
	protected static String tramitadorNif = "99999999R";
	protected static String tramitadorNom = "tramitadorNom";
	protected static String interessatNif = "interessatNif";
	protected static String interessatNom = "interessatNom";
	protected static String representantNif = "reprNif";
	protected static String representantNom = "reprfNom";
	protected static boolean signat = true;
	protected static boolean avisosHabilitats = true;
	protected static String avisosSms = "avisosSms";
	protected static String avisosEmail = "avisosEmail";
	protected static boolean notificacioTelematicaHabilitada = true;
	protected static List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
	
	

	@Override
	protected DadesVistaDocument getVistaDocumentTramit(long referenciaCodi, String referenciaClau,
			String plantillaTipus, String idioma) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Test
	public void superTest() throws Exception {
		Camp camp = new Camp();
		//camp.setTipus(TipusCamp.STRING);
		camp.setTipus(TipusCamp.PRICE);
		camp.setMultiple(false);
		StringBuilder logMsg = new StringBuilder();
		ExpedientTipus exptipus = new ExpedientTipus("codi", "nom", new Entorn());
		camp.setExpedientTipus(exptipus);
		logMsg.append(
				"camp.et.codi=" + camp.getExpedientTipus().getCodi() + ", " +
				"camp.et.nom=" + camp.getExpedientTipus().getNom() + ")");
		try {
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<FORMULARIO modelo=\"TF0066SINI\" version=\"1\" >\r\n" + "<NOMENTIDAD></NOMENTIDAD>"
					+ "		<DADA_MULTIPLE><DADA1>d1</DADA1><DADA2>d2</DADA2></DADA_MULTIPLE>" // amb DADA:MULTIPLE ha de retornar null però fer un log d'error
			        + "		<SELECCIO indice=\"7\">Banyalbufar</SELECCIO>"					// amb SELECCIO segurament retornarà Banyalbufar
					+  "     <PRICE>2,000,000.123</PRICE>\r\n" 
			        + "    <DATOS_ACEPTASUBVENCION>\r\n" + "        <IMPORTESUBV>5.000</IMPORTESUBV>\r\n"
					+ "        <ACCEPTSUBV>true</ACCEPTSUBV>\r\n" + "        <ESSISTRA>true</ESSISTRA>\r\n"
					+ "        <IMPSUBVENCIO/>\r\n" + "    </DATOS_ACEPTASUBVENCION>\r\n" + "</FORMULARIO>";
			// valorVariableSistra
			Object valorVariableSistra = null;
			org.w3c.dom.Document document = xmlToDocument(new ByteArrayInputStream(xml.getBytes()));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Element node1 = (Element) xpath.evaluate("/FORMULARIO/DATOS_ACEPTASUBVENCION/IMPSUBVENCIO", document,
					XPathConstants.NODE);
			Element node2 = (Element) xpath.evaluate("/FORMULARIO/NOMENTIDAD", document, XPathConstants.NODE);
			Element node3 = (Element) xpath.evaluate("/FORMULARIO/DADA_MULTIPLE", document, XPathConstants.NODE);
			Element node4 = (Element) xpath.evaluate("/FORMULARIO/SELECCIO", document, XPathConstants.NODE);
			Element node = (Element) xpath.evaluate("/FORMULARIO/PRICE", document, XPathConstants.NODE); 
			if (node != null) {
				if (node.hasAttribute("indice")) {
					valorVariableSistra = node.getAttribute("indice");
				} else if (node.getChildNodes().getLength() == 1) {
					valorVariableSistra = node.getTextContent();
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
									Element columna = (Element) columnes.item(i);
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
					valorVariableSistra = valorsFiles.toArray(new Object[valorsFiles.size()]);
				}		
			}
			
			Object valorHelium = super.valorVariableHelium(valorVariableSistra, camp);
			Map<String, Object> resposta = new HashMap<String, Object>();
			resposta.put(
					// mapeig.getCodiHelium(),
					"codiHelium", 
					valorHelium);
			resposta.toString();
		} catch (Exception ex) {
			logger.error("Error en el mapeig de camp. " + logMsg.toString() + ":" + ex.getMessage());
		}
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
}
