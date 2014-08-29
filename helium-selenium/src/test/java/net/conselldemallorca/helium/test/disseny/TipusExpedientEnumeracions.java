package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientEnumeracions extends BaseTest {

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
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "WRITE", "READ", "DELETE");
	}
	
	@Test
	public void b1_crear_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		driver.findElement(By.xpath(botoNovaEnumeracio)).click();
		
		driver.findElement(By.id("codi0")).sendKeys(codiEnumeracio);
		
		driver.findElement(By.id("nom0")).sendKeys(nomEnumeracio);
		
		driver.findElement(By.xpath(botoGuardaEnumeracio)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear l'enumeracio "+codiEnumeracio+" del tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void c1_modificar_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		driver.findElement(By.xpath(filaEnumeracioLlista)).click();
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codiEnumeracioMod);
		
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nomEnumeracioMod);
		
		driver.findElement(By.xpath(botoModificaEnumeracio)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut modificar l'enumeracio "+codiEnumeracio+" del tipus d´expedient "+codTipusExp+".");
		
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
		
		//driver.findElement(By.xpath(filaEnumeracioLlistaM)).click();
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();

		driver.findElement(By.id("codi0")).sendKeys("vEnum1");
		
		driver.findElement(By.id("nom0")).sendKeys("11111111");
		
		driver.findElement(By.xpath(botoGuardarValorsEnum)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
		
		driver.findElement(By.id("codi0")).sendKeys("vEnum2");
		
		driver.findElement(By.id("nom0")).sendKeys("2222222");
		
		driver.findElement(By.xpath(botoGuardarValorsEnum)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void e1_modificar_valors_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		//driver.findElement(By.xpath(filaEnumeracioLlistaM)).click();
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();
		
		driver.findElement(By.xpath(filaValorEnumeracioLlista)).click();
		
		driver.findElement(By.id("nom0")).sendKeys(" mod");
		
		driver.findElement(By.xpath(botoModifValorsEnum)).click();
		
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
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();
		
		sortTable("registre", 1, 2);
		sortTable("registre", 1, 2);
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
		
		driver.findElement(By.xpath(botoValorsEnumeracio)).click();
		
		while (existeixElement(botoEliminarValorEnum)) {
			driver.findElement(By.xpath(botoEliminarValorEnum)).click();
			if (isAlertPresent()) {	acceptarAlerta(); }
			existeixElementAssert("//*[@class='missatgesOk']", "Error al eliminar el valor per l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
		}
	}
	
	@Test
	public void h1_eliminar_enumeracio() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaEnumeracions)).click();
		
		while (existeixElement(botoEliminarEnum)) {
			driver.findElement(By.xpath(botoEliminarEnum)).click();
			if (isAlertPresent()) {	acceptarAlerta(); }
			existeixElementAssert("//*[@class='missatgesOk']", "Error al eliminar l'enumeracio vEnum1 per el tipus d´expedient "+codTipusExp+".");
		}
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
}
