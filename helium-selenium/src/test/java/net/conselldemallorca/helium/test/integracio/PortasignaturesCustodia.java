package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.portafib.ws.callback.api.v1.SigningRequest;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.test.BaseExpedientTest;
import net.consellemallorca.helium.util.ScreenShotOnFailure;

/** Tests per comprovar la integració amb el servei de porta signatures i de custòdia de documents signats.
 * El servei de portasignatures es crida des d'un handler i envia un document
 * a firmar al servei per als alts càrrecs. Quan els alts càrrecs firmen
 * els documents el servei crida al ws de callback del portasignatures per informar
 * a helium que ja s'ha firmat i que pot proseguir amb l'expedient si estava pendent
 * de les firmes.
 * El servei de custòdia de signatures és un servei extern que emmagatzema els documents signats i proporciona
 * una url de consulta o el document en sí.
 * 
 * Els dos serveis s'haurien de provar per separat però el servei de custòdia de firmes
 * és difícil de provar sense les signatures amb applet o passarel·la i s'aprofita el fet que el callback
 * del portafirmes fa que es guardi el document firmat al servei de custòdia.
 * 
 * Per fer les proves:
 * 1- Es crearà un expedient que utilitzi el servei de portafirmes.
 * 2- Es comprova que l'expedient queda pendent de firma.
 * 3- Es crida al WS de callback
 * 4- Es comprova que l'expedient ha avançat.
 * 5- Es comproven les urls que ha proporcionat custòdia i la descàrrega del fitxer retornat per custòdia.
 * 6- S'esborra l'expedient creat.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortasignaturesCustodia extends BaseExpedientTest {
	
	@Rule
    public ScreenShotOnFailure failure = new ScreenShotOnFailure();
	
	/** Títol utilitzat per a l'expedient del portafirmes amb document signat. */
	private static final String SELENIUM_EXPEDIENT_TITOL_SIGNAT = "Selenium portasignatures custòdia signat";
	/** Títol utilitzat per a l'expedient del portafirmes amb document signat. */
	private static final String SELENIUM_EXPEDIENT_TITOL_REBUTJAT = "Selenium portasignatures custòdia rebutjat";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		// Carrega el magatzem de certificats de confiança
		String trustStoreFilePath = propietats.getTrustStoreFilePath();
		if(trustStoreFilePath == null)
			trustStoreFilePath = getTrustStoreFilePath();
		System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath);

		// Configura la rule de captura de pantalles en cas d'error
		failure.setDriver(driver);		
		failure.setScreenshotHelper(screenshotHelper);		
		// Obre el navegador en la pàgina inicial i a l'entorn de test
		carregarPaginaIncial();
		navegarEntornTest();		
	}	
	
	/** Crea els expedients de tipus portafirmes
	 */
	@Test
	public void a01_crearExpedient_Signat() throws Exception {
		// Si ja existeix l'expedient, l'esborra
		esborrarExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT);
		// Crea l'expedient amb la opció del porta signatures
		crearExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT, "psigna");
	}
	
	/** Envia a firmar l'expedient que se signarà correctament
	 *  Entra a la pestanya de tasques i finalitza la tasca per a que el document s'enviï a firmar.
	 */
	@Test
	public void a02_enviarAFirmar_signat() throws Exception {
		
		this.enviarSignar(SELENIUM_EXPEDIENT_TITOL_SIGNAT);
		waitPaginaCarregada();
	}
	
	/** Crea els expedients de tipus portafirmes
	 */
	@Test
	public void a03_crearExpedient_rebutjat() throws Exception {
		// Si ja existeix l'expedient, l'esborra
		esborrarExpedient(SELENIUM_EXPEDIENT_TITOL_REBUTJAT);
		// Crea l'expedient amb la opció del porta signatures
		crearExpedient(SELENIUM_EXPEDIENT_TITOL_REBUTJAT, "psigna");
	}
	
	
	
	/** Envia a firmar l'expedient que es rebutjarà
	 *  Entra a la pestanya de tasques i finalitza la tasca per a que el document s'enviï a firmar.
	 */
	@Test
	public void a04_enviarAFirmar_rebutjat() throws Exception {
		
		this.enviarSignar(SELENIUM_EXPEDIENT_TITOL_REBUTJAT);
		waitPaginaCarregada();
	}	
	
	/** Mètode privat per enviar a signar un expedient de tipus portasignatures. */
	private void enviarSignar(String titol) {
		String botoFinalitzarId = "tasca-boto-completar-Finalitzar";
		
		// Navega a la pestanya de tasques
		navegarExpedient(titol, "tasques");

		// Comprova que existeix la taula de tasques
		WebElement taulaTasques = existeixElementAssert(By.id("tasques-pendents-meves"), "No existeix la taula de tasques");
		// Cerca la fila amb la tasca del registre
		String tascaId = null;
		for (WebElement tr : taulaTasques.findElements(By.cssSelector("tr")))
			if (tr.getText().contains("firmar")) {
				// Prem sobre la opció del menú
				WebElement btnGroup = tr.findElement(By.cssSelector(".btn-group"));
				btnGroup.click();
				// Troba l'enllaç de tramitar la tasca
				for (WebElement a : btnGroup.findElements(By.cssSelector("a")))
					if (a.findElements(By.cssSelector(".fa.fa-folder-open")).size() > 0) {
						// Guarda la url de la modal
						tascaId = a.getAttribute("id").replace("tramitar-tasca-", "");
						// Obre la modal de la tasca
						a.click();
						break;
					}
				break;
			}
		// Comprova que s'ha trobat la url de la modal
		assertNotNull(tascaId, "No s'ha pogut obrir correctament la tramitació de la tasca");
		
		// Passa a la finestra modal de tramitació de la tasca i espera que es carreguin els controls
		modalObertaAssert(tascaId);
		vesAModal(tascaId);
		waitPaginaCarregada();
		// Informa el responsable amb el codi de l'usuari de proves
		existeixElementAssert(By.id("psigna_responsable")).clear();
		driver.findElement(By.id("psigna_responsable")).sendKeys(propietats.getUsuariTestCodi());
		// Informa la importància "normal"
		driver.findElement(By.id("psigna_importancia")).clear();
		driver.findElement(By.id("psigna_importancia")).sendKeys("normal");
		
		// Torna fora de la modal i prem Validar les dades que ja estan introduïdes
		tornaAPare();
		existeixElementAssert(By.id("tasca-boto-validar")).click();
		existeixElementAssert(By.id(botoFinalitzarId));
		waitPaginaCarregada();
		
		// Entra de nou a la modal i navega a la pestanya de documents
		vesAModal(tascaId);
		existeixElementAssert(By.id("pipella-document")).click();
		waitPaginaCarregada();
		
		// Selecciona un document
		existeixElementAssert(By.id("documentsForm")).findElement(By.cssSelector("input[name='arxiu']")).sendKeys(
				getBlankFilePath());
		// Guardar el document
		existeixElementAssert(By.id("documentsForm")).findElement(By.cssSelector(".fa-floppy-o")).click();
		existeixElementAssert(By.cssSelector(".fa-download"));	// espera que aparegui el botó de descàrrega del document

		// Torna fora de la modal i prem finalitzar la tasca
		tornaAPare();
		existeixElementAssert(By.id(botoFinalitzarId)).click();
		// Accepta l'avís de finalització de la tasca
		if (isAlertPresent()) {
			acceptarAlerta();
		}		
		waitPaginaCarregada();
		
		// Verifica la creació de la tasca
		tornaAPare();
		existeixElementAssert(By.cssSelector(".fa-check"));
		// Espera a que desaparegui el vel abans de continuar
		noExisteixElement(By.cssSelector(".modal-backdrop"));		
	}
	
	/** Si és l'entor de desenvolupament crida al WS del Callback de porta firmes amb resposta correcta. Si és l'entorn 
	 * de PRE de test navega a la aplicació del portasignatures i firma amb l'applet de firmes.
	 * Aquest test:
	 * 1- Entra a la pestanya de documents i comprova que està pendent de firma
	 * 2- Agafa les dades del portafirmes.
	 * 3- Si és l'entorn de desenvolupament crida al WS de callback del portafirmes
	 * 4- Si és l'entorn de pre producció navega al porta firmes i signa amb l'applet
	 * 5- Comprova que l'expedient ha finalitzat correctament
	 */
	@Test
	public void a05_portafirmesCorrecte() throws Exception {
				
		if ("dev".equals(propietats.getEntorn())) {
			// Entorn de desenvolupament

			// Navega a la pestanya de documents
			navegarExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT, "documents");
			// Comprova que està pendent de firma i obre la modal
			existeixElementAssert(By.cssSelector(".fa-clock-o.psigna-info")).click();
			// Llegeix l'id de la modal amb la informació del portasignatures
			String psignaDocumentId = existeixElementAssert(By.id("psignaDocumentId")).getText();
			// Tanca la modal
			existeixElementAssert(By.id("psignaTancar")).click();

			// Inicia la crida callback del portasignatures
			
			// Inicialitza l'objecte de paràmetre
			PortaFIBEvent event = new PortaFIBEvent();
			SigningRequest signingRequest = new SigningRequest();
			signingRequest.setID(Long.valueOf(psignaDocumentId));
			signingRequest.setState(3);
			event.setSigningRequest(signingRequest);
			event.setEventTypeID(60);

			// Crida al WS callback del portasignatures
			String urlEndPoint =  propietats.getProperty("app.portasignatures.plugin.url.callback");
			PortaFIBCallBackWs ws = (PortaFIBCallBackWs) WsClientUtils.getWsClientProxy(
					PortaFIBCallBackWs.class,
					urlEndPoint,
					null,
					null,
					"NONE",
					false,
					true,
					true,
					null);
			ws.event(event);
		} else {
			// Entorn de test
			
			String portafibUrl = propietats.getProperty("app.portasignatures.web.url");
			
			// Canviar DNI per 12345678Z
			this.portafibCanviarDni(portafibUrl, "12345678Z");
			
			boolean portafigSignarError = false;
			String portafigSignarErrorMsg = null;
			try {
				// Firma amb l'applet
				this.portafibSignar(portafibUrl, SELENIUM_EXPEDIENT_TITOL_SIGNAT);
			} catch (Exception e) {
				portafigSignarError = true;
				portafigSignarErrorMsg = e.getLocalizedMessage();
			}
			// Restaurar DNI a 43110511R
			this.portafibCanviarDni(portafibUrl, "43110511R");
			
			if (portafigSignarError) {
				fail("Error signant a l'aplicació portasignatures: " + portafigSignarErrorMsg);
			}
		}
	}
	
	/** Mètode per navegar a la pipella de signarues i signar els documents. La finestra
	 * del navegador ha d'estar visible. 
	 * @throws Exception 
	 * @throws  */
	private void portafibSignar(String portafibUrl, String expedientTitol) throws Exception {
		
		// Navega a la pàgina de firmes, les selecciona totes i inicia la firma				
		driver.get(portafibUrl + "/dest/estatDeFirmaPendent/list/1");
		// Marca el checkbox del document per firmar
		String script = "$(\"tr td:contains('" + expedientTitol + "')\").closest('tr').find('input[name=\"selectedItems\"]').prop('checked', true)";
		((JavascriptExecutor) driver).executeScript(script);
		driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();

		// Inicia la signatura amb skuli
	    Screen s = new Screen();
	    s.click(getImatge("firmar.png"));
	    s.wait(getImatge("continuar.png"), 10L).click();
	    s.wait(getImatge("siguiente.png"), 30L).click();
	    s.wait(getImatge("certificado.png"), 10L).click();
	    s.click(getImatge("aceptar.png"));
	    
	    // Espera a que acabi el procés de signatura
	    WebDriverWait wait = new WebDriverWait(driver, 20);
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("FilterButton")));
	}

	private Pattern getImatge(String nom) throws Exception {
		return new Pattern(ImageIO.read(this.getClass().getResourceAsStream("/net/conselldemallorca/helium/test/integracio/portafib/" + nom)));
	}

	/** Mètode privat per canviar el DNI en el portafirmes. */
	private void portafibCanviarDni(String portafibUrl, String dni) {
		driver.get(portafibUrl + "/common/principal.html");
		driver.findElement(By.linkText("Josep Bernadí Gayà Miralles")).click();
		driver.findElement(By.linkText("Configuració")).click();
		
		// Habilita el control del DNI
		String script = "$(\"input[name='usuariPersona.nif']\").removeAttr('readonly')";
		((JavascriptExecutor) driver).executeScript(script);
		
		driver.findElement(By.id("usuariPersona.nif")).clear();
		driver.findElement(By.id("usuariPersona.nif")).sendKeys(dni);

		driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
		
	}	

	
	/** Test per navegar novament a la pestanya de documents de l'expedient i comprovar l'estat
	 * de l'expedient i que existeix el botó per esborrar les signatures.
	 */
	@Test
	public void a06_comprovaEstatIDocumentCorrecte() {
		// Navega a la pestanya de documents
		navegarExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT, "documents");
		// Comprova que l'expedient està en estat finalitzat
		assertTrue("No s'ha trobat l'estat \"Finalitzat\" en el cos de la pàgina",
				driver.findElement(By.tagName("BODY")).getText().contains("Finalitzat"));
		// Comprova que existeix el botó per esborrar les signatures
		existeixElementAssert(By.cssSelector(".fa.fa-ban.fa-stack-2x.text-danger"), "No existeix el botó per eliminar signatures");
	}
	
	/** Test per navegar novament a la pestanya de documents de l'expedient i comprovar l'estat
	 * de l'expedient i que existeix el botó per esborrar les signatures.
	 * Es descarrega el pdf de proves custodiat i comprova que conté la url de verificació de custòdia.
	 */
	@Test
	public void a07_comprovaUrlCustodiaCorrecta() {
		// Navega a la pestanya de documents
		navegarExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT, "documents");
		// Comprova que hi ha l'enllaç amb la url de custòdia com a l'enllaç a amb classe .signature i que no tingui la classe .fa-stack que és el rodol de prohivició
		WebElement aEnllacCustodia = existeixElementAssert(By.cssSelector("a.icon.signature:not(.fa-stack)"));
		String urlCustodia = aEnllacCustodia.getAttribute("href");
		// Verifica que és un enllaç fora d'helium
		assertTrue("L'enllaç a custòdia ha d'apuntar fora d'Helium: " + urlCustodia,
				urlCustodia != null && !urlCustodia.contains("helium"));
		// Descarrega el document firmat i comprova que hi ha inpresa la url de custòdia
		byte[] contingut = downloadFile(By.cssSelector("a[href*='descarregar']"), "document signat");
		assertTrue("El document no pot estar buit", contingut != null && contingut.length > 0);
		// Confirma que el document conté la url de descàrrega incrustada
		try {
			String pdfText = extractPdfText(contingut);
			if (pdfText != null)
				pdfText = pdfText.replace(System.getProperty("line.separator"),"");
			assertTrue("No es troba la url de custòdia \"" + urlCustodia + "\" dins del document",
					pdfText != null && pdfText.contains(urlCustodia));
		} catch(Exception e) {
			e.printStackTrace();
			fail("Error obrint el contingut PDF: " + e.getMessage());
		}
	}	
	
	/** Mètode que rep el contingut d'un document PDF i retorna el text contingut. */
	private static String extractPdfText(byte[] pdfData) throws IOException {
		PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
		try {
			return new PDFTextStripper().getText(pdfDocument);
		} finally {
			pdfDocument.close();
		}
	}

	
	/**  Cridada al WS del Callback de porta firmes amb resposta de rebuig.
	 * Aquest test:
	 * 1- Entra a la pestanya de documents i comprova que està pendent de firma
	 * 2- Agafa les dades del portafirmes i crida al WS de callback del portafirmes
	 * 3- Comprova que l'expedient ha finalitzat correctament
	 */
	@Test
	public void a08_portafirmesRebutjat() throws Exception {
		// Navega a la pestanya de documents
		navegarExpedient(SELENIUM_EXPEDIENT_TITOL_REBUTJAT, "documents");
		// Comprova que està pendent de firma i obre la modal
		existeixElementAssert(By.cssSelector(".fa-clock-o.psigna-info")).click();
		// Llegeix l'id de la modal amb la informació del portasignatures
		String psignaDocumentId = existeixElementAssert(By.id("psignaDocumentId")).getText();
		// Tanca la modal
		existeixElementAssert(By.id("psignaTancar")).click();
		
		// Inicia la crida callback del portasignatures
		
		// Inicia la crida callback del portasignatures
		
		// Inicialitza l'objecte de paràmetre
		PortaFIBEvent event = new PortaFIBEvent();
		SigningRequest signingRequest = new SigningRequest();
		signingRequest.setID(Long.valueOf(psignaDocumentId));
		signingRequest.setState(3);
		signingRequest.setRejectionReason("Prova rebuig");
		event.setSigningRequest(signingRequest);
		event.setEventTypeID(70);

		// Crida al WS callback del portasignatures
		String urlEndPoint =  propietats.getProperty("app.portasignatures.plugin.url.callback");
		PortaFIBCallBackWs ws = (PortaFIBCallBackWs) WsClientUtils.getWsClientProxy(
				PortaFIBCallBackWs.class,
				urlEndPoint,
				null,
				null,
				"NONE",
				false,
				true,
				true,
				null);
		ws.event(event);
	}
	
	/** Test per navegar novament a la pestanya de documents de l'expedient i comprovar l'estat
	 * de l'expedient i que existeix el botó per esborrar les signatures.
	 */
	@Test
	public void a09_comprovaEstatIDocumentRebutjat() {
		// Navega a la pestanya de documents
		navegarExpedient(SELENIUM_EXPEDIENT_TITOL_REBUTJAT, "documents");
		// Comprova que existeix la taula del document
		WebElement taulaDocument = existeixElementAssert(By.cssSelector(".tableDocuments"), "No existeix la taula de documents");
		// Comprova que existeix el botó d'alertes
		WebElement botoAlertesSignatura = null;
		try {
			botoAlertesSignatura = taulaDocument.findElement(By.cssSelector(".psigna-info"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("No s'ha trobat el botó d'alertes sobre el document: " + e.getMessage());
		}
		// Obre la modal i comprova que l'estat és REBUTJAT
		botoAlertesSignatura.click();
		assertTrue("L'estat de la signatura no és 'REBUTJAT'",
					"REBUTJAT".equals(existeixElementAssert(By.id("psignaEstat"), "No es pot llegir l'estat de la signatura").getText()));

	}	
	
	/** Esborra l'expedient creat de portasignatures */
	@Test
	public void a10_esborrarExpedient() {
		if (propietats.isEsborrarExpedientTest())
			esborrarExpedient(SELENIUM_EXPEDIENT_TITOL_SIGNAT);
		if (propietats.isEsborrarExpedientTest())
			esborrarExpedient(SELENIUM_EXPEDIENT_TITOL_REBUTJAT);
	}
	
}
