package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProces extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		crearEntornTest(entorn, titolEntorn, usuari);
		crearTipusExpedientTest(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void b_desplegarParExp() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		desplegar(true);
		eliminar(true);
	}
	
	@Test
	public void c_desplegarPar() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		desplegar(false);
	}
	
	@Test
	public void d_eliminarDefProc() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		eliminar(false);
	}
	
	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		eliminarEntornTest(entorn, usuari, codTipusExp);
	}
	
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	private void desplegar(boolean tipExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
		actions.click();
		actions.build().perform();
		
		// Deploy
		driver.findElement(By.xpath("//option[@value='JBPM']")).click();
		
		// tipus d'expedient
		if (tipExp) {
			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
				if (option.getText().equals(nomTipusExp)) {
					option.click();
					break;
				}
			}
		}		
		
		driver.findElement(By.id("arxiu0")).sendKeys(pathDefProc);
		screenshotHelper.saveScreenshot("defproces/importPar/" + (tipExp ? "tipusExp" : "global") + "/1_importPar.png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		screenshotHelper.saveScreenshot("defproces/importPar/" + (tipExp ? "tipusExp" : "global") + "/2_importPar.png");
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (tipExp) {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][text() = '" + nomTipusExp + "']]", "defproces/importPar/tipusExp/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés a nivell de tipus d'expedient");
		} else {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "defproces/importPar/global/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés a nivell global");
		}
	}
	
	private void eliminar(boolean continuar) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("defproces/1_elimDefProc.png");
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "defproces/importPar/tipusExp/3_definicionsExistents.png")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[4]/a")).click();
			acceptarAlerta();
			screenshotHelper.saveScreenshot("defproces/2_elimDefProc.png");
		} else 	{
			if (!continuar)
				fail("La definició de procés no existeix");
		}
	}
	
	// Inicialitzacions
	
	protected void crearEntornTest(String entorn, String titolEntorn, String usuari) {
		entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		// Crear entorn
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).clear();
			driver.findElement(By.id("codi0")).sendKeys(entorn);
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(titolEntorn);
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha pogut crear l'entorn");
		}
		// Assignar permisos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")) {
			driver.findElement(By.id("nom0")).sendKeys(usuari);
			driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
			driver.findElement(By.xpath("//input[@value='ORGANIZATION']")).click();
			driver.findElement(By.xpath("//input[@value='READ']")).click();
			driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut assignar permisos");
		}
		// Marcar entorn per defecte
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		String src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a/img")).getAttribute("src");
		if (!src.endsWith("star.png")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a")).click();
		}
		// Selecció entorn
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]/a")));
		actions.click();
		actions.build().perform();
	}
	
	protected void eliminarEntornTest(String entorn, String usuari, String codiTipusExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]/td[4]/a")).click();
			acceptarAlerta();
		}
		//Entorn actual per defecte
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		if (!driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a/img")).getAttribute("src").endsWith("star.png")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a")).click();
		}
		
		// Eliminam l'entorn de test
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
			acceptarAlerta();
			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha pogut eliminar l'entorn de proves");
		}
	}
	
	protected void crearTipusExpedientTest(String nom, String codi) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys(codi);
			driver.findElement(By.id("nom0")).sendKeys(nom);
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			actions.moveToElement(driver.findElement(By.id("menuDisseny")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
			actions.click();
			actions.build().perform();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut crear el tipus d'expedient de test");
		}
	}
	
	protected void seleccionarTipExp(String codTipExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]", "No s'ha trobat el tips d'expedient");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[1]/a")).click();
	}
}
