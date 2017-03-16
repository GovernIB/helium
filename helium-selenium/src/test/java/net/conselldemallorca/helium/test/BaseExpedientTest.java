package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/** Classe base amb mètodes per treballar amb el tipus d'expedient Test
 * que conté els casos de proves per a integracions i altres.
 */
@Ignore("Classe base per a la execució dels tests amb Selenium")
public class BaseExpedientTest extends BaseTestSelenium{

	// Mètodes d'ajuda a la navegació.
		
	/** Carrega l'entorn de proves configurat per propietats. */
	protected void navegarEntornTest() {
		String titolEntorn	= propietats.getEntornTestTitol();
		// Comprova si ja està carregat a la capçalera
		String entornActual = driver.findElement(By.id("entornActualNom")).getText();
		if (!titolEntorn.equals(entornActual)) {
			driver.findElement(By.id("menuEntorns")).click();
			driver.findElement(By.linkText(titolEntorn)).click();
			// Espera que es carregui la pàgina
			waitPaginaCarregada();

			//			existeixElementAssert(By.id("taulaDades_info"), "No es carrega la taula");
			
			// Comprova que s'hagi actualitzat l'entorn
			assertTrue(titolEntorn.equals(driver.findElement(By.id("entornActualNom")).getText()));
		}
	}	

	/** Carrega el tipus d'expedient configurat per propietats.
	 * @param pipellaId Si s'informa es clicarà sobre la pipella per carregar-la. 
	 * @throws InterruptedException */
	protected void navegarDefinicioProces(String pipellaId) {
		navegarEntornTest();
		String definicioProcesCodi = propietats.getDefinicioProcesCodi();
		driver.get(propietats.getTestBaseUrl() + "/definicioProces/" + definicioProcesCodi);
		waitPaginaCarregada();
		// Si s'ha passat un id de pipella com a paràmetre la acciona
		if (pipellaId != null) {
			// Comprova que existeix la pipella d'integracions i la acciona
			existeixElementAssert(By.id(pipellaId), "No existeix la pipella " + pipellaId);
			driver.findElement(By.id(pipellaId)).click();
			// Espera que es carregui la pàgina
			waitPaginaCarregada();
		}
	}
	
	/** Carrega la definició de procés configurada per propietats.
	 * @param pipellaId Si s'informa es clicarà sobre la pipella per carregar-la. */
	protected void navegarTipusExpedient(String pipellaId) {
		navegarEntornTest();
		// Navega a la pàgina dels tipus d'expedient
		existeixElementAssert(By.id("menuDisseny"), "No existeix el menú de disseny").click();
		existeixElementAssert(By.id("menuTipusExpedient"), "No existeix el menú de tipus d'expedient").click();
		// Espera que es carregui la pàgina
		waitPaginaCarregada();
		// Cerca el caixetí de filtre del datatable
		existeixElementAssert(By.id("expedientTipus_filter"), "No existeix el filtre de tipus d'expedient");
		// Filtra pel títol de l'expedient
		String expedientTipusTitol = propietats.getTipusExpedientTitol();
		driver.findElement(By.cssSelector("#expedientTipus_filter .input-sm")).sendKeys(expedientTipusTitol);
		existeixElementAssert(By.id("row_0"), "No hi ha resultats a la taula de tipus d'expedients");
		waitPaginaCarregada();
		// Navega a la primera fila trobada
		driver.findElement(By.id("row_0")).click();
		// Espera que es carregui la pàgina de l'expedient
		waitPaginaCarregada();
		// Si s'ha passat un id de pipella com a paràmetre la acciona
		if (pipellaId != null) {
			// Comprova que existeix la pipella d'integracions i la acciona
			existeixElementAssert(By.id(pipellaId), "No existeix la pipella " + pipellaId).click();
			// Espera que es carregui la pàgina
			waitPaginaCarregada();
		}
	}	
	
