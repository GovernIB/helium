package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProces extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExportDefProc = carregarPropietat("defproc.deploy.export.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntornTest(entorn, titolEntorn, usuari);
		crearTipusExpedientTest(nomTipusExp, codTipusExp);
	}
	
	@Test
	public void b_desplegarParExp() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, nomTipusExp, pathDefProc, null, false, true);
		eliminar(true);
	}
	
	@Test
	public void c_desplegarPar() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, false, true);
		eliminar(true);
	}
	
	@Test
	public void d_desplegarParEtiqueta() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, "Etiqueta", false, true);
		eliminar(true);
	}
	
	@Test
	public void e_desplegarParActualitzar() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, true, true);
		eliminar(true);
	}
	
	@Test
	public void f_desplegarExp() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathExportDefProc, null, false, true);
	}
	
	@Test
	public void g_eliminarDefProc() {
		carregarUrlConfiguracio();
		eliminar(false);
	}
	
	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntornTest(entorn, usuari, codTipusExp);
	}
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
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
