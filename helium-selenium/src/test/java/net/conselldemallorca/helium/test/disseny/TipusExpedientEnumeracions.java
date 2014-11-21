package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientEnumeracions extends BaseTest {

							//TEX.6 - Enumeracions
								//TEX.6.1 - Crear enumeracio
								//TEX.6.2 - Modificar enumeració
								//TEX.6.3 - Valors
									//TEX.6.3.1 - Assignar valors a enumeracio
									//TEX.6.3.2 - Modificar valors de enumeracio
									//TEX.6.3.3 - Ordenar valors enumeracio
									//TEX.6.3.4 - Importar valors (Implementat a la prova de TipusExpedient.java (e2_comprovar_desplegament_exportacio))
									//TEX.6.3.5 - Eliminar valor de enumeració
								//TEX.6.4 - Eliminar enumeracio	
	
	String entorn 		= carregarPropietat("tipexp.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");

	//XPATHS DE ENUMERACIONS
	String codiEnumeracio 		 = "codEnum1";
	String nomEnumeracio 		 = "Nom Enum 1";
	String codiEnumeracioMod	 = codiEnumeracio + " mod";
	String nomEnumeracioMod		 = nomEnumeracio + " mod";
	
	String pestanyaEnumeracions  = "//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]";
	String botoNovaEnumeracio	 = "//*[@id='content']/form/button[contains(text(), 'Nova enum')]";
	
	String botoGuardaEnumeracio	  = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Crear']";
	String botoModificaEnumeracio = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']";
	
	String botoValorsEnumeracio	 = "//*[@id='registre']/tbody/tr/td/form[contains(@action, 'enumeracioValors.html')]/button";
	String filaEnumeracioLlista  = "//*[@id='registre']/tbody/tr[contains(td,'"+codiEnumeracio+"')]";
	String filaEnumeracioLlistaM = "//*[@id='registre']/tbody/tr[contains(td,'"+codiEnumeracioMod+"')]";
	
	String botoGuardarValorsEnum = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Afegir']";
	String filaValorEnumeracioLlista  = "//*[@id='registre']/tbody/tr/td/a[contains(@href,'enumeracioValorsForm.html')]";
	
	String botoModifValorsEnum	 = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']";
	String botoEliminarValorEnum = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/enumeracioValorsEsborrar.html')]";
	String botoEliminarEnum 	 = "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/enumeracioEsborrar.html')]";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "WRITE", "MANAGE", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "WRITE", "READ", "DELETE", "ADMINISTRATION");
	}
	
	@Test
	public void b1_crear_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/b1_1_crear-estat_inicial.png");
		
		driver.findElement(By.xpath(botoNovaEnumeracio)).click();
		
		driver.findElement(By.id("codi0")).sendKeys(codiEnumeracio);
		
		driver.findElement(By.id("nom0")).sendKeys(nomEnumeracio);
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/b1_2_crear-dades.png");
		
		driver.findElement(By.xpath(botoGuardaEnumeracio)).click();
				
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/b1_3_crear-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear l'enumeracio "+codiEnumeracio+" del tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void c1_modificar_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		driver.findElement(By.xpath(filaEnumeracioLlista)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/c1_1_modificar-estat_inicial.png");
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codiEnumeracioMod);
		
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nomEnumeracioMod);
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/c1_2_modificar-dades_cambiades.png");
		
		driver.findElement(By.xpath(botoModificaEnumeracio)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/c1_3_modificar-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut modificar l'enumeracio "+codiEnumeracio+" del tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/c1_4_modificar-comprobacio_canvis.png");
		
		driver.findElement(By.xpath(filaEnumeracioLlistaM)).click();
		
		if (!codiEnumeracioMod.equals(driver.findElement(By.id("codi0")).getAttribute("value"))) { fail("El codi de la enumeracio "+codiEnumeracioMod+" del tipus d´expedient "+codTipusExp+" no s´ha modificat!"); }
		
		if (!nomEnumeracioMod.equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El nom de la enumeracio "+codiEnumeracioMod+" del tipus d´expedient "+codTipusExp+" no s´ha modificat!"); }
	}
	
	@Test
	public void d1_asignar_valors_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_1_afegir_valors-pestanya_enumeracions.png");
		
		//driver.findElement(By.xpath(filaEnumeracioLlistaM)).click();
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();

		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_2_afegir_valors-valors_inici.png");
		
		driver.findElement(By.id("codi0")).sendKeys("vEnum1");
		
		driver.findElement(By.id("nom0")).sendKeys("11111111");
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_3_afegir_valors-dades_primer.png");
		
		driver.findElement(By.xpath(botoGuardarValorsEnum)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_4_afegir_valors-resultat_primer.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.id("codi0")).sendKeys("vEnum2");
		
		driver.findElement(By.id("nom0")).sendKeys("2222222");
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_5_afegir_valors-dades_segon.png");
		
		driver.findElement(By.xpath(botoGuardarValorsEnum)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/d1_6_afegir_valors-resultat_segon.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void e1_modificar_valors_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/e1_1_modificar_valors-pestanya_enumeracions.png");
		
		//driver.findElement(By.xpath(filaEnumeracioLlistaM)).click();
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/e1_2_modificar_valors-llistat_valors.png");
		
		driver.findElement(By.xpath(filaValorEnumeracioLlista)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/e1_3_modificar_valors-seleccionat.png");
		
		driver.findElement(By.id("nom0")).sendKeys(" mod");
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/e1_4_modificar_valors-modificat.png");
		
		driver.findElement(By.xpath(botoModifValorsEnum)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/e1_5_modificar_valors-resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
		
		//Tornam a entrar dins l´enumerat a comprovar que realment s´ha canviat el valor
		driver.findElement(By.xpath(filaValorEnumeracioLlista)).click();
		
		if (!"11111111 mod".equals(driver.findElement(By.id("nom0")).getAttribute("value")))  { fail("El titol del valor de la enumeracio "+codiEnumeracioMod+" del tipus d´expedient "+codTipusExp+" no s´ha modificat!"); }
	}
	
	@Test
	public void f1_ordenar_valors_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/f1_1_ordenar_valors-pipella_enumeracions.png");
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/f1_2_ordenar_valors-valors_inicials.png");
		
		sortTable("registre", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/f1_3_ordenar_valors-ordenat_1.png");
		
		sortTable("registre", 1, 2);
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/f1_4_ordenar_valors-ordenat_2.png");
	}
	
	// -------------------------
	// Test de importar valor ja incluit a la prova de TipusExpedient.java (e2_comprovar_desplegament_exportacio)
	// -------------------------
	
	@Test
	public void g1_eliminar_valors_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/g1_1_eliminar_valors-pipella_enumeracions.png");
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();

		int contadorScreeshots = 1;
		while (existeixElement(botoEliminarValorEnum)) {
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/g1_2_"+contadorScreeshots+"_eliminar_valors-abans.png");
			
			driver.findElement(By.xpath(botoEliminarValorEnum)).click();
			
			if (isAlertPresent()) {	acceptarAlerta(); }
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/g1_2_"+contadorScreeshots+"_eliminar_valors-despres.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al eliminar el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
			
			contadorScreeshots++;
		}
	}
	
	@Test
	public void h1_eliminar_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/h1_1_eliminar_enumeracions-estat_inicial.png");
		
		int contadorScreeshots = 1;
		while (existeixElement(botoEliminarEnum)) {
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/h1_2_"+contadorScreeshots+"_eliminar_enum-abans.png");
			
			driver.findElement(By.xpath(botoEliminarEnum)).click();
			
			if (isAlertPresent()) {	acceptarAlerta(); }
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracions/h1_2_"+contadorScreeshots+"_eliminar_enum-despres.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al eliminar l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
			
			contadorScreeshots++;
		}
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
}
