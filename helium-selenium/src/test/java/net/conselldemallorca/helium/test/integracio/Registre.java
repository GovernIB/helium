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

/** Tests per comprovar la integració amb el registre.
 *  La entrada i sortida de documents es fa amb el plugin del registre
 *  i de plugins de registre hi ha una gran varietat, així que només queda
 *  verficar que es comporti bé amb la crida i errors del registre.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Registre extends BaseExpedientTest {
	
	@Rule
    public ScreenShotOnFailure failure = new ScreenShotOnFailure();
	
	/** Títol utilitzat per a l'expedient del registre d'entrada. */
	private static final String SELENIUM_REGISTRE_ENTRADA_TITOL = "Selenium registre entrada";
	/** Títol utilitzat per a l'expedient del registre de sortida. */
	private static final String SELENIUM_REGISTRE_SORTIDA_TITOL = "Selenium registre sortida";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Configura la rule de captura de pantalles en cas d'error
		failure.setScreenshotHelper(screenshotHelper);		
		failure.setDriver(driver);		
		// Obre el navegador en la pàgina inicial i a l'entorn de test
		carregarPaginaIncial();
		navegarEntornTest();		
	}	
	
	/** Obre la tasca de l'expedient del registre i finalitza la tasca amb una entrada.
	 */
	@Test
	public void a1_enregistrarEntradaDocument() throws Exception {
		// Crea l'expedient per a l'entrada i tramita la  tasca
		enregistrarDocument(
				SELENIUM_REGISTRE_ENTRADA_TITOL, 
				"tasca-boto-completar-entrada");
		// Verifica la informació del registre
		verificarInformacioRegistre("Entrada");		
	}
	
	/** Obre la tasca de l'expedient del registre i finalitza la tasca amb una entrada.
	 */
	@Test
	public void a2_enregistrarSortidaDocument() throws Exception {
		// Crea l'expedient per a la sortida i tramita la  tasca
		enregistrarDocument(
				SELENIUM_REGISTRE_SORTIDA_TITOL, 
				"tasca-boto-completar-sortida");
		// Verifica la informació del registre
		verificarInformacioRegistre("Sortida");		
	}
	
	/** Esborra l'expedient creat de formularis externs*/
	@Test
	public void a3_EsborrarExpedientsRegistre() {
		if (propietats.isEsborrarExpedientTest()) {
			esborrarExpedient(SELENIUM_REGISTRE_ENTRADA_TITOL);
			esborrarExpedient(SELENIUM_REGISTRE_SORTIDA_TITOL);
		}
	}
	
	private void enregistrarDocument(String titol, String botoFinalitzarId) {

		// Si ja existeix l'expedient, l'esborra
		esborrarExpedient(titol);
		// Crea l'expedient
		crearExpedient(titol, "registre");
		//crearExpedientRegistre(titol);

		// Navega als expedients
		navegarExpedient(titol, "tasques");
		// Comprova que existeix la taula de tasques
		WebElement taulaTasques = existeixElementAssert(By.id("tasques-pendents-meves"), "No existeix la taula de tasques");
		// Cerca la fila amb la tasca del registre
		String tascaId = null;
		for (WebElement tr : taulaTasques.findElements(By.cssSelector("tr")))
			if (tr.getText().contains("registrar")) {
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
		existeixElementAssert(By.id("registre_oficina"));
		
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

		// Torna fora de la modal i prem Validar les dades que ja estan introduïdes
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
	
	/** Mètode per navegar a la pestanya de documents i verificar la informació del registre del document
	 * 
	 * @param entradaSortida Es compara el text. Si és entrada en el formulari apareixerà "Entrada", si 
	 * és sortida apareixerà "Sortida".
	 */
	private void verificarInformacioRegistre(String entradaSortida) {
		// Comprova que existeix la pipella d'integracions i la acciona
		existeixElementAssert(By.id("pipella-documents"), "No existeix la pipella de documents").click();
		waitPaginaCarregada();
		// Prem sobre l'enllaç d'informació del registre
		existeixElementAssert(By.cssSelector(".registre")).click();
		// Va a la modal
		vesAModal("registre/verificar");
		
		// Recull la informació mostrada per pantalla i verifica
		String registreOficinaNom = existeixElementAssert(By.id("registreOficinaNom")).getText();
		assertTrue("El nom de la oficina no s'ha consultat correctament",  
				registreOficinaNom != null && "".compareTo(registreOficinaNom != null? registreOficinaNom : "") != 0);
		String registreData = existeixElementAssert(By.id("registreData")).getText();
		assertNotNull("La data del registre no s'ha informat correctament", registreData);
		String registreEntrada = existeixElementAssert(By.id("registreEntrada")).getText();
		assertTrue("El tipus de registre no és el correcte, s'esperava \""+entradaSortida+"\" i s'ha rebut\""+registreEntrada+"\"", 
				entradaSortida.equals(registreEntrada != null? registreEntrada : ""));
		String registreNumero = existeixElementAssert(By.id("registreNumero")).getText();
		assertNotNull("El número de registre no s'ha informat correctament", registreNumero);		
	}
}
