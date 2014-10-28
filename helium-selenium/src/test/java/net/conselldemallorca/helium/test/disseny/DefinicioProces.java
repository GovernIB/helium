package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
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
	String pathDefProc = carregarPropietatPath("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathExportDefProc = carregarPropietatPath("defproc.deploy.export.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntornTest(entorn, titolEntorn, usuari);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void b_desplegarParExp() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, nomTipusExp, pathDefProc, null, false, true);
		eliminar(false);
	}
	
	@Test
	public void c_desplegarPar() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, false, true);
		eliminar(false);
	}
	
	@Test
	public void d_desplegarParEtiqueta() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, "Etiqueta", false, true);
		eliminar(false);
	}
	
	@Test
	public void e_desplegarParActualitzar() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, true, true);
		eliminar(false);
	}
	
	@Test
	public void f_desplegarExp() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.EXPORTDEFPRC, nomDefProc, null, pathExportDefProc, null, false, true);
	}
	
	@Test
	public void g_eliminarVersioDefProc() {
		carregarUrlConfiguracio();
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, false, true);
		
		seleccionarDefinicioProces(nomDefProc);
		Integer versioActual = 0;
		try {
			versioActual = Integer.parseInt(driver.findElement(By.xpath("//*[@id='content']/dl/dd[4]")).getText());
		} catch (Exception e) {
			fail("No s'ha pogut obtenir la versió actual de la definició de procés");
		}
		existeixElementAssert("//*[@id='content']/div[2]/form[2]/button", "No s'ha trobat el botó per a borrar la versió");
		driver.findElement(By.xpath("//*[@id='content']/div[2]/form[2]/button")).click();
		acceptarAlerta();
		seleccionarDefinicioProces(nomDefProc);
		Integer versioNova = 0;
		try {
			versioNova = Integer.parseInt(driver.findElement(By.xpath("//*[@id='content']/dl/dd[4]")).getText());
		} catch (Exception e) {
			fail("No s'ha pogut obtenir la nova versió de la definició de procés");
		}
		assertTrue("No s'ha podut eliminar la versió de procés", versioNova < versioActual);
//		eliminar(false);
	}
	
	@Test
	public void h_descarregarRecursos() {
		carregarUrlConfiguracio();
		seleccionarDefinicioProces(nomDefProc);
		// Accedir a la fitxa de recursos
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/recursLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("defproces/recursos/descarregar/1_recursos.png");
		
		// descarregar recursos
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[1][text() ='gpd.xml']", "No existeix el recurs gpd.xml a descarregar");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[1][text() ='processimage.jpg']", "No existeix el recurs processimage.jpg a descarregar");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[1][text() ='processdefinition.xml']", "No existeix el recurs processdefinition.xml a descarregar");
		
		downloadFileHash("//*[@id='registre']/tbody/tr/td[1][text() ='gpd.xml']/../td[2]/a",
				carregarPropietat("defproc.recurs.gpd.hash", "Hash del gpd.xml no configurat al fitxer de properties"), 
				"gpd.xml");
		downloadFileHash("//*[@id='registre']/tbody/tr/td[1][text() ='processimage.jpg']/../td[2]/a", 
				carregarPropietat("defproc.recurs.processimage.hash", "Hash del processimage.jpg no configurat al fitxer de properties"), 
				"processimage.jpg");
		downloadFileHash("//*[@id='registre']/tbody/tr/td[1][text() ='processdefinition.xml']/../td[2]/a", 
				carregarPropietat("defproc.recurs.processdefinition.hash", "Hash del processdefinition.xml no configurat al fitxer de properties"), 
				"processdefinition.xml");
	}
	
	@Test
	public void i_importarDefProc() {
		carregarUrlConfiguracio();
		importarDadesDefPro(nomDefProc, carregarPropietatPath("defproc.import.dades.path", "El path a la exportació de dades de la definició de procés no configurat al fitxer de properties"));
		// Comprovar dades importades:
		seleccionarDefinicioProces(nomDefProc);
		// variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'var1') and contains(td[2],'var1') and contains(td[3],'STRING')]", "La variable var1 no s'ha importat");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'var2') and contains(td[2],'var2') and contains(td[3],'INTEGER')]", "La variable var2 no s'ha importat");
		// documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'doc1') and contains(td[2],'doc1')]", "El document doc1 no s'ha importat");
		// terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'ter1') and contains(td[2],'ter1') and contains(td[3],'4 di')]", "El termini ter1 no s'ha importat");
		// agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'agr1') and contains(td[2],'agr1') and contains(td[3]/form/button,'1')]", "La agrupacio agr1 no s'ha importat");
		// accions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/accioLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'acc1') and contains(td[2],'acc1') and contains(td[3],'action')]", "La acció acc1 no s'ha importat");
		// tasques
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'tasca1') and contains(td[2],'tasca1') and contains(td[3]/form/button,'2') and contains(td[4]/form/button,'0') and contains(td[5]/form/button,'0')]", "La tasca1 no s'ha importat correctament");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'tasca2') and contains(td[2],'tasca2') and contains(td[3]/form/button,'0') and contains(td[4]/form/button,'1') and contains(td[5]/form/button,'1')]", "La tasca2 no s'ha importat correctament");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'tasca3') and contains(td[2],'tasca3') and contains(td[3]/form/button,'1') and contains(td[4]/form/button,'0') and contains(td[5]/form/button,'0')]", "La tasca3 no s'ha importat correctament");
	}
	
	@Test
	public void j_exportarDefProc() {
		carregarUrlConfiguracio();
		seleccionarDefinicioProces(nomDefProc);
		// Export
//		postDownloadFileHash("//*[@id='content']/div[2]/form[1]", 
//				carregarPropietat("defproc.export.dades.hash", "Hash de la exportació de la definició de procés no configurat al fitxer de properties"));
		postDownloadFile("//*[@id='content']/div[2]/form[1]");
	}
	
	@Test
	public void k_eliminarDefProc() {
		carregarUrlConfiguracio();
		eliminar(false);
	}
	
	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		try { Thread.sleep(5000); }catch (Exception ex) {}
		eliminarEntorn(entorn);
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
			try {Thread.sleep(3000);}catch(Exception ex){}
			screenshotHelper.saveScreenshot("defproces/2_elimDefProc.png");
		} else 	{
			if (!continuar)
				fail("La definició de procés no existeix");
		}
	}
	
	// Inicialitzacions
	
	protected void crearEntornTest(String entorn, String titolEntorn, String usuari) {
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
	
	protected void crearTipusExpedient(String nom, String codi) {
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
