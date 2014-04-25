package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesVars extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietat("defproc.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
//	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;
	
	static enum TipusVar {
		STRING,
		INTEGER,
		FLOAT,
		BOOLEAN,
		TEXTAREA,
		DATE,
		PRICE,
		TERMINI,
		SEL_ENUM,
		SEL_DOMINI,
		SEL_INTERN,
		SEL_CONSULTA,
		SUGGEST,
		ACCIO
	}
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		crearEntornTest(entorn, titolEntorn, usuari);
		crearEnumeracionsTest();
//		crearTipusExpedientTest(nomTipusExp, codTipusExp);
//		crearDominisTest();
	}
	
	@Test
	public void b_crearVarString() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				false,
				false);
	}
	
	@Test
	public void c_crearVarInteger() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.int.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.int.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.INTEGER,
				false,
				false);
	}
	
	@Test
	public void d_crearVarFloat() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.float.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.float.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.FLOAT,
				false,
				false);
	}
	
	@Test
	public void e_crearVarBoolean() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.bool.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.bool.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.BOOLEAN,
				false,
				false);
	}
	
	@Test
	public void f_crearVarTextArea() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.tarea.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.tarea.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TEXTAREA,
				false,
				false);
	}
	
	@Test
	public void g_crearVarDate() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.date.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.date.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.DATE,
				false,
				false);
	}
	
	@Test
	public void h_crearVarPrice() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.price.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.price.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.PRICE,
				false,
				false);
	}
	
	@Test
	public void i_crearVartermini() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.term.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.term.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TERMINI,
				false,
				false);
	}
	
	@Test
	public void j_crearVarSelEnum() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.enum.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.enum.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.SEL_ENUM,
				false,
				false);
	}
	
	@Test
	public void k_crearVarStringMultiple() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				true,
				false);
	}
	
	@Test
	public void k_crearVarStringOculta() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				false,
				true);
	}
	
	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		eliminarEnumeracionsTest();
		eliminarEntornTest(entorn, usuari, codTipusExp);
//		eliminarDominisTest();
	}
	
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	private void crearVar(String codi, String nom, TipusVar tipùs, boolean multiple, boolean oculta) {
		
	}
//	private void desplegar(boolean tipExp) {
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
//		actions.click();
//		actions.build().perform();
//		
//		// Deploy
//		driver.findElement(By.xpath("//option[@value='JBPM']")).click();
//		
//		// tipus d'expedient
//		if (tipExp) {
//			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
//				if (option.getText().equals(nomTipusExp)) {
//					option.click();
//					break;
//				}
//			}
//		}		
//		
//		driver.findElement(By.id("arxiu0")).sendKeys(pathDefProc);
//		screenshotHelper.saveScreenshot("defproces/importPar/" + (tipExp ? "tipusExp" : "global") + "/1_importPar.png");
//		
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		screenshotHelper.saveScreenshot("defproces/importPar/" + (tipExp ? "tipusExp" : "global") + "/2_importPar.png");
//		
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		if (tipExp) {
//			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][text() = '" + nomTipusExp + "']]", "defproces/importPar/tipusExp/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés a nivell de tipus d'expedient");
//		} else {
//			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "defproces/importPar/global/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés a nivell global");
//		}
//	}
	
	
//	private void eliminar(boolean continuar) {
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//
//		screenshotHelper.saveScreenshot("defproces/1_elimDefProc.png");
//		
//		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "defproces/importPar/tipusExp/3_definicionsExistents.png")) {
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[4]/a")).click();
//			acceptarAlerta();
//			screenshotHelper.saveScreenshot("defproces/2_elimDefProc.png");
//		} else 	{
//			if (!continuar)
//				fail("La definició de procés no existeix");
//		}
//	}
	
	
	
//	private void crearEntornTest() {
//		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
//			driver.findElement(By.id("codi0")).clear();
//			driver.findElement(By.id("codi0")).sendKeys(entorn);
//			driver.findElement(By.id("nom0")).clear();
//			driver.findElement(By.id("nom0")).sendKeys(titolEntorn);
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "entorns/crear/3_entornCrearActiu_02.png", "No s'ha pogut crear l'entorn");
//		}
//		
//		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L'entorn de proves no existeix.");
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
//		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")) {
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]/td[4]/a")).click();
//			acceptarAlerta();
//			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut eliminar els permisos");
//		}
//		driver.findElement(By.id("nom0")).sendKeys(usuari);
//		driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
//		driver.findElement(By.xpath("//input[@value='ORGANIZATION']")).click();
//		driver.findElement(By.xpath("//input[@value='READ']")).click();
//		driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut assignar permisos");
//	}
	
	
//	private void eliminaPermisos(String tipus, String usurol) {
//		// Anem a la pantalla de configuració d'entorns
//		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		
//		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/1_entornsExistents_01.png");
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "L'entorn de proves no existeix.");
//		
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
//		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/2_permisos.png");
//		
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]", usurol + " no té permisos per aquest entorn.");
//		// eliminam els permisos actuals
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]/td[4]/a")).click();
//		acceptarAlerta();
//		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/3_permisos.png");
//		
//		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usurol + "')]", "No s'han pogut eliminar els permisos");
//	}

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
		// Importar entorn	
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]")).click();
		existeixElementAssert("h3[@class='titol-tab titol-delegacio']/img", "No s'ha trobat la secció d'importació");
		driver.findElement(By.xpath("h3[@class='titol-tab titol-delegacio']/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(pathExportEntorn);
		driver.findElement(By.xpath("//*[@id='commandImportacio']//button")).click();
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
	
	protected void crearEnumeracionsTest() {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys("enumsel");
			driver.findElement(By.id("nom0")).sendKeys("Enumerat selenium");
	  	    driver.findElement(By.xpath("//button[@value='submit']")).click();
	  	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'ha pogut crear la enumeració");
		}
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[3]/form/button")).click();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")) {
			driver.findElement(By.id("codi0")).sendKeys("A");
			driver.findElement(By.id("nom0")).sendKeys("Tipus A");
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'A')]", "No s'han pogut crear els elements de la enumeració");
		}
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'B')]")) {
			driver.findElement(By.id("codi0")).sendKeys("B");
			driver.findElement(By.id("nom0")).sendKeys("Tipus B");
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'B')]", "No s'han pogut crear els elements de la enumeració");
		}
	}
	
	protected void eliminarEnumeracionsTest() {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[4]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'han pogut eliminar l'enumeració");
	}
}
