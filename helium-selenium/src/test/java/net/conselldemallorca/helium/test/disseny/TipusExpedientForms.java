package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientForms extends BaseTest {
	
							//TEX.12 - Integració amb forms (no provar)
								//TEX.12.1 - Activar /desactivar
								//TEX.12.2 - Modificar
	
	String entorn 		= carregarPropietat("tipexp.integracio.forms.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.integracio.forms.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	String nomTipusExp	= carregarPropietat("tipexp.integracio.forms.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.integracio.forms.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");

	String urlDadesProves = "http://proves.integracio.forms.com/selenium/";
	String usuDadesProves = "UsuariProvesIF";
	String pasDadesProves = "PasswrdProvesIF";
	
	// XPATHS
	String pestanyaForms	= "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/formext.html')]";
	String botoGuardarCanvis	= "//*[@id='command']//div[@class='buttonHolder']/button[text() = 'Guardar']";
	
	@Test
	public void a1_inicialitzacio() {
		
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	
		crearTipusExpedient(nomTipusExp, codTipusExp);		
		
		assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
	}
	
	@Test
	public void b1_activar_forms() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaForms)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/b1_1_activar_forms-pipella_forms.png");
		
		driver.findElement(By.id("actiu0")).click();
		driver.findElement(By.id("url0")).sendKeys(urlDadesProves);
		driver.findElement(By.id("usuari0")).sendKeys(usuDadesProves);
		driver.findElement(By.id("contrasenya0")).sendKeys(pasDadesProves);
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/b1_2_activar_forms-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardarCanvis)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/b1_3_activar_forms-resultat_insercio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al activar la integració amb forms per el tipus d´expedient "+codTipusExp+".");
		
		//Tornam a carregar la pestanya per comprobar que les dades s´han guardat
		
		driver.findElement(By.xpath(pestanyaForms)).click();
		
		if (!checkboxSelected("//*[@id='actiu0']", true)) {fail("El check 'Activar' de la integració amb forms del tipus d´expedient hauria de estar seleccionat"); }
		if (!"http://proves.integracio.forms.com/selenium/".equals(driver.findElement(By.id("url0")).getAttribute("value"))) { fail("La URL guardada no coincideix amb la introduida o no s´ha guardat."); }
		if (!"UsuariProvesIF".equals(driver.findElement(By.id("usuari0")).getAttribute("value"))) { fail("L'usuari guardat no coincideix amb la introduida o no s´ha guardat."); }
		if (!"PasswrdProvesIF".equals(driver.findElement(By.id("contrasenya0")).getAttribute("value"))) { fail("La contrasenya guardada no coincideix amb la introduida o no s´ha guardat."); }
	}
	
	@Test
	public void c1_modificar_forms() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaForms)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/c1_1_modificar_forms-pipella_forms.png");
		
		driver.findElement(By.id("url0")).sendKeys("mod");
		
		driver.findElement(By.id("usuari0")).sendKeys("mod");
		
		driver.findElement(By.id("contrasenya0")).sendKeys("mod");
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/c1_2_modificar_forms-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardarCanvis)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/c1_3_modificar_forms-resultat_modificacio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar les dades de la integració amb forms per el tipus d´expedient "+codTipusExp+".");
		
		String urlDadesProves_m = urlDadesProves+"mod";
		String usuDadesProves_m = usuDadesProves+"mod";
		String pasDadesProves_m = pasDadesProves+"mod";
		
		if (!urlDadesProves_m.equals(driver.findElement(By.id("url0")).getAttribute("value"))) { fail("La URL guardada no coincideix amb la introduida o no s´ha guardat."); }
		if (!usuDadesProves_m.equals(driver.findElement(By.id("usuari0")).getAttribute("value"))) { fail("L'usuari guardat no coincideix amb la introduida o no s´ha guardat."); }
		if (!pasDadesProves_m.equals(driver.findElement(By.id("contrasenya0")).getAttribute("value"))) { fail("La contrasenya guardada no coincideix amb la introduida o no s´ha guardat."); }
	}
	
	@Test
	public void d1_desactivar_forms() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaForms)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/d1_1_desactivar_forms-pipella_forms.png");
		
		driver.findElement(By.id("actiu0")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/d1_2_desactivar_forms-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardarCanvis)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/forms/d1_3_desactivar_forms-resultat_desactivacio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al activar la integració amb forms per el tipus d´expedient "+codTipusExp+".");
		
		//Tornam a carregar la pestanya per comprobar que les dades s´han guardat
		
		driver.findElement(By.xpath(pestanyaForms)).click();
		
		if (checkboxSelected("//*[@id='actiu0']", true)) {fail("El check 'Activar' de la integració amb forms del tipus d´expedient NO hauria de estar seleccionat"); }
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
}