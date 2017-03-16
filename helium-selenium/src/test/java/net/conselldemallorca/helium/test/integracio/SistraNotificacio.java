package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import net.conselldemallorca.helium.test.BaseExpedientTest;
import net.consellemallorca.helium.util.ScreenShotOnFailure;

/** Tests per comprovar la integració amb Sitra referent a les notificacions.
 * El sistema de sistra ofereix la possibilitat de notificar al ciutadà l'estat
 * d'un tràmit. Per fer-ho s'iniciarà un expedient de tipus "Test" amb la opció de
 * "Notificació electrònica" i en avançar la tasca de la definició de procés
 * farà la invocació al handler per a que es notifiqui. Es comprovarà que ha anat
 * bé anant a les propietats de l'expedient i comprovant que s'ha notificat.
 * 1- Crear expedient de tipus "Test"
 * 2- Avançar la tasca que invoca el handler de notificació
 * 3- Comprovar que s'ha notificat
 * 4- Esborrar expedient
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SistraNotificacio extends BaseExpedientTest {
	
	@Rule
    public ScreenShotOnFailure failure = new ScreenShotOnFailure();
	
	/** Títol utilitzat per a l'expedient del portafirmes. */
	private static final String SELENIUM_REGISTRE_ENTRADA_TITOL = "Selenium notificació electrònica";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Configura la rule de captura de pantalles en cas d'error
		failure.setDriver(driver);		
		failure.setScreenshotHelper(screenshotHelper);		
		// Obre el navegador en la pàgina inicial i a l'entorn de test
		carregarPaginaIncial();
		navegarEntornTest();		
	}	
	
	/** Crea l'expedient de tipus portafirmes
	 */
	@Test
	public void a1_crearExpedient() throws Exception {
		// Si ja existeix l'expedient, l'esborra
		esborrarExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL);
		// Crea l'expedient amb la opció del porta signatures
		crearExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL, "notificacio");
	}
	
	/** Entra a la pestanya de tasques, la obre, valida les dades, informa el document
	 *  i finalitza la tasca per a que la notificació s'enviï.
	 */
	@Test
	public void a2_finalitzarTascaNotificacio() throws Exception {
		
		String botoFinalitzarId = "tasca-boto-completar-Finalitzar";
		
		// Navega a la pestanya de tasques
		navegarExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL, "tasques");

		// Comprova que existeix la taula de tasques
		WebElement taulaTasques = existeixElementAssert(By.id("tasques-pendents-meves"), "No existeix la taula de tasques");
		// Cerca la fila amb la tasca del registre
		String tascaId = null;
		for (WebElement tr : taulaTasques.findElements(By.cssSelector("tr")))
			if (tr.getText().contains("notificacio")) {
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
		existeixElementAssert(By.id("notificacio_assumpte_tipus"));
		
		// Prem Validar les dades que ja estan introduïdes
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

	/** Test per comprovar que la notificació s'ha enregistrat bé i que apareix a la pestanya
	 * de notificacions de l'expedient
	 */
	@Test
	public void a3_comprovaNotificacioEnviada() {
		// Navega a la pestanya de documents
		navegarExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL, "notificacions");
		// Comprova que existeix la notificació
		assertTrue("No es pot comprovar que la notificació està enviada, text \"ENVIAT\" no trobat",
				existeixElementAssert(By.cssSelector(".tableNotificacions")).getText().contains("ENVIAT"));	
		// Aquí ja estaria comprovat èro es completa obrint la modal d'informació de la notificació i tornant a comprovar
		// Prem el botó d'accions
		driver.findElement(By.cssSelector(".tableNotificacions")).findElement(By.cssSelector("button")).click();
		// Prem el botó d'informació
		existeixElementAssert(By.id("notificacioInfo")).click();
		// Passa a la finestra modal d'informació de la notificació
		modalObertaAssert("notificacio");
		vesAModal("notificacio");
		assertTrue("No es troba el text \"ENVIAT\" a la modal d'informació de la notificació",
				existeixElementAssert(By.id("dades_generals")).getText().contains("ENVIAT"));	
	}
	
	/** Esborra l'expedient creat de portasignatures */
	@Test
	public void a4_esborrarExpedient() {
		if (propietats.isEsborrarExpedientTest())
			esborrarExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL);
	}
}
