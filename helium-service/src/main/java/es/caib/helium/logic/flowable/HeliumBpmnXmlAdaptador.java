package es.caib.helium.logic.flowable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.flowable.common.engine.api.FlowableException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.helium.bpmn.handler.HeliumBpmnXml;

/**
 * Adapta el BPMN 2.0 genèric al que llegeix Flowable:
 * <ul>
 *   <li>tradueix els atributs d'extensió de Helium ({@link HeliumBpmnXml#ATRIBUTS_EXTENSIO}) als
 *   equivalents de Flowable ({@code helium:async} → {@code flowable:async}...);</li>
 *   <li>dins {@code resourceAssignmentExpression} (assignació de tasques), passa la forma
 *   {@code <bpmn:expression xsi:type="bpmn:tFormalExpression">} a {@code <bpmn:formalExpression>},
 *   que és l'única que entén Flowable.</li>
 * </ul>
 *
 * <p>Només s'aplica al XML que llegeix el motor (veure {@link HeliumBpmnPreDeployer}); el XML
 * desat al desplegament continua sent BPMN 2.0 genèric.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class HeliumBpmnXmlAdaptador {

	public static final String FLOWABLE_NAMESPACE = "http://flowable.org/bpmn";
	public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String FLOWABLE_PREFIX = "flowable";

	/**
	 * Atributs que no es tradueixen als de Flowable perquè els executa Helium (veure
	 * {@link HeliumServiceTaskXMLConverter}): {@code helium:expression} s'avalua a {@link HeliumHandlerJavaDelegate}
	 * per desar el resultat a les dades de l'expedient.
	 */
	static final java.util.Set<String> ATRIBUTS_GESTIONATS_PER_HELIUM = java.util.Set.of("expression");

	private HeliumBpmnXmlAdaptador() {}

	/**
	 * Retorna el XML amb els atributs de Helium traduïts als de Flowable, o el mateix array si
	 * no en conté cap.
	 */
	public static byte[] adaptar(byte[] xml) {
		if (xml == null || !potNecessitarAdaptacio(xml)) {
			return xml;
		}
		try {
			Document document = parse(xml);
			List<Attr> atributs = new ArrayList<>();
			List<Element> expressions = new ArrayList<>();
			cercar(document.getDocumentElement(), atributs, expressions);
			if (atributs.isEmpty() && expressions.isEmpty()) {
				return xml;
			}
			if (!atributs.isEmpty()) {
				String prefix = prefixFlowable(document.getDocumentElement());
				for (Attr atribut : atributs) {
					Element element = atribut.getOwnerElement();
					String nom = atribut.getLocalName();
					if (!element.hasAttributeNS(FLOWABLE_NAMESPACE, nom)) {
						element.setAttributeNS(FLOWABLE_NAMESPACE, prefix + ":" + nom, atribut.getValue());
					}
					element.removeAttributeNode(atribut);
				}
			}
			for (Element expression : expressions) {
				// <bpmn:expression xsi:type="bpmn:tFormalExpression"> → <bpmn:formalExpression>: les dues formes són
				// BPMN 2.0 vàlid, però Flowable només llegeix la segona dins resourceAssignmentExpression
				expression.removeAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
				String prefix = expression.getPrefix();
				document.renameNode(
					expression,
					BPMN_NAMESPACE,
					(prefix != null ? prefix + ":" : "") + "formalExpression");
			}
			return serialitzar(document);
		} catch (FlowableException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new FlowableException("No s'han pogut adaptar els atributs de Helium del BPMN", ex);
		}
	}

	private static boolean potNecessitarAdaptacio(byte[] xml) {
		// Els textos cercats són ASCII, així que es poden cercar sense conèixer la codificació del document
		String text = new String(xml, StandardCharsets.ISO_8859_1);
		return text.contains(HeliumBpmnXml.NAMESPACE) || text.contains("resourceAssignmentExpression");
	}

	private static void cercar(Element element, List<Attr> atributs, List<Element> expressions) {
		if (esExpressionFormalDAssignacio(element)) {
			expressions.add(element);
		}
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			if (HeliumBpmnXml.NAMESPACE.equals(attr.getNamespaceURI())
					&& HeliumBpmnXml.ATRIBUTS_EXTENSIO.contains(attr.getLocalName())
					&& !ATRIBUTS_GESTIONATS_PER_HELIUM.contains(attr.getLocalName())) {
				atributs.add(attr);
			}
		}
		NodeList fills = element.getChildNodes();
		for (int i = 0; i < fills.getLength(); i++) {
			if (fills.item(i).getNodeType() == Node.ELEMENT_NODE) {
				cercar((Element) fills.item(i), atributs, expressions);
			}
		}
	}

	/** {@code <bpmn:expression xsi:type="...tFormalExpression">} fill d'un {@code resourceAssignmentExpression}. */
	private static boolean esExpressionFormalDAssignacio(Element element) {
		if (!BPMN_NAMESPACE.equals(element.getNamespaceURI()) || !"expression".equals(element.getLocalName())) {
			return false;
		}
		Node pare = element.getParentNode();
		if (pare == null || !"resourceAssignmentExpression".equals(pare.getLocalName())) {
			return false;
		}
		String tipus = element.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
		return tipus != null && (tipus.equals("tFormalExpression") || tipus.endsWith(":tFormalExpression"));
	}

	/** Declara l'espai de noms de Flowable a l'arrel (si no ho està) i en retorna el prefix. */
	private static String prefixFlowable(Element arrel) {
		String existent = arrel.lookupPrefix(FLOWABLE_NAMESPACE);
		if (existent != null) {
			return existent;
		}
		String prefix = FLOWABLE_PREFIX;
		for (int i = 1; arrel.lookupNamespaceURI(prefix) != null; i++) {
			prefix = FLOWABLE_PREFIX + i;
		}
		arrel.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, FLOWABLE_NAMESPACE);
		return prefix;
	}

	private static Document parse(byte[] xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(new ByteArrayInputStream(xml));
	}

	private static byte[] serialitzar(Document document) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(document), new StreamResult(out));
		return out.toByteArray();
	}

}