	/** Navega a l'expedient filtrant pel títol i accedint a la pestanya especificada */
	protected void navegarExpedient(String titol, String pipella) {
		// Navega als expedients
		driver.findElement(By.id("menuExpedients")).click();
		waitPaginaCarregada();
		// Filtra per títol i accedeix al menú de gestió
		driver.findElement(By.id("titol")).clear();
		driver.findElement(By.id("titol")).sendKeys(titol);
		driver.findElement(By.id("consultar")).click();
		waitPaginaCarregada();
		// Prem els botons de gestionar
		driver.findElement(By.id("taulaDades")).findElement(By.cssSelector(".btn.btn-primary")).click();
		driver.findElement(By.id("taulaDades")).findElement(By.cssSelector(".consultar-expedient")).click();
		waitPaginaCarregada();			
		// Accedeix a la pipella de tasques de l'expedient
		// Comprova que existeix la pipella d'integracions i la acciona
		existeixElementAssert(By.id("pipella-" + pipella), "No existeix la pipella de " + pipella).click();
		waitPaginaCarregada();
	}	
	
	
	/** Mètode per crear l'expedient per a les proves  passant el títol i el tipus per escollir
	 * en el desplegable d'opcions inicials de l'expedient de tipus test.
	 * */
	protected void crearExpedient(String titol, String tipus) {
		// Guarda la ruta a la modal de nou expedient
		String urlNouExpedient = "/expedient/iniciar";
		// Prem el botó de nou expedient
		driver.findElement(By.id("iniciar-expediente")).click();
		// Espera que es carregui la pàgina de nou expedient
		modalObertaAssert(urlNouExpedient);
		// Passa a la modal
		vesAModal(urlNouExpedient);
		waitPaginaCarregada();
		// Troba la línia on hi ha el text del codi de l'expedient
		for(WebElement tr : driver.findElements(By.cssSelector("#registre tr"))) 
			if (tr.getText().contains(propietats.getTipusExpedientCodi())) {
				// Inicia l'expedident
				tr.findElement(By.cssSelector(".form-init-exedient")).submit();
				break;
			}
		waitPaginaCarregada();
		// Selecciona el tipus de test en el select2
		String script = "$('#tipus_test').val('" + tipus + "').change()";
		((JavascriptExecutor) driver).executeScript(script);
		// Prem el botó iniciar
		tornaAPare();
		existeixElementAssert(By.id("iniciar"));
		String inciarScript = "$('#iniciar').click()";
		((JavascriptExecutor) driver).executeScript(inciarScript);
		// Informa del títol
		vesAModal(urlNouExpedient);
		existeixElementAssert(By.id("titol")).sendKeys(titol);
		// Accepta el formulari
		((JavascriptExecutor) driver).executeScript(inciarScript);
		// Accepta l'avís de creació
		if (isAlertPresent()) {
			acceptarAlerta();
		}
		// Torna al pare i comprova que l'expedient s'ha creat correctament
		tornaAPare();
		waitPaginaCarregada();
		existeixElementAssert(By.className("alert-success"), "Error creant el nou expedient");			
	}
	
	
	/** Retorna el path cap al fitxer blank.pdf. Primer mira si existeix, si no el guarda des del
	 * classpath cap al directori temporal.
	 * 
	 * @return
	 */
	protected String getBlankFilePath() {
		
		// Ruta destí a la carpeta selenium del directori temporal
		String folderPath = System.getProperty("java.io.tmpdir") + "selenium";
		String filePath = folderPath + File.separator + "blank.pdf";
		// Mira si existeix, si no el crea
		File file = new File(filePath);
		if (!file.exists()) {
			// Mira si crear el directori
			File folder = new File(folderPath);
			if (!folder.exists())
				folder.mkdirs();
			// Copia el contingut de l'arxiu del .jar al temporal
			InputStream stream = null;
	        OutputStream resStreamOut = null;
	        try {
	    		String resourceName = "/net/conselldemallorca/helium/test/util/blank.pdf";
	            stream = this.getClass().getResourceAsStream(resourceName);
	            if(stream == null) {
	            	fail("No es pot llegir el fitxer de proves del .jar " + resourceName);
	            }
	            int readBytes;
	            byte[] buffer = new byte[4096];
	            resStreamOut = new FileOutputStream(filePath);
	            while ((readBytes = stream.read(buffer)) > 0) {
	                resStreamOut.write(buffer, 0, readBytes);
	            }
	        } catch (Exception e) {
	            fail("Error creant el fitxer blank.pdf de proves al directori temporal: " + e.getMessage());
	        } finally {
	        	try {
	        		if (stream != null)
	        			stream.close();
	        		if (resStreamOut != null)
	        			resStreamOut.close();
	        	} catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        }     
	    }
		return filePath;		
	}
}