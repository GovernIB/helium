package net.conselldemallorca.helium.jbpm3.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.security.crypto.codec.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesConsultaPinbal;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Funcionari;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Titular;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class PinbalConsultaGenericaHandler extends BasicActionHandler implements PinbalConsultaGenericaHandlerInterface {
	
	protected static final Log logger = LogFactory.getLog(PinbalConsultaGenericaHandler.class);
	
	
	private String codi;
	private String var_codi;
	
	/**Codi del servei sol·licitat de PINBAL.**/
	protected String serveiCodi;
	protected String varServeiCodi;
	
	/**Codi del tipus de document en què guardar el document del justificant de la consulta..**/
	protected String documentCodi;
	
	/**Codi de la variable que conté el codi de document en què guardar el justificant de la consulta.**/
	protected String varDocumentCodi;
	
	/**finalitat Text descriptiu de la finalitzat de la consulta. OBLIGATORI.**/
	protected String finalitat;
	
	/**Codi de la variable que conté el text per la finalitat.**/
	protected String varFinalitat;
	
	/**Indica si la consulta té el consentiment del titular o per llei. Les
		possibles valors són:
		• "SI": Es té el consentiment del titular interessat.
		• "LLEI": El consentiment és per llei.**/
	protected enum consentiment {
		SI,
		LLEI
	}
	protected String consentiment;
	protected String varConsentiment;
	
	/**Codi de l'interessat per informar les dades del titular. Si no es té
	interessat es poden informar els paràmetres del titular.**/
	protected String interessatCodi;
	
	/**Codi de la variable que conté el codi de l'interessat per informar
	les dades del titular.**/
	protected String varInteressatCodi;
	
	/**Codi de la variable que conté el nom del titular interessat.**/
	protected String varTitularNom;
	protected String titularNom;
	
	/**Codi de la variable que conté el primer llinatge del titular interessat.**/
	protected String varTitularLlinatge1;
	protected String titularLlinatge1;
	
	/**Codi de la variable que conté el segon llinatge del titular interessat.**/
	protected String varTitularLlinatge2;
	protected String titularLlinatge2;
	
	/**Camp per indicar el tipus de documentació. Els possibles valors.  OBLIGATORI**/
	protected enum varTitularTipusDocumentacio {
		DNI,
		NIE,
		NIF,
		Passaport,
		Altres	
	}
	protected String varTitularTipusDocumentacio;
	protected String titularTipusDocumentacio;
	
	/**Codi de la variable que conté el valor de la documentació del
	titular interessat.	**/
	protected String varTitularDocumentacio;
	protected String titularDocumentacio;
	
	/**Valor de text amb l' XML de les dades específiques. Per informar
	les dades específiques amb variables Helium revisarà el contingut
	del text de dades específiques i substituirà els valors tipus "$
	{var_codi}" pel valor de la variable que coincideixi amb el codi.**/
	protected String dadesEspecifiques;
	
	/**Codi de la variable que conté el valor de text per les dades específiques.**/
	protected String varDadesEspecifiques;
	

	protected String idioma;	// Possibles valors [ES, CA]
	protected String varIdioma;
	
	/**Indica si la consulta és asíncrona o no. Per exemple el valor "True" i "true" indiquen una crida asíncrona,
	 qualsevol altre valor es considerarà fals i es farà una crida síncrona. **/
	protected String asincrona;
	protected String varAsincrona;
	/**Codi opcional de la transició de sortida en el cas que la consulta a PINBAL finalitzi correctament 
	 i en el cas que el flux estigui aturat en un estat.**/
	protected String transicioOK;
	/**Variable que conté la transició de sortida per a la consulta a PINBAL amb finalització correcta.**/
	protected String varTransicioOK;
	
	/**Codi opcional de la transició de sortida en el cas que la consulta a PINBAL finalitzi correctament 
	 i en el cas que el flux estigui aturat en un estat.**/
	protected String transicioKO;
	/**Variable que conté la transició de sortida per a la consulta a PINBAL amb finalització incorrecta.**/
	protected String varTransicioKO;
	
	
	public void execute(ExecutionContext executionContext) throws Exception {	
		logger.debug("Inici execució handler genèric a Pinbal");
		ExpedientDto expedient = getExpedientActual(executionContext);
	
		DadesConsultaPinbal dadesConsultaPinbal = this.obtenirDadesConsulta(executionContext);
		
		dadesConsultaPinbal.setServeiCodi((String)getValorOVariable(
				executionContext,
				this.serveiCodi,
				this.varServeiCodi));
		
		if(dadesConsultaPinbal.getTitular()!=null) {
			dadesConsultaPinbal.getTitular().setNombreCompleto((String)getValorOVariable(
					executionContext,
					this.titularNom,
					this.varTitularNom));
		}

		if(dadesConsultaPinbal.getTitular()!=null) {
			dadesConsultaPinbal.getTitular().setNombreCompleto((String)getValorOVariable(
					executionContext,
					this.titularNom,
					this.varTitularNom));
		}
		
		dadesConsultaPinbal.setXmlDadesEspecifiques((String)getValorOVariable(
				executionContext,
				this.dadesEspecifiques,
				this.varDadesEspecifiques));
		
		String dadesEspecifiquesXml = dadesConsultaPinbal.getXmlDadesEspecifiques();
		if(dadesEspecifiquesXml!=null && !"".equals(dadesEspecifiquesXml)) {
			
//			executionContext.setVariable("var_codi", "00000000T");
	
			Map<String, Object> mapaDadesEspecifiques = this.getDadesEspecifiquesXml(dadesEspecifiquesXml);
			for (Map.Entry<String, Object> entry : mapaDadesEspecifiques.entrySet()) {
				String codiAmbCorxets= entry.getValue().toString();
				String codiSenseCorxets = codiAmbCorxets.replace("${", "").replace("}", "");
				Object value = this.getVariableValor(executionContext, codiSenseCorxets);
				String valorText = value != null? value.toString() : "";
				dadesEspecifiquesXml = dadesEspecifiquesXml.replace( "${" + codiSenseCorxets + "}", valorText);
			}
			dadesConsultaPinbal.setXmlDadesEspecifiques(dadesEspecifiquesXml);
		}
		
		dadesConsultaPinbal.setTransicioOK((String)getValorOVariable(
				executionContext,
				this.transicioOK,
				this.varTransicioOK));

		dadesConsultaPinbal.setTransicioKO((String)getValorOVariable(
				executionContext,
				this.transicioKO,
				this.varTransicioKO));

		Boolean asincrona = getValorOVariableBoolean(
                executionContext,
                this.asincrona,
                this.varAsincrona);
		dadesConsultaPinbal.setAsincrona(asincrona != null ? asincrona.booleanValue() : false);
		
		Object resposta = super.consultaPinbal(
				dadesConsultaPinbal,
				expedient.getId(),
				expedient.getProcessInstanceId(),
				(executionContext!=null && executionContext.getToken()!=null)?executionContext.getToken().getId():null);

	}
	
	
	private Map<String, Object> getDadesEspecifiquesXml(
			String xmlPeticion) throws ParserConfigurationException, SAXException, IOException {
		Map<String, Object> dades = new HashMap<String, Object>();
		if (xmlPeticion != null) {
			Document doc = xmlToDocument(
					new ByteArrayInputStream(xmlPeticion.getBytes()));
			NodeList nl = doc.getElementsByTagNameNS("*", "DatosEspecificos");
			if (nl.getLength() > 0) {
				List<String> path = new ArrayList<String>();
				recorrerDocument(
						nl.item(0),
						path,
						dades,
						true);
			}
		}
		return dades;
	}
	
	private void recorrerDocument(
			org.w3c.dom.Node node,
			List<String> path,
			Map<String, Object> dades,
			boolean incloureAlPath) {
		if (incloureAlPath) {
			if (node.getPrefix() != null) {
				path.add(node.getNodeName().substring(
						node.getPrefix().length() + 1));
			} else {
				path.add(node.getNodeName());
			}
		}
		if (node.hasChildNodes()) {
			NodeList fills = node.getChildNodes();
			for (int i = 0; i < fills.getLength(); i++) {
				org.w3c.dom.Node fill = fills.item(i);
				if (fill.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					recorrerDocument(
							fill,
							path,
							dades,
							true);
				}
				if (fill.getNodeType() == org.w3c.dom.Node.TEXT_NODE && fills.getLength() == 1) {
					//TODO: si el node.getTextContect aplica a ${codi} llavors obtenir el "codi"
					//TODO: obtenir el valor per la variable "codi" amb this.getVariableText("codi")
					//SUubstituir ${codi} pel valor obtingut 
					dades.put(
							pathToString(path),
							node.getTextContent());
				}
			}
		} else {
			dades.put(
					pathToString(path),
					node.getTextContent());
		}
		if (incloureAlPath) {
			path.remove(path.size() - 1);
		}
	}
	
	private Document xmlToDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}
	private String nodeToString(org.w3c.dom.Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
	private String pathToString(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < path.size(); i++) {
			sb.append("/");
			sb.append(path.get(i));
		}
		return sb.toString();
	}

	
	protected DadesConsultaPinbal obtenirDadesConsulta (ExecutionContext executionContext) throws ParseException {
		DadesConsultaPinbal dadesConsultaPinbal = new DadesConsultaPinbal();
		Titular titular = new Titular();
		titular.setNombre((String)getValorOVariable(
				executionContext,
				this.titularNom,
				this.varTitularNom));
		titular.setApellido1((String)getValorOVariable(
				executionContext,
				this.titularLlinatge1,
				this.varTitularLlinatge1));
		titular.setApellido2((String)getValorOVariable(
				executionContext,
				this.titularLlinatge2,
				this.varTitularLlinatge2));
		titular.setDocumentacion((String)getValorOVariable(
				executionContext,
				this.titularDocumentacio,
				this.varTitularDocumentacio));
		titular.setTipoDocumentacion((String)getValorOVariable(
				executionContext,
				this.titularTipusDocumentacio,
				this.varTitularTipusDocumentacio));
		
		/**El nom complet no es vàlid per a tots els serveis**/
//		titular.setNombreCompleto((String)getValorOVariable(
//				executionContext,
//				this.titularNom,
//				this.varTitularNom));
		
		dadesConsultaPinbal.setTitular(titular);
		
		dadesConsultaPinbal.setServeiCodi((String)getValorOVariable(
				executionContext,
				this.serveiCodi,
				this.varServeiCodi));
		
		dadesConsultaPinbal.setDocumentCodi((String)getValorOVariable(
				executionContext,
				this.documentCodi,
				this.varDocumentCodi));		
		
		dadesConsultaPinbal.setFinalitat((String)getValorOVariable(
				executionContext,
				this.finalitat,
				this.varFinalitat));
		
		dadesConsultaPinbal.setConsentiment((String)getValorOVariable(
				executionContext,
				this.consentiment,
				this.varConsentiment));

		dadesConsultaPinbal.setInteressatCodi((String)getValorOVariable(
				executionContext,
				this.interessatCodi,
				this.varInteressatCodi));
		if(this.asincrona!=null && !this.asincrona.isEmpty()) {
			boolean asincrona = Boolean.parseBoolean(this.asincrona.toLowerCase().trim());
			dadesConsultaPinbal.setAsincrona(asincrona);
		}
		
		return dadesConsultaPinbal;
	}
	
	private String formatIdentificadorEni(String identificador) {
		String identificadorEni = String.format("%30s", identificador).replace(' ', '0').toUpperCase();
		if (identificadorEni.length() > 30) {
			return identificadorEni.substring(0, 12) + "..." + identificadorEni.substring(identificadorEni.length() - 13);
		}
		return identificadorEni;
	}
    
	private Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.decode(s.getBytes());
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64.encode(baos.toByteArray()));
	}

	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}

	public void setVarServeiCodi(String varServeiCodi) {
		this.varServeiCodi = varServeiCodi;
	}

	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}

	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}

	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}

	public void setVarFinalitat(String varFinalitat) {
		this.varFinalitat = varFinalitat;
	}
	
	public void setConsentiment(String consentiment) {
		this.consentiment = consentiment;
	}
	
	public void setVarConsentiment(String varConsentiment) {
		this.varConsentiment = varConsentiment;
	}

	public void setInteressatCodi(String interessatCodi) {
		this.interessatCodi = interessatCodi;
	}

	public void setVarInteressatCodi(String varInteressatCodi) {
		this.varInteressatCodi = varInteressatCodi;
	}

	public void setVarTitularNom(String varTitularNom) {
		this.varTitularNom = varTitularNom;
	}

	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}

	public void setVarTitularLlinatge1(String varTitularLlinatge1) {
		this.varTitularLlinatge1 = varTitularLlinatge1;
	}

	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}

	public void setVarTitularLlinatge2(String varTitularLlinatge2) {
		this.varTitularLlinatge2 = varTitularLlinatge2;
	}

	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}

	public void setVarTitularDocumentacio(String varTitularDocumentacio) {
		this.varTitularDocumentacio = varTitularDocumentacio;
	}

	public void setTitularDocumentacio(String titularDocumentacio) {
		this.titularDocumentacio = titularDocumentacio;
	}

	public void setDadesEspecifiques(String dadesEspecifiques) {
		this.dadesEspecifiques = dadesEspecifiques;
	}

	public void setVarDadesEspecifiques(String varDadesEspecifiques) {
		this.varDadesEspecifiques = varDadesEspecifiques;
	}

	public void setVarTitularTipusDocumentacio(String varTitularTipusDocumentacio) {
		this.varTitularTipusDocumentacio = varTitularTipusDocumentacio;
	}

	@Override
	public void setTitularTipusDocumentacio(String titularTipusDocumentacio) {
		this.titularTipusDocumentacio=titularTipusDocumentacio;
		
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}


	public void setVar_codi(String var_codi) {
		this.var_codi = var_codi;
	}


	public void setAsincrona(String asincrona) {
		this.asincrona = asincrona;
	}
	
	public void setVarAsincrona(String varAsincrona) {
		this.varAsincrona = varAsincrona;
	}
	
	public void setTransicioOK(String transicioOK) {
		this.transicioOK = transicioOK;
	}

	public void setVarTransicioOK(String varTransicioOK) {
		this.varTransicioOK = varTransicioOK;
	}

	public void setVarTransicioKO(String varTransicioKO) {
		this.varTransicioKO = varTransicioKO;
	}

	public void setTransicioKO(String transicioKO) {
		this.varTransicioKO = transicioKO;	
	}
	
	

}