/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementació del plugin de tramitacio per tests
 * dels ws de SISTRA
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginSistraMock implements TramitacioPlugin {

	@Override
	public boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente) throws TramitacioPluginException {
		return true;
	}
	
	@Override
	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException {
	}
	
	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioPluginException {
		return null;
	}
	
	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioPluginException {
		return null;
	}

	public void publicarEvent(PublicarEventRequest request) throws TramitacioPluginException {
	}

	public DadesTramit obtenirDadesTramit(ObtenirDadesTramitRequest request) throws TramitacioPluginException {
		DadesTramit dadesTramit = new DadesTramit();
		dadesTramit.setNumero(request.getNumero());
		dadesTramit.setClauAcces(getRandomNumber());
		dadesTramit.setIdentificador("TF0069RDIN");
		dadesTramit.setUnitatAdministrativa(12L);
		dadesTramit.setVersio(1);
		dadesTramit.setData(new Date());
		dadesTramit.setIdioma("ca");
		dadesTramit.setRegistreNumero("GOIBE" + getRandomNumber() + "/2023");
		dadesTramit.setRegistreData(new Date());
		dadesTramit.setPreregistreTipusConfirmacio(null);
		dadesTramit.setPreregistreNumero(null);
		dadesTramit.setPreregistreData(null);
		dadesTramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
		dadesTramit.setTramitadorNif("18225486X");
		dadesTramit.setTramitadorNom("Andreu Nadal, Melcior");
		dadesTramit.setInteressatNif("18225486X");
		dadesTramit.setInteressatNom("Andreu Nadal, Melcior");
		dadesTramit.setRepresentantNif("18225486X");
		dadesTramit.setRepresentantNom("Andreu Nadal, Melcior");
		dadesTramit.setSignat(false);
		dadesTramit.setAvisosHabilitats(false);
		dadesTramit.setAvisosSms(null);
		dadesTramit.setAvisosEmail(null);
		dadesTramit.setNotificacioTelematicaHabilitada(false);
		dadesTramit.setDocuments(getDocumentTramits(request.getClau()));
		return dadesTramit;
	}

	private List<DocumentTramit> getDocumentTramits(String clau) {
		List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
		// Document a adjuntar
		DocumentTramit document = new DocumentTramit();
		document.setNom("doc1");
		document.setIdentificador("ANE01");
		document.setInstanciaNumero(1);
		document.setDocumentTelematic(getDocumentTelematic(clau));
		document.setDocumentPresencial(null);
		documents.add(document);
		// Document del formulari
		DocumentTramit formulari = new DocumentTramit();
		formulari.setNom("Formulario");
		formulari.setIdentificador("FORM1");
		formulari.setInstanciaNumero(1);
		formulari.setDocumentTelematic(getFormulariTelematic(clau));
		formulari.setDocumentPresencial(null);
		documents.add(formulari);

		return documents;
	}

	private DocumentTelematic getDocumentTelematic(String clau) {
		DocumentTelematic documentTelematic = new DocumentTelematic();
		documentTelematic.setArxiuNom("certificado censal.PDF");
		documentTelematic.setArxiuExtensio("PDF");
		byte[] bytes = getFileContent();
		documentTelematic.setArxiuContingut(bytes);
		documentTelematic.setReferenciaGestorDocumental(null);
		documentTelematic.setSignatures(null);
		documentTelematic.setReferenciaCodi(getRandomNumber());
		documentTelematic.setReferenciaClau(clau);
		documentTelematic.setEstructurat(null);
		return documentTelematic;
	}

	private DocumentTelematic getFormulariTelematic(String clau) {
		DocumentTelematic documentTelematic = new DocumentTelematic();
		documentTelematic.setArxiuNom("formulario.xml");
		documentTelematic.setArxiuExtensio("xml");
//		byte[] bytes = getFileContent();
		documentTelematic.setArxiuContingut(formulari.getBytes());
		documentTelematic.setReferenciaGestorDocumental(null);
		documentTelematic.setSignatures(null);
		documentTelematic.setReferenciaCodi(getRandomNumber());
		documentTelematic.setReferenciaClau(clau);
		documentTelematic.setEstructurat(null);
		return documentTelematic;
	}
	private String formulari = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<FORMULARIO modelo=\"TF0069RDIN\" version=\"1\" >\n" +
			"    <DATOS_IDIOMA>\n" +
			"        <IDIOMAOCULTO>es</IDIOMAOCULTO>\n" +
			"    </DATOS_IDIOMA>\n" +
			"    <DATOS_REPRESENTANTE>\n" +
			"        <NIFREPRESENT>43090588L</NIFREPRESENT>\n" +
			"        <NOMREPRESENT>Antonio</NOMREPRESENT>\n" +
			"        <CG1REPRESENTANT>Fresco</CG1REPRESENTANT>\n" +
			"        <CG2REPRESENTANT>Pombo</CG2REPRESENTANT>\n" +
			"        <DIRREPRESENT>calle de pruebas</DIRREPRESENT>\n" +
			"        <CPREPRESENT>07000</CPREPRESENT>\n" +
			"        <SEXEREPRESENTANT indice=\"H\">Hombre</SEXEREPRESENTANT>\n" +
			"        <PAISREPRESENTANT indice=\"724\">ESPAÑA</PAISREPRESENTANT>\n" +
			"        <PROVREPRESENT indice=\"7\">Balears (Illes)</PROVREPRESENT>\n" +
			"        <MUNIREPRESENT indice=\"1\">Alaró</MUNIREPRESENT>\n" +
			"        <POBREPRESENTANT>Alaró</POBREPRESENTANT>\n" +
			"        <TELREPRESENT>999888777</TELREPRESENT>\n" +
			"        <EMAILREPRESENT>correo@pruebas.com</EMAILREPRESENT>\n" +
			"        <TPODREPRESENT indice=\"1\">General</TPODREPRESENT>\n" +
			"        <INSCRIREA>true</INSCRIREA>\n" +
			"        <CODICSVAP>546546546464654654654</CODICSVAP>\n" +
			"        <NOMBREREPCOMP>Antonio Fresco Pombo</NOMBREREPCOMP>\n" +
			"    </DATOS_REPRESENTANTE>\n" +
			"    <DATOSGENERICO>\n" +
			"        <CHECKSOL>true</CHECKSOL>\n" +
			"    </DATOSGENERICO>\n" +
			"    <DATOS_SOLICITANTE>\n" +
			"        <NIFSOLICITANT>43090588L</NIFSOLICITANT>\n" +
			"        <NOMSOLICITANT>Antonio</NOMSOLICITANT>\n" +
			"        <CG1SOLICITANT>Fresco</CG1SOLICITANT>\n" +
			"        <CG2SOLICITANT>Pombo</CG2SOLICITANT>\n" +
			"        <DOMSOLICITANT>calle de pruebas</DOMSOLICITANT>\n" +
			"        <CPSOLICITANT>07000</CPSOLICITANT>\n" +
			"        <SEXESOLICITANT indice=\"H\">Hombre</SEXESOLICITANT>\n" +
			"        <PAISSOLICITANT indice=\"724\">ESPAÑA</PAISSOLICITANT>\n" +
			"        <PROVSOLICITANT indice=\"7\">Balears (Illes)</PROVSOLICITANT>\n" +
			"        <MUNRSOLICITANT indice=\"1\">Alaró</MUNRSOLICITANT>\n" +
			"        <POBSOLICITANT>Alaró</POBSOLICITANT>\n" +
			"        <TELSOLICITANT>999888777</TELSOLICITANT>\n" +
			"        <EMAILSOLICITANT>correo@pruebas.com</EMAILSOLICITANT>\n" +
			"        <NOMBRESOLCOMP>Antonio Fresco Pombo</NOMBRESOLCOMP>\n" +
			"    </DATOS_SOLICITANTE>\n" +
			"    <DATOS_EXPEDIENTE>\n" +
			"        <NUMEXPEDIENTE>AUTXXXX/2020</NUMEXPEDIENTE>\n" +
			"        <NUMREGISTRO>L99EXXXX/2020</NUMREGISTRO>\n" +
			"        <TIPOENTRADA indice=\"1\">Enmienda</TIPOENTRADA>\n" +
			"        <EXPONE>Pruebas indra</EXPONE>\n" +
			"        <SOLICITA>Pruebas indra</SOLICITA>\n" +
			"    </DATOS_EXPEDIENTE>\n" +
			"</FORMULARIO>";

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException {
	}

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws TramitacioPluginException {
		return null;
	}

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException {
		DadesVistaDocument resposta = new DadesVistaDocument();
		resposta.setNom("certificado censal.PDF");
		resposta.setArxiuNom("certificado censal.PDF");
		resposta.setArxiuContingut(getFileContent());
		return resposta;
	}

	private static byte[] getFileContent() {
		byte[] bytes = null;
		File file = new File("/home/siona/Documents/DocumentsTests/buit.pdf");
		try {
			bytes = new byte[(int) file.length()];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				fis.read(bytes);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		} catch (Exception ex) {}
		return bytes;
	}

	private int getRandomNumber() {
		return getRandomNumber(1, 999999);
	}
	private int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	private static final Log logger = LogFactory.getLog(TramitacioPluginSistraMock.class);
}
