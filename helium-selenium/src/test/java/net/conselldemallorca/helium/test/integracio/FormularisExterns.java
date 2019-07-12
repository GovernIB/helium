package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import net.conselldemallorca.helium.test.BaseExpedientTest;
import net.consellemallorca.helium.util.ScreenShotOnFailure;

/** Tests per probar el servei d'integració de formularis externs. S'accedeix a l'entorn
 * de test i es crea i configura un expedient de prova per provar formularis externs.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormularisExterns extends BaseExpedientTest {
	
	private static Tomcat mTomcat;

	@Rule
    public ScreenShotOnFailure failure = new ScreenShotOnFailure();
		
	private static final String SELENIUM_FORMEXT_TITOL = "Selenium formext";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BaseExpedientTest.setUpBeforeClass();
		// Arrenca el tomcat amb la aplicació de proves
		if (propietats.getPropertyBool("app.forms.embedded", false)) {
			String mWorkingDir = propietats.getProperty("app.forms.embedded.dir");
			// Inicia el tomcat
			mTomcat = new Tomcat();
			mTomcat.setPort(propietats.getPropertyInt("app.forms.embedded.port", 9080));
			mTomcat.setBaseDir(mWorkingDir);
			mTomcat.getHost().setAppBase(mWorkingDir);
			mTomcat.getHost().setAutoDeploy(true);
			mTomcat.getHost().setDeployOnStartup(true);

			// Fa el deploy de la aplicació war
			String applicationId = propietats.getProperty("app.forms.embedded.applicationid");
			String contextPath = "/" + applicationId;
			File webApp = new File(mWorkingDir, applicationId);
			File oldWebApp = new File(webApp.getAbsolutePath());
			//FileUtils.deleteDirectory(oldWebApp);
			StandardContext ctx = (StandardContext) mTomcat.addWebapp(mTomcat.getHost(), contextPath, webApp.getAbsolutePath());
			// Configura el context
		    ApplicationParameter p = new ApplicationParameter();
		    p.setName("app.base.url");
		    p.setValue(propietats.getBaseUrl());
		    ctx.addApplicationParameter(p);

		    // Inicia el tomcat
			mTomcat.start();
		}
	}
	  
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		BaseExpedientTest.tearDownAfterClass();
		  if (mTomcat.getServer() != null
		            && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
		        if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
		              mTomcat.stop();
		        }
		        mTomcat.destroy();
		    }
	}
	
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
	
	/** Comprova que el tipus d'expedient es pot configurar bé i posa la configuració
	 * correcta per integrar amb la aplicació de test de helium-selenium-webapp.
	 * - Accedeix al tipus d'expedient de test
	 * - Accedeix a la pestanya d'integració amb forms.
	 * - Desmarca la opció i guarda.
	 * - Marca la opció, omple els camps i guarda.
	 */
	@Test
	public void a0_ConfiguracioTipusExpedient() {
		
		// Navega a la pestanya de integració amb forms
		navegarTipusExpedient("pipella-integracio-forms");
		// Desmarca actiu
		existeixElementAssert(By.id("actiu"), "No existeix la casella actiu");
		WebElement actiu = driver.findElement(By.id("actiu"));
		if (actiu.isSelected())
			actiu.click();
		// Guarda
		driver.findElement(By.cssSelector(".btn.btn-primary.right")).click();
		existeixElementAssert(By.className("alert-success"), "Error guardant formularis externs");
		// Omple
		if (!actiu.isSelected())
			actiu.click();
		existeixElementAssert(By.id("url"));
		existeixElementAssert(By.id("usuari"));
		existeixElementAssert(By.id("contrasenya"));
		String[] forms = propietats.getIntegracioFormsPropietats();
		driver.findElement(By.id("url")).clear();
		driver.findElement(By.id("url")).sendKeys(forms[0]);
		if (forms[1] != null)
			driver.findElement(By.id("usuari")).sendKeys(forms[1]);
		else
			driver.findElement(By.id("usuari")).clear();
		if (forms[2] != null)
			driver.findElement(By.id("contrasenya")).sendKeys(forms[2]);
		else
			driver.findElement(By.id("contrasenya")).clear();		
		// Tanca el missatge que ha anat bé
		if (existeixElement(By.cssSelector("#contingut-alertes.close-alertes")))
			driver.findElement(By.cssSelector("#contingut-alertes.close-alertes")).click();
		// Guarda
		driver.findElement(By.cssSelector(".btn.btn-primary.right")).click();
		// Verifica que s'ha guardat correctament
		existeixElementAssert(By.cssSelector("#contingut-alertes div.alert-success"), "Error guardant formularis externs");
	}	
		
	/** Comprova que el tipus d'expedient es pot configurar bé i posa la configuració
	 * correcta per integrar amb la aplicació de test de helium-selenium-webapp.
	 * - Navega a la definició de procés de test.
	 * - Navega fins la tasca de formularis externs.
	 * - Configura el formulari extern i guarda els canvis.
	 */
	@Test
	public void a1_ConfiguracioDefinicioProces() {
		// Navega a la pestanya de integració amb forms
		navegarDefinicioProces("pipella-tasques");
		// Cerca el caixetí de filtre del datatable
		existeixElementAssert(By.id("definicioProcesTasques_filter"), "No existeix el filtre de tipus d'expedient");
		// Filtra pel títol de l'expedient
		driver.findElement(By.cssSelector("#definicioProcesTasques_filter .input-sm")).sendKeys("formext");
		existeixElementAssert(By.cssSelector(".btn.btn-default .fa-pencil"), "No hi ha resultats a la taula tasques de la definicio de proces");
		waitPaginaCarregada();
		// Troba el botó de modificar
		WebElement botoModificar = null;
		String urlModificacio = null;
		for (WebElement boto : driver.findElements(By.cssSelector("a.btn.btn-default")))
			if (boto.getText().contains("Modificar")) {
				botoModificar = boto;
				urlModificacio = boto.getAttribute("href").replace(propietats.getTestBaseUrl(), "");
				break;
			}
		assertNotNull("No s'ha pogut trobar el botó per modificar la tasca", botoModificar);
		// Clica sobre el botó de modificar
		botoModificar.click();
		// Espera que es carregui la pàgina de l'expedient
		modalObertaAssert(urlModificacio);
		// Passa a la modal
		vesAModal(urlModificacio);
		assertTrue("El valor codi difereix de formext", "formext".equals(existeixElementAssert(By.id("jbpmName")).getAttribute("value")));
		// Omple el valor del camp
		driver.findElement(By.id("formExtern")).clear();
		driver.findElement(By.id("formExtern")).sendKeys("formExt");
		// Guarda els canvis
		driver.findElement(By.cssSelector("form")).submit();
		tornaAPare();
		waitPaginaCarregada();
		existeixElementAssert(By.className("alert-success"), "Error guardant la tasca de la definició de procés");
	}		
	
	/** Un cop configurat el tipus d'expedient i la definició de procés prova de crear un nou expedient.
	 * - Crea un nou expedient de test de funcionalitats amb la opció de formularis externs.
	 * - Inicia la tasca i obre el formulari extern
	 * - Guarda les dades i comprova que les dades es guarden.
	 */
	@Test
	public void a2_CreacioExpedientFormularisExterns() {
		// Si ja existeix l'esborra
		esborrarExpedient(SELENIUM_FORMEXT_TITOL);
		// Crea l'expedient amb la opció de formularis externsl porta signatures
		crearExpedient(SELENIUM_FORMEXT_TITOL, "formext");
	}
	
	/** Comprova que es pot accedir a l'expedient, a la tramitació de la tasca, al formulari extern i que
	 * es guarden les dades correctament
	 */
	@Test
	public void a3_InicarFormulariExtern() throws Exception {
		// Navega a la pestanya de tasques
		navegarExpedient(SELENIUM_FORMEXT_TITOL, "tasques");
		
		// Cerca la fila amb la tasca del formext
		WebElement taulaTasques = existeixElementAssert(By.id("tasques-pendents-meves"), "No existeix la taula de tasques");
		String tascaId = null;
		for (WebElement tr : taulaTasques.findElements(By.cssSelector("tr")))
			if (tr.getText().contains("formext")) {
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
		// Passa a la finestra modal de tramitació de la tasca
		modalObertaAssert(tascaId);
		vesAModal(tascaId);
		// Prem el botó del formulari extern
		WebElement obrirFormularisExternsBtn = existeixElementAssert(By.id("boto-formext"), "No existix el botó per obrir els formularis externs");
		waitPaginaCarregada();
		obrirFormularisExternsBtn.click();
		// Passa al formulari extern amb part de la ruta que retorna el WS d'inici del formulari extern
		WebElement formulariExtern = existeixElementAssert(By.cssSelector("div.ui-dialog"), "No existeix la finestra amb el contingut del formulari extern");
		vesAModal("integracio/formulari");
		// Informa els valors dels camps del formulari extern
		// valor_0 corresponent a la variable var_txt1
		existeixElementAssert(By.id("valor_0")).clear();
		driver.findElement(By.id("valor_0")).sendKeys("valor txt");
		// Accepta el formulari extern
		driver.findElement(By.cssSelector("button[type='submit']")).click();
		tornaAPare();
		waitPaginaCarregada();
		// Torna a la modal de tramitació de la tasca i tanca el formulari extern
		vesAModal(tascaId);
		formulariExtern.findElement(By.cssSelector(".ui-icon-closethick")).click();
		waitPaginaCarregada();
		// Comprova que el valor es troba en els camps de la tasca
		System.out.println("Valor: " + existeixElementAssert(By.id("var_txt1")).getAttribute("value"));
		assertTrue(	"El valor de la variable var_txt1 no s'ha fixat correctament",
					"valor txt".equals(existeixElementAssert(By.id("var_txt1")).getAttribute("value"))); 
		// torna fora de la modal i prem finalitzar la tramitació de la tasca
		tornaAPare();
		existeixElementAssert(By.id("tasca-boto-completar-Finalitzar")).click();
		// Accepta l'avís de finalització de la tasca
		if (isAlertPresent()) {
			acceptarAlerta();
		}		
	}
	
	/** Esborra l'expedient creat de formularis externs*/
	@Test
	public void a4_EsborrarExpedientFormularisExterns() {
		if (propietats.isEsborrarExpedientTest())
			esborrarExpedient(SELENIUM_FORMEXT_TITOL);
	}	
}
